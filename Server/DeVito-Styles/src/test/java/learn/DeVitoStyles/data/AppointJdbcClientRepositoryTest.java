package learn.DeVitoStyles.data;

import learn.DeVitoStyles.models.Appointment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class AppointJdbcClientRepositoryTest {

    @Autowired
    private AppointJdbcClientRepository repository;

    @Autowired
    private JdbcClient jdbcClient;

    @BeforeEach
    void setup() {
        jdbcClient.sql("call set_known_good_state();").update();
    }

    private Appointment createAppointment() {
        Appointment a = new Appointment();
        a.setUserId(1);
        a.setBarberId(1);
        a.setServiceId(1);
        a.setAppointmentDatetime(LocalDateTime.now().plusDays(1));
        a.setStatus("BOOKED");
        return a;
    }

    @Test
    void shouldFindAllAppointments() {
        List<Appointment> result = repository.findAll();

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void findAppointmentById() {
        Appointment insert = repository.add(createAppointment());

        Appointment found = repository.findById(insert.getAppointmentId());

        assertNotNull(found);
        assertEquals(insert.getAppointmentId(), found.getAppointmentId());
    }

    @Test
    void shouldNotFindInvalidAppointmentId() {
        Appointment found = repository.findById(9999);

        assertNull(found);
    }

    @Test
    void findByUserIdAndReturnAppointment() {
        Appointment insert = repository.add(createAppointment());

        List<Appointment> result = repository.findByUserId(1);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.get(0).getUserId());
    }

    @Test
    void findByBarberIdAndDateAndReturnAppointment() {
        Appointment insert = repository.add(createAppointment());

        LocalDate date = insert.getAppointmentDatetime().toLocalDate();

        List<Appointment> result = repository.findByBarberIdAndDate(1, date);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void shouldAddAppointment() {
        Appointment result = repository.add(createAppointment());

        assertNotNull(result);
        assertTrue(result.getAppointmentId() > 0);
    }

    @Test
    void shouldUpdateAppointment() {
        Appointment insert = repository.add(createAppointment());

        insert.setStatus("CANCELLED");

        boolean result = repository.update(insert);

        assertTrue(result);

        Appointment updated = repository.findById(insert.getAppointmentId());
        assertEquals("CANCELLED", updated.getStatus());
    }

    @Test
    void shouldNotUpdateInvalidId() {
        Appointment fake = createAppointment();
        fake.setAppointmentId(999999);

        boolean result = repository.update(fake);

        assertFalse(result);
    }
}