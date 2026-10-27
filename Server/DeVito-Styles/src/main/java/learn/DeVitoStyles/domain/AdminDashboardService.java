package learn.DeVitoStyles.domain;

import learn.DeVitoStyles.data.interfaces.AdminDashboardRepository;
import learn.DeVitoStyles.dto.AdminDashboardResponse;
import learn.DeVitoStyles.dto.AdminDashboardResponse.*;
import learn.DeVitoStyles.models.dashboard.AppointmentFact;
import learn.DeVitoStyles.models.dashboard.OrderFact;
import learn.DeVitoStyles.models.dashboard.OrderItemFact;
import learn.DeVitoStyles.models.dashboard.ReviewFact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Builds the admin dashboard: KPIs with a comparison window, a revenue and
 * appointment trend, and breakdowns by service, product, barber and time.
 *
 * Definitions:
 * - service revenue: the service price of COMPLETED appointments
 * - product revenue: the total of Paid, Processing and Completed orders
 * - appointments: every appointment except CANCELLED ones
 */
@Service
public class AdminDashboardService {

    static final Set<String> PAID_ORDER_STATUSES = Set.of("Paid", "Processing", "Completed");
    private static final int UPCOMING_LIMIT = 6;
    private static final int ACTIVITY_LIMIT = 10;
    private static final int TOP_LIMIT = 5;

    enum Range {
        DAY(1, 14), WEEK(7, 12), MONTH(30, 12);

        final int kpiDays;     // length of the KPI window in days
        final int trendBuckets;

        Range(int kpiDays, int trendBuckets) {
            this.kpiDays = kpiDays;
            this.trendBuckets = trendBuckets;
        }

        // First day of the bucket that contains date
        LocalDate bucketStart(LocalDate date) {
            return switch (this) {
                case DAY -> date;
                case WEEK -> date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                case MONTH -> date.withDayOfMonth(1);
            };
        }

        LocalDate addBuckets(LocalDate bucketStart, int buckets) {
            return switch (this) {
                case DAY -> bucketStart.plusDays(buckets);
                case WEEK -> bucketStart.plusWeeks(buckets);
                case MONTH -> bucketStart.plusMonths(buckets);
            };
        }
    }

    private final AdminDashboardRepository repository;
    private final Clock clock;

    @Autowired
    public AdminDashboardService(AdminDashboardRepository repository) {
        this(repository, Clock.systemDefaultZone());
    }

