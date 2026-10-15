package learn.DeVitoStyles.domain.checkout;

import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.RefundCreateParams;
import learn.DeVitoStyles.models.Checkout.Order;
import learn.DeVitoStyles.models.Checkout.OrderItem;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class StripeService {

    @Value("${app.frontend-url}")
    private String frontEndUrl;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    public String createCheckoutSession(Order order, List<OrderItem> orderItems) throws StripeException {

        String orderId = String.valueOf(order.getOrderId());

        // Convert our OrderItems into Stripe format.
        List<SessionCreateParams.LineItem> lineItems = buildStripeLineItems(orderItems);

        // Stripe only charges for the line items it is given, so tax needs its own line.
        if (order.getTaxAmount() != null && order.getTaxAmount().signum() > 0) {
            lineItems.add(buildLineItem("Sales Tax", order.getTaxAmount(), 1));
        }

        // Build the Stripe checkout request.
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)              // This tells Stripe a one time payment
                .addAllLineItem(lineItems)                              // What products the customer is buying
                .setSuccessUrl(frontEndUrl + "/checkout/success?order=" + order.getOrderNumber()) // Stripe sends after successful payment
                .setCancelUrl(frontEndUrl + "/checkout/cancel?order=" + order.getOrderNumber())   // Stripe sends after payment cancels
                .setClientReferenceId(orderId)
                .putMetadata("orderId", orderId)                        // Store Id inside Stripe, usefull for webhooks
                .setPaymentIntentData(                                  // payment_intent.* webhooks need the order id too
                        SessionCreateParams.PaymentIntentData.builder()
                                .putMetadata("orderId", orderId)
                                .build()
                )
                .build();

        Session session = Session.create(params);                       // Create the checkout session with Stripe

        return session.getUrl();
    }

    public PaymentIntent retrievePaymentIntent(String paymentIntentId) throws StripeException {
        return PaymentIntent.retrieve(paymentIntentId);
    }

    public Session retrieveSession(String sessionId) throws StripeException {
        return Session.retrieve(sessionId);
    }

    public Refund refundPayment(String paymentIntentId) throws StripeException {

        RefundCreateParams params =
                RefundCreateParams.builder()
                        .setPaymentIntent(paymentIntentId)
                        .build();

        return Refund.create(params);
    }

    /*
     * Confirms the webhook really came from Stripe by checking the
     * Stripe-Signature header against our webhook signing secret.
     *
     * Throws SignatureVerificationException if the signature is invalid.
     */
    public Event verifyWebHook(String payload, String signature) throws StripeException {
        return Webhook.constructEvent(payload, signature, webhookSecret);
    }

    private List<SessionCreateParams.LineItem> buildStripeLineItems(List<OrderItem> orderItems) {

        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();

        /*
        * Convert every orderItems from our database model
        * into a Stripe LineItem.
        */
        for (OrderItem item : orderItems) {
            lineItems.add(buildLineItem(item.getProductName(), item.getUnitPrice(), item.getQuantity()));
        }

        return lineItems;
    }

    private SessionCreateParams.LineItem buildLineItem(String name, BigDecimal unitPrice, int quantity) {

        /*
         * Stripe stores money as cents.
         *
         * Example:
         *
         * Database:
         * $25.00
         *
         * Stripe:
         * 2500
         */
        long amountInCents = unitPrice
                .multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();

        /*
         * Create Stripe session information.
         *
         * This is what the customer sees
         * on the Stripe checkout page.
         */
        SessionCreateParams.LineItem.PriceData.ProductData productData =
                SessionCreateParams.LineItem.PriceData.ProductData
                        .builder()
                        .setName(name)
                        .build();

        // Create the price information.
        SessionCreateParams.LineItem.PriceData priceData =
                SessionCreateParams.LineItem.PriceData
                        .builder()
                        .setCurrency("usd")
                        .setUnitAmount(amountInCents)
                        .setProductData(productData)
                        .build();

        // Create the actual Stripe LineItem
        return SessionCreateParams.LineItem
                .builder()
                .setQuantity((long) quantity)
                .setPriceData(priceData)
                .build();
    }
}
