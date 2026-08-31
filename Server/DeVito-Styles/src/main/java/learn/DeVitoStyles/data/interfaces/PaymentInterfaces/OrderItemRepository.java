package learn.DeVitoStyles.data.interfaces.PaymentInterfaces;

import learn.DeVitoStyles.models.Checkout.OrderItem;

import java.util.List;

public interface OrderItemRepository {

    OrderItem add(OrderItem orderItem);

    List<OrderItem> findByOrderId(int orderId);
}
