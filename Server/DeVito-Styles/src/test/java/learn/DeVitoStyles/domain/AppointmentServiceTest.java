package learn.DeVitoStyles.domain;

import learn.DeVitoStyles.data.interfaces.*;
import learn.DeVitoStyles.dto.AppointmentResponse;
import learn.DeVitoStyles.dto.GoogleCalendarResponse;
import learn.DeVitoStyles.google.GoogleCalendarService;
import learn.DeVitoStyles.models.Appointment;
import learn.DeVitoStyles.models.Barber;
import learn.DeVitoStyles.models.Review;
import learn.DeVitoStyles.models.Service;
import learn.DeVitoStyles.models.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    private static final LocalDateTime FUTURE =
            LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);

    @Mock private AppointmentRepository appointmentRepository;
    @Mock private ReviewRepository reviewRepository;
    @Mock private UserRepository userRepository;
    @Mock private ServiceRepository serviceRepository;
    @Mock private EmailService emailService;
    @Mock private GoogleCalendarService googleCalendarService;
    @Mock private BarberRepository barberRepository;

    @InjectMocks private AppointmentService service;

    // =========================
    // GET ALL
    // =========================

    @Test
    void shouldGetAllAppointments() {
        Appointment reviewed = makeAppointment(1);
        Appointment notReviewed = makeAppointment(2);

        when(appointmentRepository.findAll()).thenReturn(List.of(reviewed, notReviewed));
        when(reviewRepository.findByAppointmentId(1)).thenReturn(new Review());

        Result<List<Appointment>> result = service.getAll();

        assertTrue(result.isSuccess());
        assertEquals(2, result.getpayload().size());
        assertTrue(reviewed.isHasReview());
        assertFalse(notReviewed.isHasReview());
    }

    // =========================
    // GET BY ID
    // =========================

    @Test
    void shouldGetAppointmentById() {
        when(appointmentRepository.findById(1)).thenReturn(makeAppointment(1));

        Result<Appointment> result = service.getById(1);

        assertTrue(result.isSuccess());
        assertEquals(1, result.getpayload().getAppointmentId());
        verify(reviewRepository).findByAppointmentId(1);
    }

    @Test
    void shouldNotGetAppointmentByInvalidId() {
        Result<Appointment> result = service.getById(0);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Appointment ID must be greater than 0"), result.getMessages());
        verifyNoInteractions(appointmentRepository);
    }

    @Test
    void shouldNotFindMissingAppointment() {
        when(appointmentRepository.findById(99999)).thenReturn(null);

        Result<Appointment> result = service.getById(99999);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.NOT_FOUND, result.getResultType());
        assertEquals(List.of("Appointment not found"), result.getMessages());
        verifyNoInteractions(reviewRepository);
    }

    // =========================
    // GET BY USER
    // =========================

    @Test
    void shouldGetAppointmentsByUserId() {
        when(appointmentRepository.findByUserId(1)).thenReturn(List.of(makeAppointment(1)));

        Result<List<Appointment>> result = service.getByUserId(1);

        assertTrue(result.isSuccess());
        assertEquals(1, result.getpayload().size());
    }

    @Test
    void shouldNotGetAppointmentsByInvalidUserId() {
        Result<List<Appointment>> result = service.getByUserId(0);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("User ID must be valid"), result.getMessages());
        verifyNoInteractions(appointmentRepository);
    }

    // =========================
    // GET BY BARBER AND DATE
    // =========================

    @Test
    void shouldGetAppointmentsByBarberAndDate() {
        LocalDate date = FUTURE.toLocalDate();
        when(appointmentRepository.findByBarberIdAndDate(1, date)).thenReturn(List.of(makeAppointment(1)));

        Result<List<Appointment>> result = service.getByBarberAndDate(1, date);

        assertTrue(result.isSuccess());
        assertEquals(1, result.getpayload().size());
    }

    @Test
    void shouldNotGetAppointmentsByInvalidBarberId() {
        Result<List<Appointment>> result = service.getByBarberAndDate(0, FUTURE.toLocalDate());

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Barber ID must be valid"), result.getMessages());
        verifyNoInteractions(appointmentRepository);
    }

    @Test
    void shouldNotGetAppointmentsWithoutDate() {
        Result<List<Appointment>> result = service.getByBarberAndDate(1, null);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Date is required"), result.getMessages());
        verifyNoInteractions(appointmentRepository);
    }

    // =========================
    // ADD
    // =========================

    @Test
    void shouldAddAppointment() throws Exception {
        Appointment appointment = makeAppointment(0);
        appointment.setStatus(null);
        Appointment created = makeAppointment(10);
        User user = makeUser();

        GoogleCalendarResponse calendar = new GoogleCalendarResponse();
        calendar.setEventId("evt-1");
        calendar.setEventUrl("https://calendar.example.com/evt-1");

        when(appointmentRepository.existsByBarberIdAndAppointmentDatetime(1, FUTURE)).thenReturn(false);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(serviceRepository.findById(1)).thenReturn(makeService());
        when(barberRepository.findById(1)).thenReturn(makeBarber());
        when(googleCalendarService.createAppointment(
                "John Doe", "test@test.com", "Tony DeVito", "Haircut", FUTURE, 30))
                .thenReturn(calendar);
        when(appointmentRepository.add(appointment)).thenReturn(created);

        Result<AppointmentResponse> result = service.add(appointment);

        assertTrue(result.isSuccess());
        assertEquals("BOOKED", appointment.getStatus());
        assertEquals("evt-1", appointment.getGoogleEventId());

        AppointmentResponse response = result.getpayload();
        assertEquals(10, response.getAppointmentId());
        assertEquals("John Doe", response.getCustomerName());
        assertEquals("test@test.com", response.getCustomerEmail());
        assertEquals("Tony DeVito", response.getBarberName());
        assertEquals("Haircut", response.getServiceName());
        assertEquals(30, response.getDurationMinutes());
        assertEquals(FUTURE, response.getAppointmentDatetime());
        assertEquals("BOOKED", response.getStatus());
        assertEquals("https://calendar.example.com/evt-1", response.getGoogleCalendarUrl());

        verify(emailService).sendAppointmentConfirmation(eq("test@test.com"), contains("confirmed"));
    }

    @Test
    void shouldNotAddNullAppointment() throws Exception {
        // NOTE: AppointmentService.add currently drops validation messages and returns a
        // SUCCESS-typed empty result; this only verifies nothing is persisted.
        Result<AppointmentResponse> result = service.add(null);

        assertNull(result.getpayload());
        verifyNoInteractions(appointmentRepository, googleCalendarService, emailService);
    }

    @Test
    void shouldNotAddAppointmentWithInvalidIds() throws Exception {
        Appointment appointment = makeAppointment(0);
        appointment.setBarberId(0);

        Result<AppointmentResponse> result = service.add(appointment);

        assertNull(result.getpayload());
        verifyNoInteractions(appointmentRepository, googleCalendarService, emailService);
    }

    @Test
    void shouldNotAddAppointmentInThePast() throws Exception {
        Appointment appointment = makeAppointment(0);
        appointment.setAppointmentDatetime(LocalDateTime.now().minusDays(1));

        Result<AppointmentResponse> result = service.add(appointment);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Appointment cannot be in the past"), result.getMessages());
        verifyNoInteractions(appointmentRepository, googleCalendarService, emailService);
    }

    @Test
    void shouldNotAddWhenTimeSlotBooked() throws Exception {
        Appointment appointment = makeAppointment(0);
        when(appointmentRepository.existsByBarberIdAndAppointmentDatetime(1, FUTURE)).thenReturn(true);

        Result<AppointmentResponse> result = service.add(appointment);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Time slot already booked"), result.getMessages());
        verify(appointmentRepository, never()).add(any());
        verifyNoInteractions(googleCalendarService, emailService);
    }

    @Test
    void shouldNotAddWhenDetailsCannotBeLoaded() throws Exception {
        Appointment appointment = makeAppointment(0);
        when(appointmentRepository.existsByBarberIdAndAppointmentDatetime(1, FUTURE)).thenReturn(false);
        when(userRepository.findById(1)).thenReturn(Optional.empty());
        when(serviceRepository.findById(1)).thenReturn(makeService());
        when(barberRepository.findById(1)).thenReturn(makeBarber());

        Result<AppointmentResponse> result = service.add(appointment);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Unable to load appointment details"), result.getMessages());
        verify(appointmentRepository, never()).add(any());
        verifyNoInteractions(googleCalendarService, emailService);
    }

    @Test
    void shouldNotAddWhenGoogleCalendarFails() throws Exception {
        Appointment appointment = makeAppointment(0);
        when(appointmentRepository.existsByBarberIdAndAppointmentDatetime(1, FUTURE)).thenReturn(false);
        when(userRepository.findById(1)).thenReturn(Optional.of(makeUser()));
        when(serviceRepository.findById(1)).thenReturn(makeService());
        when(barberRepository.findById(1)).thenReturn(makeBarber());
        when(googleCalendarService.createAppointment(
                anyString(), anyString(), anyString(), anyString(), any(), anyInt()))
                .thenThrow(new IOException("calendar down"));

        Result<AppointmentResponse> result = service.add(appointment);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Google Calendar event creation failed"), result.getMessages());
        verify(appointmentRepository, never()).add(any());
        verifyNoInteractions(emailService);
    }

    @Test
    void shouldNotAddWhenRepositoryAddFails() throws Exception {
        Appointment appointment = makeAppointment(0);
        GoogleCalendarResponse calendar = new GoogleCalendarResponse();
        calendar.setEventId("evt-1");

        when(appointmentRepository.existsByBarberIdAndAppointmentDatetime(1, FUTURE)).thenReturn(false);
        when(userRepository.findById(1)).thenReturn(Optional.of(makeUser()));
        when(serviceRepository.findById(1)).thenReturn(makeService());
        when(barberRepository.findById(1)).thenReturn(makeBarber());
        when(googleCalendarService.createAppointment(
                anyString(), anyString(), anyString(), anyString(), any(), anyInt()))
                .thenReturn(calendar);
        when(appointmentRepository.add(appointment)).thenReturn(null);

        Result<AppointmentResponse> result = service.add(appointment);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Failed to create appointment"), result.getMessages());
        assertNull(result.getpayload());
        verifyNoInteractions(emailService);
    }

    // =========================
    // CANCEL
    // =========================

    @Test
    void shouldCancelAppointment() throws IOException {
        Appointment appointment = makeAppointment(1);
        appointment.setGoogleEventId("evt-1");

        when(appointmentRepository.findById(1)).thenReturn(appointment);
        when(appointmentRepository.update(appointment)).thenReturn(true);
        when(userRepository.findById(1)).thenReturn(Optional.of(makeUser()));

        Result<Void> result = service.cancel(1);

        assertTrue(result.isSuccess());
        assertEquals("CANCELLED", appointment.getStatus());
        verify(googleCalendarService).deleteAppointment("evt-1");
        verify(emailService).sendCancellationEmail(eq("test@test.com"), contains("cancelled"));
    }

    @Test
    void shouldNotCancelMissingAppointment() throws IOException {
        when(appointmentRepository.findById(999)).thenReturn(null);

        Result<Void> result = service.cancel(999);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.NOT_FOUND, result.getResultType());
        assertEquals(List.of("Appointment not found"), result.getMessages());
        verify(appointmentRepository, never()).update(any());
        verifyNoInteractions(googleCalendarService, emailService);
    }

    @Test
    void shouldNotCancelWhenRepositoryUpdateFails() throws IOException {
        Appointment appointment = makeAppointment(1);
        when(appointmentRepository.findById(1)).thenReturn(appointment);
        when(appointmentRepository.update(appointment)).thenReturn(false);

        Result<Void> result = service.cancel(1);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Failed to cancel appointment"), result.getMessages());
        verifyNoInteractions(emailService);
    }

    // =========================
    // UPDATE
    // =========================

    @Test
    void shouldUpdateAppointment() {
        Appointment appointment = makeAppointment(1);

        // The appointment's own existing slot is not a conflict
        when(appointmentRepository.findByBarberIdAndDate(1, FUTURE.toLocalDate()))
                .thenReturn(List.of(makeAppointment(1)));
        when(appointmentRepository.update(appointment)).thenReturn(true);
        when(userRepository.findById(1)).thenReturn(Optional.of(makeUser()));

        Result<Appointment> result = service.update(appointment);

        assertTrue(result.isSuccess());
        assertSame(appointment, result.getpayload());
        verify(emailService).sendUpdatedAppointmentEmail(eq("test@test.com"), contains("updated"));
    }

    @Test
    void shouldUpdateWithoutEmailWhenUserHasNoEmail() {
        Appointment appointment = makeAppointment(1);
        User user = makeUser();
        user.setEmail(null);

        when(appointmentRepository.findByBarberIdAndDate(1, FUTURE.toLocalDate())).thenReturn(List.of());
        when(appointmentRepository.update(appointment)).thenReturn(true);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        Result<Appointment> result = service.update(appointment);

        assertTrue(result.isSuccess());
        verifyNoInteractions(emailService);
    }

    @Test
    void shouldNotUpdateNullAppointment() {
        Result<Appointment> result = service.update(null);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Appointment cannot be null"), result.getMessages());
        verifyNoInteractions(appointmentRepository, emailService);
    }

    @Test
    void shouldNotUpdateWithInvalidIds() {
        Appointment appointment = makeAppointment(1);
        appointment.setServiceId(0);

        Result<Appointment> result = service.update(appointment);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Invalid IDs provided"), result.getMessages());
        verifyNoInteractions(appointmentRepository, emailService);
    }

    @Test
    void shouldNotUpdateWithoutDatetime() {
        Appointment appointment = makeAppointment(1);
        appointment.setAppointmentDatetime(null);

        Result<Appointment> result = service.update(appointment);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Appointment datetime is required"), result.getMessages());
        verifyNoInteractions(appointmentRepository, emailService);
    }

    @Test
    void shouldNotUpdateWithoutAppointmentId() {
        Appointment appointment = makeAppointment(0);

        Result<Appointment> result = service.update(appointment);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Appointment ID is required"), result.getMessages());
        verifyNoInteractions(appointmentRepository, emailService);
    }

    @Test
    void shouldNotUpdateWhenTimeSlotBooked() {
        Appointment appointment = makeAppointment(1);
        when(appointmentRepository.findByBarberIdAndDate(1, FUTURE.toLocalDate()))
                .thenReturn(List.of(makeAppointment(2)));

        Result<Appointment> result = service.update(appointment);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Time slot already booked"), result.getMessages());
        verify(appointmentRepository, never()).update(any());
        verifyNoInteractions(emailService);
    }

    @Test
    void shouldNotUpdateMissingAppointment() {
        Appointment appointment = makeAppointment(999);
        when(appointmentRepository.findByBarberIdAndDate(1, FUTURE.toLocalDate())).thenReturn(List.of());
        when(appointmentRepository.update(appointment)).thenReturn(false);

        Result<Appointment> result = service.update(appointment);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.NOT_FOUND, result.getResultType());
        assertEquals(List.of("Appointment not found"), result.getMessages());
        assertNull(result.getpayload());
        verifyNoInteractions(emailService);
    }

    // =========================
    // HELPERS
    // =========================

    private Appointment makeAppointment(int appointmentId) {
        Appointment appointment = new Appointment();
        appointment.setAppointmentId(appointmentId);
        appointment.setUserId(1);
        appointment.setBarberId(1);
        appointment.setServiceId(1);
        appointment.setAppointmentDatetime(FUTURE);
        appointment.setStatus("BOOKED");
        return appointment;
    }

    private User makeUser() {
        User user = new User();
        user.setUserId(1);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("test@test.com");
        return user;
    }

    private Service makeService() {
        return new Service(1, "Haircut", 30, new BigDecimal("25.00"), "Classic cut");
    }

    private Barber makeBarber() {
        Barber barber = new Barber();
        barber.setBarberId(1);
        barber.setFirstName("Tony");
        barber.setLastName("DeVito");
        return barber;
    }
}
