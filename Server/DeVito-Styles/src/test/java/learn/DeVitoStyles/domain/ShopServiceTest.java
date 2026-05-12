package learn.DeVitoStyles.domain;

import learn.DeVitoStyles.data.ServiceRepository;
import learn.DeVitoStyles.models.Service;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ShopServiceTest {

    @Autowired
    ShopService service;

    @MockBean
    ServiceRepository repository;

    @Test
    void shouldFindAll() {
        Service service1 = new Service();
        service1.setServiceId(1);
        service1.setName("Haircut");

        when(repository.findAll()).thenReturn(List.of(service1));

        Result<List<Service>> result = service.findAll();

        assertTrue(result.isSuccess());
        assertEquals(1, result.getpayload().size());
    }

    @Test
    void shouldFindById() {
        Service s1 = new Service();
        s1.setServiceId(1);
        s1.setName("Haircut");

        when(repository.findById(1)).thenReturn(s1);

        Result<Service> result = service.findById(1);

        assertTrue(result.isSuccess());
        assertEquals("Haircut", result.getpayload().getName());
    }

    @Test
    void shouldNotFindById() {
        when(repository.findById(999)).thenReturn(null);

        Result<Service> result = service.findById(999);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.NOT_FOUND, result.getResultType());
    }

    @Test
    void shouldCreate() {
        Service input = new Service();
        input.setName("Beard Trim");
        input.setDurationMinutes(30);
        input.setPrice(new BigDecimal("15.00"));
        input.setDescription("Trim Beard");

        Service saved = new Service();
        saved.setServiceId(1);
        saved.setName("Beard Trim");

        when(repository.create(input)).thenReturn(saved);

        Result<Service> result = service.create(input);

        assertTrue(result.isSuccess());
        assertEquals(1, result.getpayload().getServiceId());
    }

    @Test
    void shouldNotCreateInvalidService() {
        Service input = new Service();

        Result<Service> result = service.create(input);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
    }

    @Test
    void shouldUpdate() {
        Service existing = new Service();
        existing.setServiceId(1);
        existing.setName("Old name");

        Service update = new Service();
        update.setServiceId(1);
        update.setName("new name");
        update.setDurationMinutes(30);
        update.setPrice(new BigDecimal("25.00"));
        update.setDescription("updated");

        when(repository.findById(1)).thenReturn(existing);
        when(repository.update(update)).thenReturn(true);

        Result<Service> result = service.update(update);

        assertTrue(result.isSuccess());
        assertEquals("new name", result.getpayload().getName());
    }

    @Test
    void shouldNotUpdate() {
        Service update = new Service();
        update.setServiceId(999);
        update.setName("Ghost");
        update.setDurationMinutes(20);
        update.setPrice(new BigDecimal("10.00"));

        when(repository.findById(999)).thenReturn(null);

        Result<Service> result = service.update(update);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.NOT_FOUND, result.getResultType());
    }

    @Test
    void shouldDeleteById() {
        Service existing = new Service();
        existing.setServiceId(1);

        when(repository.findById(1)).thenReturn(existing);
        when(repository.deleteById(1)).thenReturn(true);

        Result<Void> result = service.deleteById(1);

        assertTrue(result.isSuccess());
    }

    @Test
    void shouldNotDeleteById() {

        when(repository.findById(999)).thenReturn(null);

        Result<Void> result = service.deleteById(999);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.NOT_FOUND, result.getResultType());
    }
}