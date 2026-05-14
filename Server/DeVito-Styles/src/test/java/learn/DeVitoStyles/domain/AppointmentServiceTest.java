package learn.DeVitoStyles.domain;

import learn.DeVitoStyles.data.AppointmentRepository;
import learn.DeVitoStyles.data.UserRepository;
import learn.DeVitoStyles.models.Appointment;
import learn.DeVitoStyles.models.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class AppointmentServiceTest {

    @Autowired
    private AppointmentService service;

    @MockBean
    private AppointmentRepository repository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JavaMailSender mailSender;


    @Test
    void shouldGetAllAppointments() {
        List<Appointment> expected = List.of(makeAppointment());

        when(repository.findAll()).thenReturn(expected);

        Result<List<Appointment>> result = service.getAll();

        assertTrue(result.isSuccess());
        assertEquals(1, result.getpayload().size());
    }

    @Test
    void shouldGetAppointmentById() {
        Appointment appointment = makeAppointment();

        when(repository.findById(1)).thenReturn(appointment);

        Result<Appointment> result = service.getById(1);

        assertTrue(result.isSuccess());
        assertEquals(1, result.getpayload().getAppointmentId());
    }

    @Test
    void shouldNotFindMissingAppointment() {
        when(repository.findById(99999)).thenReturn(null);

        Result<Appointment> result = service.getById(99999);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.NOT_FOUND, result.getResultType());
    }

    @Test
    void shouldGetAppointmentByUserId() {
        List<Appointment> appointments = List.of(makeAppointment());

        when(repository.findByUserId(1)).thenReturn(appointments);

        Result<List<Appointment>> result = service.getByUserId(1);

        assertTrue(result.isSuccess());
        assertEquals(1, result.getpayload().size());
    }

    @Test
    void shouldGetAppointmentsByBarberAndDate() {
        List<Appointment> appointments = List.of(makeAppointment());

        LocalDate date = LocalDate.now().plusDays(1);

        when(repository.findByBarberIdAndDate(1, date)).thenReturn(appointments);

        Result<List<Appointment>> result = service.getByBarberAndDate(1, date);

        assertTrue(result.isSuccess());
        assertEquals(1, result.getpayload().size());
    }

    @Test
    void shouldAddAppointment() {
        Appointment appointment = makeAppointment();
        User user = makeUser();

        when(repository.findByBarberIdAndDate(anyInt(), any())).thenReturn(List.of());
        when(repository.add(any())).thenReturn(appointment);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        Result<Appointment> result = service.add(appointment);

        assertTrue(result.isSuccess());

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void shouldCancelAppointment() {
        Appointment appointment = makeAppointment();
        User user = makeUser();

        when(repository.findById(1)).thenReturn(appointment);
        when(repository.update(any())).thenReturn(true);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        Result<Void> result = service.cancel(1);

        assertTrue(result.isSuccess());

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void shouldUpdateAppointment() {
        Appointment appointment = makeAppointment();
        User user = makeUser();

        when(repository.update(any())).thenReturn(true);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        Result<Appointment> result = service.update(appointment);

        assertTrue(result.isSuccess());
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    private Appointment makeAppointment() {
        Appointment appointment = new Appointment();

        appointment.setAppointmentId(1);
        appointment.setUserId(1);
        appointment.setBarberId(1);
        appointment.setServiceId(1);
        appointment.setAppointmentDatetime(LocalDateTime.now().plusDays(1));
        appointment.setStatus("BOOKED");
        return appointment;
    }

    private User makeUser() {
        User user = new User();

        user.setUserId(1);
        user.setEmail("test@test/com");
        return user;
    }
}