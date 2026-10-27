package learn.DeVitoStyles.controller;

import learn.DeVitoStyles.domain.AdminDashboardService;
import learn.DeVitoStyles.domain.Result;
import learn.DeVitoStyles.domain.ResultType;
import learn.DeVitoStyles.dto.AdminDashboardResponse;
import learn.DeVitoStyles.dto.AdminDashboardResponse.Kpis;
import learn.DeVitoStyles.dto.AdminDashboardResponse.StatusMix;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AdminDashboardControllerTest {

    @Mock
    private AdminDashboardService service;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new AdminDashboardController(service)).build();
    }

    private static AdminDashboardResponse dashboard() {
        LocalDate today = LocalDate.of(2026, 10, 27);
        Kpis kpis = new Kpis(today.minusDays(6), today, new BigDecimal("100.50"), new BigDecimal("50.00"),
                new BigDecimal("50.50"), 3, 1, 2, 2, 4.5, 2);
        return new AdminDashboardResponse("week", LocalDateTime.of(2026, 10, 27, 12, 0), today.minusWeeks(11),
                kpis, kpis, List.of(), new StatusMix(3, 1, 1), List.of(), List.of(), List.of(),
                List.of(), List.of(), List.of());
    }

    private static <T> Result<T> success(T payload) {
        Result<T> result = new Result<>();
        result.setpayload(payload);
        return result;
    }

    @Test
    void getDashboard_returns200WithStats() throws Exception {
        when(service.getDashboard("week")).thenReturn(success(dashboard()));

        mvc.perform(get("/api/admin/dashboard").param("range", "week"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.range").value("week"))
                .andExpect(jsonPath("$.current.revenue").value(100.50))
                .andExpect(jsonPath("$.current.appointments").value(3))
                .andExpect(jsonPath("$.statusMix.completed").value(3));
    }

    @Test
    void getDashboard_withoutRange_passesNullForDefault() throws Exception {
        when(service.getDashboard(null)).thenReturn(success(dashboard()));

        mvc.perform(get("/api/admin/dashboard"))
                .andExpect(status().isOk());

        verify(service).getDashboard(null);
    }

    @Test
    void getDashboard_returns400ForInvalidRange() throws Exception {
        Result<AdminDashboardResponse> invalid = new Result<>();
        invalid.addErrorMessage("Range must be day, week or month", ResultType.INVALID);
        when(service.getDashboard("year")).thenReturn(invalid);

        mvc.perform(get("/api/admin/dashboard").param("range", "year"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value("Range must be day, week or month"));
    }
}
