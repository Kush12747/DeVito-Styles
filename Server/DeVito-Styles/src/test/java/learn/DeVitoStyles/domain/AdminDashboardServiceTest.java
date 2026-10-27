package learn.DeVitoStyles.domain;

import learn.DeVitoStyles.data.interfaces.AdminDashboardRepository;
import learn.DeVitoStyles.dto.AdminDashboardResponse;
import learn.DeVitoStyles.dto.AdminDashboardResponse.BarberStat;
import learn.DeVitoStyles.dto.AdminDashboardResponse.HeatCell;
import learn.DeVitoStyles.dto.AdminDashboardResponse.RankedItem;
import learn.DeVitoStyles.models.dashboard.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.*;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminDashboardServiceTest {

    // Tuesday, Oct 27 2026 at noon
    private static final LocalDateTime NOW = LocalDateTime.of(2026, 10, 27, 12, 0);

    @Mock
    private AdminDashboardRepository repository;

    private AdminDashboardService service;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(NOW.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        service = new AdminDashboardService(repository, clock);
    }

    private static AppointmentFact appointment(int id, LocalDateTime at, String status, int userId,
                                               int serviceId, String serviceName, String price,
                                               int barberId, String barberName) {
        return new AppointmentFact(id, at, status, userId, serviceId, serviceName,
                new BigDecimal(price), barberId, barberName);
    }

    private static AppointmentFact haircut(int id, LocalDateTime at, String status, int userId) {
        return appointment(id, at, status, userId, 2, "Classic Haircut", "25.00", 1, "Antonio DeVito");
    }

    private static OrderFact order(int id, LocalDateTime at, String status, String total) {
        return new OrderFact(id, at, status, new BigDecimal(total));
    }

    private void stubData(List<AppointmentFact> appointments, List<OrderFact> orders,
                          List<OrderItemFact> items, List<ReviewFact> reviews) {
        when(repository.findAppointments(any(), any())).thenReturn(appointments);
        when(repository.findOrders(any(), any())).thenReturn(orders);
        when(repository.findPaidOrderItems(any(), any())).thenReturn(items);
        when(repository.findReviews(any(), any())).thenReturn(reviews);
        when(repository.findUpcomingAppointments(any(), anyInt())).thenReturn(List.of());
        when(repository.findRecentActivity(any(), anyInt())).thenReturn(List.of());
    }

    private void stubEmpty() {
        stubData(List.of(), List.of(), List.of(), List.of());
    }

    @Test
    void shouldRejectUnknownRange() {
        Result<AdminDashboardResponse> result = service.getDashboard("year");

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getResultType()).isEqualTo(ResultType.INVALID);
        verifyNoInteractions(repository);
    }

    @Test
    void shouldDefaultToWeekWhenRangeMissing() {
        stubEmpty();

        Result<AdminDashboardResponse> result = service.getDashboard(null);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getpayload().range()).isEqualTo("week");
    }

    @Test
    void shouldAcceptRangeInAnyCase() {
        stubEmpty();

        assertThat(service.getDashboard(" Month ").getpayload().range()).isEqualTo("month");
    }

    @Test
    void shouldBuildFourteenDailyBucketsEndingToday() {
        stubEmpty();

        AdminDashboardResponse dashboard = service.getDashboard("day").getpayload();

        assertThat(dashboard.trend()).hasSize(14);
        assertThat(dashboard.trendStart()).isEqualTo(LocalDate.of(2026, 10, 14));
        assertThat(dashboard.trend().get(13).start()).isEqualTo(LocalDate.of(2026, 10, 27));
        assertThat(dashboard.current().start()).isEqualTo(LocalDate.of(2026, 10, 27));
        assertThat(dashboard.previous().start()).isEqualTo(LocalDate.of(2026, 10, 26));
    }

    @Test
    void shouldBuildTwelveWeeklyBucketsStartingOnMondays() {
        stubEmpty();

        AdminDashboardResponse dashboard = service.getDashboard("week").getpayload();

        assertThat(dashboard.trend()).hasSize(12);
        assertThat(dashboard.trend().get(11).start()).isEqualTo(LocalDate.of(2026, 10, 26));
        assertThat(dashboard.trendStart()).isEqualTo(LocalDate.of(2026, 8, 10));
        assertThat(dashboard.trend()).allMatch(point -> point.start().getDayOfWeek() == DayOfWeek.MONDAY);
        // KPI window is the last 7 days including today
        assertThat(dashboard.current().start()).isEqualTo(LocalDate.of(2026, 10, 21));
        assertThat(dashboard.current().end()).isEqualTo(LocalDate.of(2026, 10, 27));
        assertThat(dashboard.previous().start()).isEqualTo(LocalDate.of(2026, 10, 14));
        assertThat(dashboard.previous().end()).isEqualTo(LocalDate.of(2026, 10, 20));
    }

    @Test
    void shouldBuildTwelveMonthlyBucketsAndFetchFromTheEarliestWindow() {
        stubEmpty();

        AdminDashboardResponse dashboard = service.getDashboard("month").getpayload();

        assertThat(dashboard.trend()).hasSize(12);
        assertThat(dashboard.trendStart()).isEqualTo(LocalDate.of(2025, 11, 1));
        assertThat(dashboard.trend().get(11).start()).isEqualTo(LocalDate.of(2026, 10, 1));
        // Trend starts before the previous 30-day window, and nothing after today is fetched
        verify(repository).findAppointments(LocalDateTime.of(2025, 11, 1, 0, 0), LocalDateTime.of(2026, 10, 28, 0, 0));
    }

    @Test
    void shouldCountOnlyCompletedVisitsAndPaidOrdersAsRevenue() {
        stubData(
                List.of(
                        haircut(1, NOW.minusHours(2), "COMPLETED", 10),
                        haircut(2, NOW.minusHours(1), "BOOKED", 11),
                        haircut(3, NOW.minusDays(1), "CANCELLED", 12),
                        haircut(4, NOW.minusDays(1), "COMPLETED", 10)),
                List.of(
                        order(1, NOW.minusHours(3), "Paid", "40.00"),
                        order(2, NOW.minusHours(3), "Pending", "99.00"),
                        order(3, NOW.minusDays(2), "Completed", "10.50"),
                        order(4, NOW.minusDays(2), "Cancelled", "75.00")),
                List.of(),
                List.of());

        AdminDashboardResponse.Kpis kpis = service.getDashboard("week").getpayload().current();

        assertThat(kpis.serviceRevenue()).isEqualByComparingTo("50.00");
        assertThat(kpis.productRevenue()).isEqualByComparingTo("50.50");
        assertThat(kpis.revenue()).isEqualByComparingTo("100.50");
        assertThat(kpis.appointments()).isEqualTo(3);
        assertThat(kpis.cancelled()).isEqualTo(1);
        assertThat(kpis.clientsServed()).isEqualTo(1);
        assertThat(kpis.productOrders()).isEqualTo(2);
    }

    @Test
    void shouldSplitCurrentAndPreviousWindows() {
        stubData(
                List.of(
                        haircut(1, NOW.minusHours(2), "COMPLETED", 10),
                        haircut(2, LocalDateTime.of(2026, 10, 26, 9, 0), "COMPLETED", 11),
                        haircut(3, LocalDateTime.of(2026, 10, 26, 10, 0), "COMPLETED", 12)),
                List.of(),
                List.of(),
                List.of(
                        new ReviewFact(NOW.minusHours(1), 1, 4),
                        new ReviewFact(NOW.minusHours(1), 1, 5),
                        new ReviewFact(LocalDateTime.of(2026, 10, 26, 18, 0), 1, 3)));

        AdminDashboardResponse dashboard = service.getDashboard("day").getpayload();

        assertThat(dashboard.current().appointments()).isEqualTo(1);
        assertThat(dashboard.previous().appointments()).isEqualTo(2);
        assertThat(dashboard.current().averageRating()).isEqualTo(4.5);
        assertThat(dashboard.current().reviewCount()).isEqualTo(2);
        assertThat(dashboard.previous().averageRating()).isEqualTo(3.0);
    }

    @Test
    void shouldReturnNullRatingWhenThereAreNoReviews() {
        stubEmpty();

        AdminDashboardResponse dashboard = service.getDashboard("week").getpayload();

        assertThat(dashboard.current().averageRating()).isNull();
        assertThat(dashboard.current().revenue()).isEqualByComparingTo("0");
        assertThat(dashboard.topServices()).isEmpty();
        assertThat(dashboard.barbers()).isEmpty();
    }

    @Test
    void shouldPutEachVisitInItsTrendBucket() {
        stubData(
                List.of(
                        haircut(1, LocalDateTime.of(2026, 10, 27, 9, 0), "COMPLETED", 10),
                        haircut(2, LocalDateTime.of(2026, 10, 25, 9, 0), "COMPLETED", 10),
                        haircut(3, LocalDateTime.of(2026, 10, 25, 10, 0), "CANCELLED", 10)),
                List.of(order(1, LocalDateTime.of(2026, 10, 25, 15, 0), "Paid", "20.00")),
                List.of(),
                List.of());

        List<AdminDashboardResponse.TrendPoint> trend = service.getDashboard("day").getpayload().trend();

        AdminDashboardResponse.TrendPoint oct25 = trend.get(11);
        assertThat(oct25.start()).isEqualTo(LocalDate.of(2026, 10, 25));
        assertThat(oct25.serviceRevenue()).isEqualByComparingTo("25.00");
        assertThat(oct25.productRevenue()).isEqualByComparingTo("20.00");
        assertThat(oct25.appointments()).isEqualTo(1);
        assertThat(oct25.cancelled()).isEqualTo(1);
        assertThat(trend.get(13).appointments()).isEqualTo(1);
        assertThat(trend.get(12).appointments()).isZero();
    }

    @Test
    void shouldRankServicesProductsAndBarbers() {
        LocalDateTime monday9 = LocalDateTime.of(2026, 10, 26, 9, 0);
        stubData(
                List.of(
                        appointment(1, monday9, "COMPLETED", 10, 3, "Skin Fade", "35.00", 2, "Chris Lopez"),
                        appointment(2, monday9.plusDays(-7), "COMPLETED", 11, 3, "Skin Fade", "35.00", 2, "Chris Lopez"),
                        appointment(3, monday9.plusHours(1), "COMPLETED", 12, 2, "Classic Haircut", "25.00", 1, "Antonio DeVito"),
                        appointment(4, monday9.plusHours(2), "CANCELLED", 12, 2, "Classic Haircut", "25.00", 1, "Antonio DeVito")),
                List.of(),
                List.of(
                        new OrderItemFact(7, "Beard Oil", 1, new BigDecimal("17.99")),
                        new OrderItemFact(1, "Matte Pomade", 2, new BigDecimal("39.98")),
                        new OrderItemFact(7, "Beard Oil", 2, new BigDecimal("35.98"))),
                List.of(new ReviewFact(monday9, 2, 5), new ReviewFact(monday9, 2, 4)));

        AdminDashboardResponse dashboard = service.getDashboard("week").getpayload();

        assertThat(dashboard.topServices()).extracting(RankedItem::name).containsExactly("Skin Fade", "Classic Haircut");
        assertThat(dashboard.topServices().get(1).count()).isEqualTo(1); // cancelled visit not counted

        assertThat(dashboard.topProducts()).extracting(RankedItem::name).containsExactly("Beard Oil", "Matte Pomade");
        assertThat(dashboard.topProducts().get(0).count()).isEqualTo(3);
        assertThat(dashboard.topProducts().get(0).revenue()).isEqualByComparingTo("53.97");

        BarberStat chris = dashboard.barbers().get(0);
        assertThat(chris.name()).isEqualTo("Chris Lopez");
        assertThat(chris.revenue()).isEqualByComparingTo("70.00");
        assertThat(chris.averageRating()).isEqualTo(4.5);
        BarberStat antonio = dashboard.barbers().get(1);
        assertThat(antonio.appointments()).isEqualTo(1);
        assertThat(antonio.averageRating()).isNull();

        assertThat(dashboard.statusMix().completed()).isEqualTo(3);
        assertThat(dashboard.statusMix().cancelled()).isEqualTo(1);

        // Monday (ISO 1) at 9am: two visits; the cancelled 11am one is skipped
        assertThat(dashboard.busyTimes()).contains(new HeatCell(1, 9, 2), new HeatCell(1, 10, 1));
        assertThat(dashboard.busyTimes()).noneMatch(cell -> cell.hour() == 11);
    }

    @Test
    void shouldPassUpcomingAndActivityThrough() {
        UpcomingAppointment next = new UpcomingAppointment(5, NOW.plusDays(1), "John Doe", "Skin Fade", "Chris Lopez");
        ActivityEvent event = new ActivityEvent("ORDER", NOW.minusHours(1), "John Doe", "DEV-1", "Paid",
                new BigDecimal("31.86"), null);
        when(repository.findAppointments(any(), any())).thenReturn(List.of());
        when(repository.findOrders(any(), any())).thenReturn(List.of());
        when(repository.findPaidOrderItems(any(), any())).thenReturn(List.of());
        when(repository.findReviews(any(), any())).thenReturn(List.of());
        when(repository.findUpcomingAppointments(NOW, 6)).thenReturn(List.of(next));
        when(repository.findRecentActivity(NOW, 10)).thenReturn(List.of(event));

        AdminDashboardResponse dashboard = service.getDashboard("week").getpayload();

        assertThat(dashboard.upcoming()).containsExactly(next);
        assertThat(dashboard.activity()).containsExactly(event);
        assertThat(dashboard.generatedAt()).isEqualTo(NOW);
    }
}
