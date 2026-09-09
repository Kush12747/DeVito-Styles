package learn.DeVitoStyles.controller.checkcout;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import jakarta.servlet.http.HttpServletRequest;
import learn.DeVitoStyles.domain.checkout.CheckoutService;
import learn.DeVitoStyles.domain.checkout.PaymentService;
import learn.DeVitoStyles.domain.checkout.StripeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/stripe")
public class StripeWebhookController {

    private final StripeService stripeService;
    private final PaymentService paymentService;

    public StripeWebhookController(StripeService stripeService, PaymentService paymentService) {
        this.stripeService = stripeService;
        this.paymentService = paymentService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(HttpServletRequest request,
                                                @RequestHeader("Stripe-Signature") String signature) {
        try {

            /*
             * Read the raw JSON payload exactly as Stripe sent it.
             *
             * We cannot deserialize it first because Stripe's
             * signature verification requires the original payload.
             */
            String payload = new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            Event event = stripeService.verifyWebHook(payload, signature);

            /*
             * Pass the verified event to the business layer.
             */
            paymentService.processStripeEvent(event);


            return ResponseEntity.ok("Webhook processed.");

        }
        catch (SignatureVerificationException ex) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Invalid Stripe signature.");
        }
        catch (IOException ex) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Unable to read request.");
        }
        catch (Exception ex) {

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Webhook failed.");
        }

    }

}