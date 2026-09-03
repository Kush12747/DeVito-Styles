package learn.DeVitoStyles.domain.checkout;

import com.stripe.model.billingportal.Session;
import learn.DeVitoStyles.models.Checkout.Order;
import learn.DeVitoStyles.models.Checkout.OrderItem;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StripeService {


    public String createCheckoutSession(List<OrderItem> orderItems, int orderId) {
        return "";
    }
}
