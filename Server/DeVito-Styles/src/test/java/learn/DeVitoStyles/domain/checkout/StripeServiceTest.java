package learn.DeVitoStyles.domain.checkout;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import learn.DeVitoStyles.models.Checkout.Order;
import learn.DeVitoStyles.models.Checkout.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StripeServiceTest {

    private static final String FRONTEND_URL = "http://localhost:5173";
    private static final String WEBHOOK_SECRET = "whsec_test_secret";
    private static final String SESSION_URL = "https://checkout.stripe.com/c/pay/cs_test_abc";

    private StripeService stripeService;

    @BeforeEach
    void setUp() {
        stripeService = new StripeService();
        ReflectionTestUtils.setField(stripeService, "frontEndUrl", FRONTEND_URL);
        ReflectionTestUtils.setField(stripeService, "webhookSecret", WEBHOOK_SECRET);
    }

    // ---------- verifyWebHook ----------

    @Test
    void verifyWebHook_whenSignatureValid_returnsParsedEvent() throws Exception {
        String payload = eventPayload();
        long timestamp = System.currentTimeMillis() / 1000L;
        String header = "t=" + timestamp + ",v1=" + hmacSha256Hex(timestamp + "." + payload, WEBHOOK_SECRET);

        Event event = stripeService.verifyWebHook(payload, header);

        assertEquals("evt_test_123", event.getId());
        assertEquals("checkout.session.completed", event.getType());
    }

    @Test
    void verifyWebHook_whenSignedWithWrongSecret_throwsSignatureVerificationException() throws Exception {
        String payload = eventPayload();
        long timestamp = System.currentTimeMillis() / 1000L;
        String header = "t=" + timestamp + ",v1=" + hmacSha256Hex(timestamp + "." + payload, "whsec_wrong");

        assertThrows(SignatureVerificationException.class,
                () -> stripeService.verifyWebHook(payload, header));
    }

    @Test
    void verifyWebHook_whenPayloadTampered_throwsSignatureVerificationException() throws Exception {
        String payload = eventPayload();
        long timestamp = System.currentTimeMillis() / 1000L;
        String header = "t=" + timestamp + ",v1=" + hmacSha256Hex(timestamp + "." + payload, WEBHOOK_SECRET);
        String tampered = payload.replace("evt_test_123", "evt_test_999");

        assertThrows(SignatureVerificationException.class,
                () -> stripeService.verifyWebHook(tampered, header));
    }

    @Test
    void verifyWebHook_whenHeaderMalformed_throwsSignatureVerificationException() {
        assertThrows(SignatureVerificationException.class,
                () -> stripeService.verifyWebHook(eventPayload(), "not-a-signature"));
    }

    // ---------- createCheckoutSession ----------

    @Test
    void createCheckoutSession_buildsLineItemsInCentsWithTaxAndReturnsUrl() throws StripeException {
        Order order = order(new BigDecimal("2.84"));
        List<OrderItem> items = List.of(
                orderItem("Pomade", "19.99", 2),
                orderItem("Comb", "5.50", 1)
        );

        SessionCreateParams params;
        String url;
        try (MockedStatic<Session> sessionStatic = mockStatic(Session.class)) {
            Session session = mock(Session.class);
            when(session.getUrl()).thenReturn(SESSION_URL);
            ArgumentCaptor<SessionCreateParams> captor = ArgumentCaptor.forClass(SessionCreateParams.class);
            sessionStatic.when(() -> Session.create(any(SessionCreateParams.class))).thenReturn(session);

            url = stripeService.createCheckoutSession(order, items);

            sessionStatic.verify(() -> Session.create(captor.capture()));
            params = captor.getValue();
        }

        assertEquals(SESSION_URL, url);
        assertEquals(SessionCreateParams.Mode.PAYMENT, params.getMode());

        List<SessionCreateParams.LineItem> lineItems = params.getLineItems();
        assertEquals(3, lineItems.size());
        assertLineItem(lineItems.get(0), "Pomade", 1999L, 2L);
        assertLineItem(lineItems.get(1), "Comb", 550L, 1L);
        assertLineItem(lineItems.get(2), "Sales Tax", 284L, 1L);
        assertTrue(lineItems.stream().allMatch(li -> "usd".equals(li.getPriceData().getCurrency())));

        assertEquals(FRONTEND_URL + "/checkout/success?order=DEV-TEST-ABC123", params.getSuccessUrl());
        assertEquals(FRONTEND_URL + "/checkout/cancel?order=DEV-TEST-ABC123", params.getCancelUrl());
        assertEquals("42", params.getClientReferenceId());
        assertEquals("42", params.getMetadata().get("orderId"));
        assertEquals("42", params.getPaymentIntentData().getMetadata().get("orderId"));
    }

    @Test
    void createCheckoutSession_whenTaxZero_omitsSalesTaxLineItem() throws StripeException {
        SessionCreateParams params = captureParams(order(BigDecimal.ZERO), List.of(orderItem("Pomade", "20.00", 1)));

        assertEquals(1, params.getLineItems().size());
        assertLineItem(params.getLineItems().get(0), "Pomade", 2000L, 1L);
    }

    @Test
    void createCheckoutSession_whenTaxNull_omitsSalesTaxLineItem() throws StripeException {
        SessionCreateParams params = captureParams(order(null), List.of(orderItem("Pomade", "20.00", 1)));

        assertEquals(1, params.getLineItems().size());
        assertTrue(params.getLineItems().stream()
                .noneMatch(li -> "Sales Tax".equals(li.getPriceData().getProductData().getName())));
    }

    @Test
    void createCheckoutSession_roundsFractionalCentsHalfUp() throws StripeException {
        SessionCreateParams params = captureParams(order(BigDecimal.ZERO), List.of(orderItem("Oil", "10.005", 1)));

        assertLineItem(params.getLineItems().get(0), "Oil", 1001L, 1L);
    }

    @Test
    void createCheckoutSession_whenStripeFails_propagatesException() {
        try (MockedStatic<Session> sessionStatic = mockStatic(Session.class)) {
            StripeException error = new com.stripe.exception.ApiConnectionException("down");
            sessionStatic.when(() -> Session.create(any(SessionCreateParams.class))).thenThrow(error);

            StripeException thrown = assertThrows(StripeException.class,
                    () -> stripeService.createCheckoutSession(order(BigDecimal.ZERO),
                            List.of(orderItem("Pomade", "20.00", 1))));
            assertSame(error, thrown);
        }
    }

    // ---------- helpers ----------

    private SessionCreateParams captureParams(Order order, List<OrderItem> items) throws StripeException {
        try (MockedStatic<Session> sessionStatic = mockStatic(Session.class)) {
            Session session = mock(Session.class);
            when(session.getUrl()).thenReturn(SESSION_URL);
            sessionStatic.when(() -> Session.create(any(SessionCreateParams.class))).thenReturn(session);

            assertEquals(SESSION_URL, stripeService.createCheckoutSession(order, items));

            ArgumentCaptor<SessionCreateParams> captor = ArgumentCaptor.forClass(SessionCreateParams.class);
            sessionStatic.verify(() -> Session.create(captor.capture()));
            return captor.getValue();
        }
    }

    private static void assertLineItem(SessionCreateParams.LineItem lineItem, String name, long cents, long qty) {
        assertEquals(name, lineItem.getPriceData().getProductData().getName());
        assertEquals(cents, lineItem.getPriceData().getUnitAmount());
        assertEquals(qty, lineItem.getQuantity());
    }

    private static Order order(BigDecimal tax) {
        Order order = new Order();
        order.setOrderId(42);
        order.setOrderNumber("DEV-TEST-ABC123");
        order.setTaxAmount(tax);
        return order;
    }

    private static OrderItem orderItem(String name, String unitPrice, int quantity) {
        OrderItem item = new OrderItem();
        item.setProductName(name);
        item.setUnitPrice(new BigDecimal(unitPrice));
        item.setQuantity(quantity);
        return item;
    }

    private static String eventPayload() {
        return "{\"id\":\"evt_test_123\",\"object\":\"event\",\"type\":\"checkout.session.completed\","
                + "\"api_version\":\"2024-06-20\",\"created\":1700000000,"
                + "\"data\":{\"object\":{\"id\":\"cs_test_123\",\"object\":\"checkout.session\"}}}";
    }

    private static String hmacSha256Hex(String data, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return HexFormat.of().formatHex(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }
}
