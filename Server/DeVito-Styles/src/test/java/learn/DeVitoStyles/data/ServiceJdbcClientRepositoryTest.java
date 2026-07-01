package learn.DeVitoStyles.data;

import learn.DeVitoStyles.models.Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class ServiceJdbcClientRepositoryTest {

    @Autowired
    private ServiceJdbcClientRepository repository;

    @Autowired
    private JdbcClient jdbcClient;

    @BeforeEach
    void setup() {
        jdbcClient.sql("call set_known_good_state();").update();
    }

    @Test
    void shouldFindAll() {
        List<Service> serviceList = repository.findAll();

        assertNotNull(serviceList);
        assertFalse(serviceList.isEmpty());
    }

    @Test
    void shouldFindById() {
        Service result = repository.findById(1);

        assertNotNull(result);
        assertEquals(1, result.getServiceId());

    }

    @Test
    void shouldNotFindById() {
        Service result = repository.findById(999);

        assertNull(result);
    }

    @Test
    void shouldCreate() {
        Service service = new Service();

        service.setName("Hair Wash");
        service.setDurationMinutes(5);
        service.setPrice(new BigDecimal("15.00"));
        service.setDescription("Basic hair washing service");

        Service created = repository.create(service);

        assertTrue(created.getServiceId() > 0);
        assertEquals("Hair Wash", created.getName());
    }

    @Test
    void shouldUpdate() {
        Service service = repository.findById(1);
        service.setName("updated");

        boolean updated = repository.update(service);

        assertTrue(updated);
        Service checkUpdated = repository.findById(1);
        assertEquals("updated", checkUpdated.getName());
    }

    @Test
    void shouldNotUpdate() {
        Service service = new Service();

        service.setServiceId(999);
        service.setName("test");
        service.setDurationMinutes(5);
        service.setPrice(new BigDecimal("15.00"));
        service.setDescription("test service");

        boolean updated = repository.update(service);
        assertFalse(updated);
    }

    @Test
    void shouldDeleteById() {
        jdbcClient.sql("DELETE FROM appointment WHERE service_id = 1;")
                .update();

        boolean delete = repository.deleteById(1);
        assertTrue(delete);

        Service service = repository.findById(1);
        assertNull(service);
    }

    @Test
    void shouldNotDeleteById() {
        boolean delete = repository.deleteById(999);
        assertFalse(delete);
    }
}