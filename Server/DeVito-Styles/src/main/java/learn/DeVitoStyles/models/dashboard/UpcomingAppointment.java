package learn.DeVitoStyles.models.dashboard;

import java.time.LocalDateTime;

/** A booked appointment that hasn't happened yet. */
public record UpcomingAppointment(
        int appointmentId,
        LocalDateTime appointmentDatetime,
        String customerName,
        String serviceName,
        String barberName) {
}
