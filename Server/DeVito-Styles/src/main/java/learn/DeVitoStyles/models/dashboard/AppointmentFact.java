package learn.DeVitoStyles.models.dashboard;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** One appointment joined with its service and barber, for dashboard math. */
public record AppointmentFact(
        int appointmentId,
        LocalDateTime appointmentDatetime,
        String status,
        int userId,
        int serviceId,
        String serviceName,
        BigDecimal servicePrice,
        int barberId,
        String barberName) {
}