    AdminDashboardService(AdminDashboardRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    public Result<AdminDashboardResponse> getDashboard(String rangeName) {
        Result<AdminDashboardResponse> result = new Result<>();

        Range range = parseRange(rangeName);
        if (range == null) {
            result.addErrorMessage("Range must be day, week or month", ResultType.INVALID);
            return result;
        }

        LocalDateTime now = LocalDateTime.now(clock);
        LocalDate today = now.toLocalDate();
        LocalDate tomorrow = today.plusDays(1);

        // KPI windows: [currentStart, tomorrow) and [previousStart, currentStart)
        LocalDate currentStart = tomorrow.minusDays(range.kpiDays);
        LocalDate previousStart = currentStart.minusDays(range.kpiDays);

        // Trend window: the last N buckets, ending with the one holding today
        LocalDate trendStart = range.addBuckets(range.bucketStart(today), 1 - range.trendBuckets);

        // One fetch per table covers every window; nothing after today counts
        LocalDateTime from = (trendStart.isBefore(previousStart) ? trendStart : previousStart).atStartOfDay();
        LocalDateTime to = tomorrow.atStartOfDay();
        List<AppointmentFact> appointments = repository.findAppointments(from, to);
        List<OrderFact> orders = repository.findOrders(from, to);
        List<ReviewFact> reviews = repository.findReviews(from, to);
        List<OrderItemFact> trendItems = repository.findPaidOrderItems(trendStart.atStartOfDay(), to);

        List<AppointmentFact> trendAppointments = within(appointments, AppointmentFact::appointmentDatetime, trendStart, tomorrow);
        List<ReviewFact> trendReviews = within(reviews, ReviewFact::createdAt, trendStart, tomorrow);

        result.setpayload(new AdminDashboardResponse(
                range.name().toLowerCase(),
                now,
                trendStart,
                kpis(appointments, orders, reviews, currentStart, tomorrow),
                kpis(appointments, orders, reviews, previousStart, currentStart),
                trend(range, trendStart, appointments, orders),
                statusMix(trendAppointments),
                topServices(trendAppointments),
                topProducts(trendItems),
                barbers(trendAppointments, trendReviews),
                busyTimes(trendAppointments),
                repository.findUpcomingAppointments(now, UPCOMING_LIMIT),
                repository.findRecentActivity(now, ACTIVITY_LIMIT)));

        return result;
    }

    private static Range parseRange(String rangeName) {
        if (rangeName == null || rangeName.isBlank()) {
            return Range.WEEK;
        }
        try {
            return Range.valueOf(rangeName.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    // ---------- KPIs ----------

    private Kpis kpis(List<AppointmentFact> allAppointments, List<OrderFact> allOrders,
                      List<ReviewFact> allReviews, LocalDate start, LocalDate end) {
        List<AppointmentFact> appointments = within(allAppointments, AppointmentFact::appointmentDatetime, start, end);
        List<OrderFact> paidOrders = within(allOrders, OrderFact::createdAt, start, end).stream()
                .filter(AdminDashboardService::isPaid)
                .toList();
        List<ReviewFact> reviews = within(allReviews, ReviewFact::createdAt, start, end);

        BigDecimal serviceRevenue = serviceRevenue(appointments);
        BigDecimal productRevenue = sum(paidOrders.stream().map(OrderFact::totalAmount));

        int cancelled = (int) appointments.stream().filter(AdminDashboardService::isCancelled).count();
        int clientsServed = (int) appointments.stream()
                .filter(AdminDashboardService::isCompleted)
                .map(AppointmentFact::userId)
                .distinct()
                .count();

        return new Kpis(
                start,
                end.minusDays(1),
                serviceRevenue.add(productRevenue),
                serviceRevenue,
                productRevenue,
                appointments.size() - cancelled,
                cancelled,
                clientsServed,
                paidOrders.size(),
                averageRating(reviews),
                reviews.size());
    }

    // ---------- trend ----------

    private List<TrendPoint> trend(Range range, LocalDate trendStart,
                                   List<AppointmentFact> appointments, List<OrderFact> orders) {
        List<TrendPoint> points = new ArrayList<>();

        for (int i = 0; i < range.trendBuckets; i++) {
            LocalDate start = range.addBuckets(trendStart, i);
            LocalDate end = range.addBuckets(trendStart, i + 1);

            List<AppointmentFact> bucket = within(appointments, AppointmentFact::appointmentDatetime, start, end);
            int cancelled = (int) bucket.stream().filter(AdminDashboardService::isCancelled).count();
            BigDecimal productRevenue = sum(within(orders, OrderFact::createdAt, start, end).stream()
                    .filter(AdminDashboardService::isPaid)
                    .map(OrderFact::totalAmount));

            points.add(new TrendPoint(start, serviceRevenue(bucket), productRevenue,
                    bucket.size() - cancelled, cancelled));
        }

        return points;
    }

    // ---------- breakdowns ----------

    private StatusMix statusMix(List<AppointmentFact> appointments) {
        int completed = 0, booked = 0, cancelled = 0;
        for (AppointmentFact appointment : appointments) {
            if (isCompleted(appointment)) completed++;
            else if (isCancelled(appointment)) cancelled++;
            else booked++;
        }
        return new StatusMix(completed, booked, cancelled);
    }

    private List<RankedItem> topServices(List<AppointmentFact> appointments) {
        Map<Integer, List<AppointmentFact>> byService = appointments.stream()
                .filter(appointment -> !isCancelled(appointment))
                .collect(Collectors.groupingBy(AppointmentFact::serviceId));

        return byService.values().stream()
                .map(group -> new RankedItem(
                        group.get(0).serviceId(),
                        group.get(0).serviceName(),
                        group.size(),
                        serviceRevenue(group)))
                .sorted(Comparator.comparingInt(RankedItem::count).reversed()
                        .thenComparing(RankedItem::name))
                .limit(TOP_LIMIT)
                .toList();
    }

    private List<RankedItem> topProducts(List<OrderItemFact> items) {
        Map<Integer, List<OrderItemFact>> byProduct = items.stream()
                .collect(Collectors.groupingBy(OrderItemFact::productId));

        return byProduct.values().stream()
                .map(group -> new RankedItem(
                        group.get(0).productId(),
                        group.get(0).productName(),
                        group.stream().mapToInt(OrderItemFact::quantity).sum(),
                        sum(group.stream().map(OrderItemFact::lineTotal))))
                .sorted(Comparator.comparing(RankedItem::revenue).reversed()
                        .thenComparing(RankedItem::name))
                .limit(TOP_LIMIT)
                .toList();
    }

    private List<BarberStat> barbers(List<AppointmentFact> appointments, List<ReviewFact> reviews) {
        Map<Integer, List<AppointmentFact>> byBarber = appointments.stream()
                .collect(Collectors.groupingBy(AppointmentFact::barberId));
        Map<Integer, List<ReviewFact>> reviewsByBarber = reviews.stream()
                .collect(Collectors.groupingBy(ReviewFact::barberId));

        return byBarber.values().stream()
                .map(group -> {
                    List<ReviewFact> barberReviews = reviewsByBarber.getOrDefault(group.get(0).barberId(), List.of());
                    return new BarberStat(
                            group.get(0).barberId(),
                            group.get(0).barberName(),
                            (int) group.stream().filter(appointment -> !isCancelled(appointment)).count(),
                            (int) group.stream().filter(AdminDashboardService::isCompleted).count(),
                            serviceRevenue(group),
                            averageRating(barberReviews),
                            barberReviews.size());
                })
                .sorted(Comparator.comparing(BarberStat::revenue).reversed()
                        .thenComparing(BarberStat::name))
                .toList();
    }

    private List<HeatCell> busyTimes(List<AppointmentFact> appointments) {
        Map<List<Integer>, Long> counts = appointments.stream()
                .filter(appointment -> !isCancelled(appointment))
                .collect(Collectors.groupingBy(
                        appointment -> List.of(
                                appointment.appointmentDatetime().getDayOfWeek().getValue(),
                                appointment.appointmentDatetime().getHour()),
                        Collectors.counting()));

        return counts.entrySet().stream()
                .map(entry -> new HeatCell(entry.getKey().get(0), entry.getKey().get(1), entry.getValue().intValue()))
                .sorted(Comparator.comparingInt(HeatCell::dayOfWeek).thenComparingInt(HeatCell::hour))
                .toList();
    }

    // ---------- helpers ----------

    private static <T> List<T> within(List<T> items, Function<T, LocalDateTime> time,
                                      LocalDate start, LocalDate end) {
        LocalDateTime from = start.atStartOfDay();
        LocalDateTime to = end.atStartOfDay();
        return items.stream()
                .filter(item -> {
                    LocalDateTime at = time.apply(item);
                    return at != null && !at.isBefore(from) && at.isBefore(to);
                })
                .toList();
    }

    private static BigDecimal serviceRevenue(List<AppointmentFact> appointments) {
        return sum(appointments.stream()
                .filter(AdminDashboardService::isCompleted)
                .map(AppointmentFact::servicePrice));
    }

    private static BigDecimal sum(java.util.stream.Stream<BigDecimal> amounts) {
        return amounts.filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private static Double averageRating(List<ReviewFact> reviews) {
        if (reviews.isEmpty()) {
            return null;
        }
        double average = reviews.stream().mapToInt(ReviewFact::rating).average().orElse(0);
        return BigDecimal.valueOf(average).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private static boolean isCompleted(AppointmentFact appointment) {
        return "COMPLETED".equalsIgnoreCase(appointment.status());
    }

    private static boolean isCancelled(AppointmentFact appointment) {
        return "CANCELLED".equalsIgnoreCase(appointment.status());
    }

    private static boolean isPaid(OrderFact order) {
        return PAID_ORDER_STATUSES.contains(order.status());
    }
}
