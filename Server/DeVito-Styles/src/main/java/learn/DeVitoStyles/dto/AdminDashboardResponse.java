package learn.DeVitoStyles.dto;

import learn.DeVitoStyles.models.dashboard.ActivityEvent;
import learn.DeVitoStyles.models.dashboard.UpcomingAppointment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Everything the admin dashboard shows for one range (day, week or month).
 *
 * current / previous: headline numbers for the KPI window and the window
 * before it (today vs yesterday, last 7 vs previous 7 days, last 30 vs
 * previous 30 days).
 *
 * trend and the breakdowns cover the longer chart window: 14 days,
 * 12 weeks or 12 months, ending today.
 */
public record AdminDashboardResponse(
        String range,
        LocalDateTime generatedAt,
        LocalDate trendStart,
        Kpis current,
        Kpis previous,
        List<TrendPoint> trend,
        StatusMix statusMix,
        List<RankedItem> topServices,
        List<RankedItem> topProducts,
        List<BarberStat> barbers,
        List<HeatCell> busyTimes,
        List<UpcomingAppointment> upcoming,
        List<ActivityEvent> activity) {

    public record Kpis(
            LocalDate start,
            LocalDate end,
            BigDecimal revenue,
            BigDecimal serviceRevenue,
            BigDecimal productRevenue,
            int appointments,
            int cancelled,
            int clientsServed,
            int productOrders,
            Double averageRating,
            int reviewCount) {
    }

    /** One bucket of the trend chart; start is the bucket's first day. */
    public record TrendPoint(
            LocalDate start,
            BigDecimal serviceRevenue,
            BigDecimal productRevenue,
            int appointments,
            int cancelled) {
    }

    public record StatusMix(int completed, int booked, int cancelled) {
    }

    public record RankedItem(int id, String name, int count, BigDecimal revenue) {
    }

    public record BarberStat(
            int barberId,
            String name,
            int appointments,
            int completed,
            BigDecimal revenue,
            Double averageRating,
            int reviewCount) {
    }

    /** Appointments on one ISO weekday (1 = Monday) at one hour of the day. */
    public record HeatCell(int dayOfWeek, int hour, int count) {
    }
}
