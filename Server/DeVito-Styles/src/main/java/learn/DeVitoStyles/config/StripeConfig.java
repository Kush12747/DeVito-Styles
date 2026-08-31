package learn.DeVitoStyles.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @PostConstruct
    public void init() {

        if (stripeSecretKey == null || stripeSecretKey.isEmpty()) {
            throw new IllegalArgumentException("Stripe secret key is missing");
        }
        Stripe.apiKey = stripeSecretKey; //Set up Stripe key
    }
}
