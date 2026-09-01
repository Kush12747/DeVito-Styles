package learn.DeVitoStyles.data.interfaces.orders;

import learn.DeVitoStyles.models.Checkout.Payment;
import learn.DeVitoStyles.models.Checkout.PaymentStatus;

public interface PaymentRepository {

    Payment add(Payment payment);

    Payment findByOrderId(int orderId);

    Payment findByPaymentIntentId(String paymentIntentId);

    boolean updateStatus(int paymentId, PaymentStatus paymentStatus);
}
