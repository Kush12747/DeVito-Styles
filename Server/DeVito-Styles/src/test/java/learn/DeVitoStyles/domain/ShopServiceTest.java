package learn.DeVitoStyles.domain;

import learn.DeVitoStyles.data.interfaces.ServiceRepository;
import learn.DeVitoStyles.models.Service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShopServiceTest {

    @Mock private ServiceRepository repository;

    @InjectMocks private ShopService service;

    // =========================
    // FIND ALL
    // =========================

    @Test
    void shouldFindAll() {
        when(repository.findAll()).thenReturn(List.of(makeService(1)));

        Result<List<Service>> result = service.findAll();

        assertTrue(result.isSuccess());
        assertEquals(1, result.getpayload().size());
    }

    // =========================
    // FIND BY ID
    // =========================

    @Test
    void shouldFindById() {
        when(repository.findById(1)).thenReturn(makeService(1));

        Result<Service> result = service.findById(1);

        assertTrue(result.isSuccess());
        assertEquals("Haircut", result.getpayload().getName());
    }

    @Test
    void shouldNotFindByInvalidId() {
        Result<Service> result = service.findById(0);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Service ID must be greater than 0"), result.getMessages());
        verifyNoInteractions(repository);
    }

    @Test
    void shouldNotFindById() {
        when(repository.findById(999)).thenReturn(null);

        Result<Service> result = service.findById(999);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.NOT_FOUND, result.getResultType());
        assertEquals(List.of("Service not found"), result.getMessages());
        assertNull(result.getpayload());
    }

    // =========================
    // CREATE
    // =========================

    @Test
    void shouldCreate() {
        Service input = makeService(0);
        Service saved = makeService(1);
        when(repository.create(input)).thenReturn(saved);

        Result<Service> result = service.create(input);

        assertTrue(result.isSuccess());
        assertEquals(1, result.getpayload().getServiceId());
    }

    @Test
    void shouldNotCreateNullService() {
        Result<Service> result = service.create(null);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Service cannot be null"), result.getMessages());
        verifyNoInteractions(repository);
    }

    @Test
    void shouldNotCreateEmptyService() {
        Result<Service> result = service.create(new Service());

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of(
                "Name is required",
                "Duration must be greater than 0",
                "Price must be greater then 0"
        ), result.getMessages());
        verify(repository, never()).create(any());
    }

    @Test
    void shouldNotCreateBlankName() {
        Service input = makeService(0);
        input.setName("  ");

        assertCreateInvalid(input, "Name is required");
    }

    @Test
    void shouldNotCreateZeroDuration() {
        Service input = makeService(0);
        input.setDurationMinutes(0);

        assertCreateInvalid(input, "Duration must be greater than 0");
    }

    @Test
    void shouldNotCreateZeroPrice() {
        Service input = makeService(0);
        input.setPrice(BigDecimal.ZERO);

        assertCreateInvalid(input, "Price must be greater then 0");
    }

    @Test
    void shouldNotCreateNegativePrice() {
        Service input = makeService(0);
        input.setPrice(new BigDecimal("-5.00"));

        assertCreateInvalid(input, "Price must be greater then 0");
    }

    // =========================
    // UPDATE
    // =========================

    @Test
    void shouldUpdate() {
        Service existing = makeService(1);
        existing.setName("Old name");

        Service update = makeService(1);
        update.setName("new name");

        when(repository.findById(1)).thenReturn(existing);
        when(repository.update(update)).thenReturn(true);

        Result<Service> result = service.update(update);

        assertTrue(result.isSuccess());
        assertEquals("new name", result.getpayload().getName());
        verify(repository).update(update);
    }

    @Test
    void shouldNotUpdateInvalidService() {
        Service update = makeService(1);
        update.setName(null);

        Result<Service> result = service.update(update);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Name is required"), result.getMessages());
        verifyNoInteractions(repository);
    }

    @Test
    void shouldNotUpdateMissingService() {
        Service update = makeService(999);
        when(repository.findById(999)).thenReturn(null);

        Result<Service> result = service.update(update);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.NOT_FOUND, result.getResultType());
        assertEquals(List.of("Service not found"), result.getMessages());
        verify(repository, never()).update(any());
    }

    @Test
    void shouldNotUpdateWhenRepositoryUpdateFails() {
        Service update = makeService(1);
        when(repository.findById(1)).thenReturn(makeService(1));
        when(repository.update(update)).thenReturn(false);

        Result<Service> result = service.update(update);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("update failed"), result.getMessages());
    }

    // =========================
    // DELETE
    // =========================

    @Test
    void shouldDeleteById() {
        when(repository.findById(1)).thenReturn(makeService(1));
        when(repository.deleteById(1)).thenReturn(true);

        Result<Void> result = service.deleteById(1);

        assertTrue(result.isSuccess());
        verify(repository).deleteById(1);
    }

    @Test
    void shouldNotDeleteByInvalidId() {
        Result<Void> result = service.deleteById(0);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Invalid service ID"), result.getMessages());
        verifyNoInteractions(repository);
    }

    @Test
    void shouldNotDeleteById() {
        when(repository.findById(999)).thenReturn(null);

        Result<Void> result = service.deleteById(999);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.NOT_FOUND, result.getResultType());
        verify(repository, never()).deleteById(anyInt());
    }

    @Test
    void shouldNotDeleteWhenRepositoryDeleteFails() {
        when(repository.findById(1)).thenReturn(makeService(1));
        when(repository.deleteById(1)).thenReturn(false);

        Result<Void> result = service.deleteById(1);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Delete failed"), result.getMessages());
    }

    // =========================
    // HELPERS
    // =========================

    private void assertCreateInvalid(Service input, String expectedMessage) {
        Result<Service> result = service.create(input);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of(expectedMessage), result.getMessages());
        verify(repository, never()).create(any());
    }

    private Service makeService(int serviceId) {
        Service s = new Service();
        s.setServiceId(serviceId);
        s.setName("Haircut");
        s.setDurationMinutes(30);
        s.setPrice(new BigDecimal("25.00"));
        s.setDescription("Classic cut");
        return s;
    }
}
