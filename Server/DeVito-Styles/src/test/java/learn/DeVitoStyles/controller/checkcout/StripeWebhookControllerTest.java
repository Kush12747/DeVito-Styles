package learn.DeVitoStyles.controller.checkcout;

import com.stripe.exception.ApiConnectionException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import learn.DeVitoStyles.domain.checkout.PaymentService;
import learn.DeVitoStyles.domain.checkout.StripeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class StripeWebhookControllerTest {

    private static final String PAYLOAD =
            "{\"id\":\"evt_123\",\"type\":\"checkout.session.completed\",\"data\":{\"object\":{\"note\":\"café\"}}}";
    private static final String SIGNATURE = "t=1700000000,v1=abc123";

    @Mock
    private StripeService stripeService;

    @Mock
    private PaymentService paymentService;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new StripeWebhookController(stripeService, paymentService)).build();
    }

    @Test
    void handleWebhook_whenSignatureValid_returns200AndProcessesEvent() throws Exception {
        Event event = new Event();
        event.setId("evt_123");
        event.setType("checkout.session.completed");
        when(stripeService.verifyWebHook(PAYLOAD, SIGNATURE)).thenReturn(event);

        mvc.perform(post("/api/stripe/webhook")
                        .header("Stripe-Signature", SIGNATURE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PAYLOAD.getBytes(StandardCharsets.UTF_8)))
                .andExpect(status().isOk())
                .andExpect(content().string("Webhook processed."));

        // Exact raw payload (byte-for-byte, UTF-8 decoded) must reach signature verification.
        verify(stripeService).verifyWebHook(PAYLOAD, SIGNATURE);
        verify(paymentService).processStripeEvent(event);
    }

    @Test
    void handleWebhook_whenSignatureInvalid_returns400AndDoesNotProcess() throws Exception {
        when(stripeService.verifyWebHook(PAYLOAD, SIGNATURE))
                .thenThrow(new SignatureVerificationException("No signatures found matching", SIGNATURE));

        mvc.perform(post("/api/stripe/webhook")
                        .header("Stripe-Signature", SIGNATURE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PAYLOAD.getBytes(StandardCharsets.UTF_8)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid Stripe signature."));

        verifyNoInteractions(paymentService);
    }

    @Test
    void handleWebhook_whenProcessingThrows_returns500SoStripeRetries() throws Exception {
        Event event = new Event();
        event.setId("evt_123");
        when(stripeService.verifyWebHook(PAYLOAD, SIGNATURE)).thenReturn(event);
        doThrow(new RuntimeException("DB down")).when(paymentService).processStripeEvent(event);

        mvc.perform(post("/api/stripe/webhook")
                        .header("Stripe-Signature", SIGNATURE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PAYLOAD.getBytes(StandardCharsets.UTF_8)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Webhook failed."));

        verify(paymentService).processStripeEvent(event);
    }

    @Test
    void handleWebhook_whenVerifyThrowsNonSignatureStripeException_returns500() throws Exception {
        when(stripeService.verifyWebHook(PAYLOAD, SIGNATURE))
                .thenThrow(new ApiConnectionException("unexpected"));

        mvc.perform(post("/api/stripe/webhook")
                        .header("Stripe-Signature", SIGNATURE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PAYLOAD.getBytes(StandardCharsets.UTF_8)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Webhook failed."));

        verifyNoInteractions(paymentService);
    }

    @Test
    void handleWebhook_whenSignatureHeaderMissing_returns400WithoutCallingServices() throws Exception {
        mvc.perform(post("/api/stripe/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PAYLOAD.getBytes(StandardCharsets.UTF_8)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(stripeService, paymentService);
    }
}
