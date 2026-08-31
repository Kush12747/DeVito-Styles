package learn.DeVitoStyles.data.interfaces.orders;

import learn.DeVitoStyles.models.Checkout.Payment;

public interface PaymentRepository {

    Payment add(Payment payment);

    Payment findByOrderId(int orderId);

    Payment findByPaymentIntentId(String paymentIntentId);

    boolean updateStatus(int payment, String paymentStatus);
}
