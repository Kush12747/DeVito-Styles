package learn.DeVitoStyles.data.repositories;

import learn.DeVitoStyles.models.Barber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class BarberJdbcClientRepositoryTest {

    @Autowired
    private BarberJdbcClientRepository repository;

    @Autowired
    private JdbcClient jdbcClient;

    @BeforeEach
    void setup() {
        jdbcClient.sql("call set_known_good_state();").update();
    }

    @Test
    void shouldFindAll() {
        List<Barber> barberList = repository.findAll();

        assertNotNull(barberList);
        assertFalse(barberList.isEmpty());
    }

    @Test
    void shouldFindById() {
        Barber result = repository.findById(1);

        assertNotNull(result);
        assertEquals(1, result.getBarberId());
    }

    @Test
    void shouldNotFindById() {
        Barber result = repository.findById(999);

        assertNull(result);
    }

    @Test
    void shouldAddBarber() {
        Barber barber = new Barber();
        barber.setFirstName("Tony");
        barber.setLastName("DeVito");
        barber.setAvailabilityStatus("AVAILABLE");
        barber.setSpecialization("FADES");

        Barber result = repository.add(barber);

        assertNotNull(result);
        assertTrue(result.getBarberId() > 0);

        Barber fromDb = repository.findById(result.getBarberId());
        assertEquals("Tony", fromDb.getFirstName());
    }

    @Test
    void shouldUpdateBarber() {
        Barber barber = repository.findById(1);
        barber.setFirstName("updated");
        barber.setSpecialization("Beard");

        boolean updated = repository.update(barber);

        assertTrue(updated);

        Barber updatedBarber = repository.findById(1);
        assertEquals("updated", updatedBarber.getFirstName());
        assertEquals("Beard", updatedBarber.getSpecialization());
    }

    @Test
    void shouldNotUpdateMissingBarber() {
        Barber barber = new Barber();
        barber.setBarberId(9999);
        barber.setFirstName("Ghost");
        barber.setLastName("Barber");
        barber.setAvailabilityStatus("AVAILABLE");
        barber.setSpecialization("FADES");

        boolean result = repository.update(barber);

        assertFalse(result);
    }


    @Test
    void shouldDeleteBarber() {
        Barber barber = new Barber();
        barber.setFirstName("Temp");
        barber.setLastName("Delete");
        barber.setAvailabilityStatus("AVAILABLE");
        barber.setSpecialization("FADES");

        Barber added = repository.add(barber);

        boolean result = repository.deleteById(added.getBarberId());

        assertTrue(result);
    }
}