package learn.DeVitoStyles.domain.checkout;

import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.RefundCreateParams;
import learn.DeVitoStyles.models.Checkout.OrderItem;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class StripeService {

    @Value("${app.frontend-url}")
    private String frontEndUrl;

    @Value("${app.backend-url}")
    private String backEndUrl;

    public String createCheckoutSession(List<OrderItem> orderItems, int orderId) throws StripeException {


        // Convert our OrderItems into Stripe format.
        List<SessionCreateParams.LineItem> lineItems = buildStripeLineItems(orderItems);

        // Build the Stripe checkout request.
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)              // This tells Stripe a one time payment
                .addAllLineItem(lineItems)                              // What products the customer is buying
                .setSuccessUrl(frontEndUrl + "/checkout/success")       // Stripe sends after successful payment
                .setCancelUrl(frontEndUrl + "/checkout/cancel")         // Stripe sends after payment cancels
                .putMetadata("orderId", String.valueOf(orderId))        // Store Id inside Stripe, usefull for webhooks
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

    public Event verifyWebHook(String payload, String signature) throws StripeException {
        return null;
    }

    private List<SessionCreateParams.LineItem> buildStripeLineItems(List<OrderItem> orderItems) {

        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();

        /*
        * Convert every orderItems from our database model
        * into a Stripe LineItem.
        */
        for (OrderItem item : orderItems) {

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
            long amountInCents = item.getUnitPrice()
                    .multiply(
                            BigDecimal.valueOf(100)
                    )
                    .longValue();

            /*
            * Create Stripe session information.
            *
            * This is what the customer sees
            * on the Stripe checkout page.
            */
            SessionCreateParams.LineItem.PriceData.ProductData productData =
                    SessionCreateParams.LineItem.PriceData.ProductData
                            .builder()
                            .setName(item.getProductName())
                            .build();

            /*
            * Create the price information.
            *
            * Example:
            *
            * Product:
            * Hair Pomade
            *
            * Price:
            * $20
            */
            SessionCreateParams.LineItem.PriceData priceData =
                    SessionCreateParams.LineItem.PriceData
                            .builder()
                            .setCurrency("usd")
                            .setUnitAmount(amountInCents)
                            .setProductData(productData)
                            .build();

            // Create the actual Stripe LineItem
            SessionCreateParams.LineItem lineItem =
                    SessionCreateParams.LineItem
                            .builder()
                            .setQuantity((long)item.getQuantity())
                            .setPriceData(priceData)
                            .build();

            lineItems.add(lineItem);
        }

        return lineItems;
    }
}
