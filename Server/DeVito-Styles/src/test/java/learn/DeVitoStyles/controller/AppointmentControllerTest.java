package learn.DeVitoStyles.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import learn.DeVitoStyles.domain.AppointmentService;
import learn.DeVitoStyles.domain.Result;
import learn.DeVitoStyles.domain.ResultType;
import learn.DeVitoStyles.dto.AppointmentResponse;
import learn.DeVitoStyles.models.Appointment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AppointmentControllerTest {

    @Mock
    private AppointmentService service;

    private MockMvc mvc;
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    private static final LocalDate DATE = LocalDate.of(2026, 10, 20);
    private static final LocalDateTime DATETIME = LocalDateTime.of(2026, 10, 20, 10, 30);

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new AppointmentController(service)).build();
    }

    private static Appointment appointment(int id) {
        return new Appointment(id, 2, 3, 4, DATETIME, "BOOKED");
    }

    private static <T> Result<T> success(T payload) {
        Result<T> result = new Result<>();
        result.setpayload(payload);
        return result;
    }

    private static <T> Result<T> failure(String message, ResultType type) {
        Result<T> result = new Result<>();
        result.addErrorMessage(message, type);
        return result;
    }

    // ---------- GET /availability ----------

    @Test
    void getAvailability_whenSuccess_returns200WithAppointments() throws Exception {
        when(service.getByBarberAndDate(4, DATE)).thenReturn(success(List.of(appointment(1), appointment(2))));

        mvc.perform(get("/api/appointment/availability")
                        .param("barberId", "4")
                        .param("date", "2026-10-20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].appointmentId").value(1))
                .andExpect(jsonPath("$[0].barberId").value(4))
                .andExpect(jsonPath("$[0].status").value("BOOKED"));

        verify(service).getByBarberAndDate(4, DATE);
    }

    @Test
    void getAvailability_whenInvalid_returns400WithMessages() throws Exception {
        when(service.getByBarberAndDate(4, DATE)).thenReturn(failure("Barber id is required", ResultType.INVALID));

        mvc.perform(get("/api/appointment/availability")
                        .param("barberId", "4")
                        .param("date", "2026-10-20"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value("Barber id is required"));
    }

    @Test
    void getAvailability_whenNotFound_returns404() throws Exception {
        when(service.getByBarberAndDate(4, DATE)).thenReturn(failure("Barber not found", ResultType.NOT_FOUND));

        mvc.perform(get("/api/appointment/availability")
                        .param("barberId", "4")
                        .param("date", "2026-10-20"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$[0]").value("Barber not found"));
    }

    // ---------- GET / ----------

    @Test
    void findAll_returns200WithAllAppointments() throws Exception {
        when(service.getAll()).thenReturn(success(List.of(appointment(1), appointment(2), appointment(3))));

        mvc.perform(get("/api/appointment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[2].appointmentId").value(3));

        verify(service).getAll();
    }

    // ---------- GET /{id} ----------

    @Test
    void findById_whenFound_returns200WithAppointment() throws Exception {
        when(service.getById(7)).thenReturn(success(appointment(7)));

        mvc.perform(get("/api/appointment/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.appointmentId").value(7))
                .andExpect(jsonPath("$.userId").value(2))
                .andExpect(jsonPath("$.serviceId").value(3))
                .andExpect(jsonPath("$.status").value("BOOKED"));

        verify(service).getById(7);
    }

    @Test
    void findById_whenNotFound_returns404() throws Exception {
        when(service.getById(99)).thenReturn(failure("Appointment not found", ResultType.NOT_FOUND));

        mvc.perform(get("/api/appointment/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$[0]").value("Appointment not found"));
    }

    @Test
    void findById_whenInvalid_returns400() throws Exception {
        when(service.getById(0)).thenReturn(failure("Invalid id", ResultType.INVALID));

        mvc.perform(get("/api/appointment/0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value("Invalid id"));
    }

    // ---------- GET /user/{userId} ----------

    @Test
    void findByUserId_whenSuccess_returns200() throws Exception {
        when(service.getByUserId(2)).thenReturn(success(List.of(appointment(1))));

        mvc.perform(get("/api/appointment/user/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].userId").value(2));

        verify(service).getByUserId(2);
    }

    @Test
    void findByUserId_whenNotFound_returns404() throws Exception {
        when(service.getByUserId(50)).thenReturn(failure("User not found", ResultType.NOT_FOUND));

        mvc.perform(get("/api/appointment/user/50"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$[0]").value("User not found"));
    }

    @Test
    void findByUserId_whenInvalid_returns400() throws Exception {
        when(service.getByUserId(0)).thenReturn(failure("Invalid user id", ResultType.INVALID));

        mvc.perform(get("/api/appointment/user/0"))
                .andExpect(status().isBadRequest());
    }

    // ---------- GET /barber/{barberId} ----------

    @Test
    void findByBarberAndDate_whenSuccess_returns200() throws Exception {
        when(service.getByBarberAndDate(4, DATE)).thenReturn(success(List.of(appointment(5))));

        mvc.perform(get("/api/appointment/barber/4").param("date", "2026-10-20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].appointmentId").value(5));

        verify(service).getByBarberAndDate(4, DATE);
    }

    @Test
    void findByBarberAndDate_whenNotFound_returns404() throws Exception {
        when(service.getByBarberAndDate(4, DATE)).thenReturn(failure("Barber not found", ResultType.NOT_FOUND));

        mvc.perform(get("/api/appointment/barber/4").param("date", "2026-10-20"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$[0]").value("Barber not found"));
    }

    @Test
    void findByBarberAndDate_whenDateMissing_returns400WithoutCallingService() throws Exception {
        mvc.perform(get("/api/appointment/barber/4"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    // ---------- POST / ----------

    @Test
    void add_whenSuccess_returns201WithAppointmentResponse() throws Exception {
        AppointmentResponse response = new AppointmentResponse();
        response.setAppointmentId(10);
        response.setCustomerName("John Smith");
        response.setBarberName("Vito DeVito");
        response.setServiceName("Haircut");
        response.setDurationMinutes(30);
        response.setAppointmentDatetime(DATETIME);
        response.setStatus("BOOKED");
        response.setGoogleCalendarUrl("https://calendar.example/evt");
        when(service.add(any(Appointment.class))).thenReturn(success(response));

        Appointment request = appointment(0);

        mvc.perform(post("/api/appointment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.appointmentId").value(10))
                .andExpect(jsonPath("$.customerName").value("John Smith"))
                .andExpect(jsonPath("$.serviceName").value("Haircut"))
                .andExpect(jsonPath("$.durationMinutes").value(30))
                .andExpect(jsonPath("$.googleCalendarUrl").value("https://calendar.example/evt"));

        ArgumentCaptor<Appointment> captor = ArgumentCaptor.forClass(Appointment.class);
        verify(service).add(captor.capture());
        Appointment sent = captor.getValue();
        assertThat(sent.getUserId()).isEqualTo(2);
        assertThat(sent.getServiceId()).isEqualTo(3);
        assertThat(sent.getBarberId()).isEqualTo(4);
        assertThat(sent.getAppointmentDatetime()).isEqualTo(DATETIME);
        assertThat(sent.getStatus()).isEqualTo("BOOKED");
    }

    @Test
    void add_whenInvalid_returns400WithMessages() throws Exception {
        when(service.add(any(Appointment.class))).thenReturn(failure("Time slot is in the past", ResultType.INVALID));

        mvc.perform(post("/api/appointment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(appointment(0))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value("Time slot is in the past"));
    }

    @Test
    void add_whenConflict_returns409() throws Exception {
        when(service.add(any(Appointment.class))).thenReturn(failure("Time slot already booked", ResultType.CONFLICT));

        mvc.perform(post("/api/appointment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(appointment(0))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$[0]").value("Time slot already booked"));
    }

    @Test
    void add_whenNotFound_returns404() throws Exception {
        when(service.add(any(Appointment.class))).thenReturn(failure("Service not found", ResultType.NOT_FOUND));

        mvc.perform(post("/api/appointment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(appointment(0))))
                .andExpect(status().isNotFound());
    }

    // ---------- PUT /{id} ----------

    @Test
    void update_whenSuccess_returns204() throws Exception {
        Appointment request = appointment(5);
        when(service.update(any(Appointment.class))).thenReturn(success(request));

        mvc.perform(put("/api/appointment/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        ArgumentCaptor<Appointment> captor = ArgumentCaptor.forClass(Appointment.class);
        verify(service).update(captor.capture());
        assertThat(captor.getValue().getAppointmentId()).isEqualTo(5);
        assertThat(captor.getValue().getAppointmentDatetime()).isEqualTo(DATETIME);
    }

    @Test
    void update_whenPathIdMismatch_returns409WithoutCallingService() throws Exception {
        mvc.perform(put("/api/appointment/6")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(appointment(5))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$[0]").value("Path ID and appointment id don't match"));

        verifyNoInteractions(service);
    }

    @Test
    void update_whenNotFound_returns404() throws Exception {
        when(service.update(any(Appointment.class))).thenReturn(failure("Appointment not found", ResultType.NOT_FOUND));

        mvc.perform(put("/api/appointment/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(appointment(5))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$[0]").value("Appointment not found"));
    }

    @Test
    void update_whenInvalid_returns400() throws Exception {
        when(service.update(any(Appointment.class))).thenReturn(failure("Status is required", ResultType.INVALID));

        mvc.perform(put("/api/appointment/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(appointment(5))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value("Status is required"));
    }

    // ---------- PUT /cancel/{id} ----------

    @Test
    void cancel_whenSuccess_returns204() throws Exception {
        when(service.cancel(5)).thenReturn(new Result<>());

        mvc.perform(put("/api/appointment/cancel/5"))
                .andExpect(status().isNoContent());

        verify(service).cancel(5);
    }

    @Test
    void cancel_whenNotFound_returns404() throws Exception {
        when(service.cancel(99)).thenReturn(failure("Appointment not found", ResultType.NOT_FOUND));

        mvc.perform(put("/api/appointment/cancel/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$[0]").value("Appointment not found"));
    }

    @Test
    void cancel_whenInvalid_returns400() throws Exception {
        when(service.cancel(5)).thenReturn(failure("Appointment already cancelled", ResultType.INVALID));

        mvc.perform(put("/api/appointment/cancel/5"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value("Appointment already cancelled"));
    }
}
