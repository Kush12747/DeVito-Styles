package learn.DeVitoStyles.data.interfaces;

import learn.DeVitoStyles.models.dashboard.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Read-only queries behind the admin dashboard.
 * Every window is half-open: start inclusive, end exclusive.
 */
public interface AdminDashboardRepository {

    List<AppointmentFact> findAppointments(LocalDateTime start, LocalDateTime end);

    List<OrderFact> findOrders(LocalDateTime start, LocalDateTime end);

    List<OrderItemFact> findPaidOrderItems(LocalDateTime start, LocalDateTime end);

    List<ReviewFact> findReviews(LocalDateTime start, LocalDateTime end);

    List<UpcomingAppointment> findUpcomingAppointments(LocalDateTime from, int limit);

    List<ActivityEvent> findRecentActivity(LocalDateTime before, int limit);
}
