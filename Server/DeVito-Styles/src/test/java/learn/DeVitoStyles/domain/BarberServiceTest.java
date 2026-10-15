package learn.DeVitoStyles.domain;

import learn.DeVitoStyles.data.interfaces.BarberRepository;
import learn.DeVitoStyles.models.Barber;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BarberServiceTest {

    @Mock private BarberRepository repository;

    @InjectMocks private BarberService service;

    // =========================
    // FIND ALL
    // =========================

    @Test
    void shouldFindAll() {
        when(repository.findAll()).thenReturn(List.of(makeBarber(1), makeBarber(2)));

        Result<List<Barber>> result = service.findAll();

        assertTrue(result.isSuccess());
        assertEquals(2, result.getpayload().size());
    }

    // =========================
    // FIND BY ID
    // =========================

    @Test
    void shouldFindBarberById() {
        Barber barber = makeBarber(1);
        when(repository.findById(1)).thenReturn(barber);

        Result<Barber> result = service.findById(1);

        assertTrue(result.isSuccess());
        assertSame(barber, result.getpayload());
    }

    @Test
    void shouldNotFindBarberByInvalidId() {
        Result<Barber> result = service.findById(0);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Barber ID must be greater than 0"), result.getMessages());
        verifyNoInteractions(repository);
    }

    @Test
    void shouldNotFindBarberById() {
        when(repository.findById(999)).thenReturn(null);

        Result<Barber> result = service.findById(999);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.NOT_FOUND, result.getResultType());
        assertEquals(List.of("Barber not found"), result.getMessages());
        assertNull(result.getpayload());
    }

    // =========================
    // CREATE
    // =========================

    @Test
    void shouldCreateBarber() {
        Barber barber = makeBarber(0);
        Barber saved = makeBarber(1);
        when(repository.add(barber)).thenReturn(saved);

        Result<Barber> result = service.create(barber);

        assertTrue(result.isSuccess());
        assertEquals(1, result.getpayload().getBarberId());
    }

    @Test
    void shouldNotCreateNullBarber() {
        Result<Barber> result = service.create(null);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Barber cannot be null"), result.getMessages());
        verifyNoInteractions(repository);
    }

    @Test
    void shouldNotCreateBlankFirstNameBarber() {
        Barber barber = makeBarber(0);
        barber.setFirstName("");

        assertCreateInvalid(barber, "First name is required");
    }

    @Test
    void shouldNotCreateBlankLastNameBarber() {
        Barber barber = makeBarber(0);
        barber.setLastName("   ");

        assertCreateInvalid(barber, "Last name is required");
    }

    @Test
    void shouldNotCreateNullSpecializationBarber() {
        Barber barber = makeBarber(0);
        barber.setSpecialization(null);

        assertCreateInvalid(barber, "Specialization is required");
    }

    @Test
    void shouldNotCreateNullAvailabilityBarber() {
        Barber barber = makeBarber(0);
        barber.setAvailabilityStatus(null);

        assertCreateInvalid(barber, "Availability is required");
    }

    @Test
    void shouldReportAllValidationErrorsOnCreate() {
        Result<Barber> result = service.create(new Barber());

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of(
                "First name is required",
                "Last name is required",
                "Specialization is required",
                "Availability is required"
        ), result.getMessages());
        verify(repository, never()).add(any());
    }

    // =========================
    // UPDATE
    // =========================

    @Test
    void shouldUpdateBarber() {
        Barber barber = makeBarber(1);
        when(repository.findById(1)).thenReturn(barber);
        when(repository.update(barber)).thenReturn(true);

        Result<Barber> result = service.update(barber);

        assertTrue(result.isSuccess());
        assertEquals(1, result.getpayload().getBarberId());
        verify(repository).update(barber);
    }

    @Test
    void shouldNotUpdateInvalidBarber() {
        Barber barber = makeBarber(1);
        barber.setFirstName(null);

        Result<Barber> result = service.update(barber);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("First name is required"), result.getMessages());
        verifyNoInteractions(repository);
    }

    @Test
    void shouldNotUpdateNullBarber() {
        Result<Barber> result = service.update(null);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        verifyNoInteractions(repository);
    }

    @Test
    void shouldNotUpdateMissingBarber() {
        Barber barber = makeBarber(999);
        when(repository.findById(999)).thenReturn(null);

        Result<Barber> result = service.update(barber);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.NOT_FOUND, result.getResultType());
        assertEquals(List.of("Barber not found"), result.getMessages());
        verify(repository, never()).update(any());
    }

    @Test
    void shouldNotUpdateBarberWithoutId() {
        Barber barber = makeBarber(0);

        Result<Barber> result = service.update(barber);

        assertFalse(result.isSuccess());
        verify(repository, never()).update(any());
    }

    @Test
    void shouldNotUpdateWhenRepositoryUpdateFails() {
        Barber barber = makeBarber(1);
        when(repository.findById(1)).thenReturn(barber);
        when(repository.update(barber)).thenReturn(false);

        Result<Barber> result = service.update(barber);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertTrue(result.getMessages().contains("update failed"));
    }

    // =========================
    // DELETE
    // =========================

    @Test
    void shouldDeleteBarber() {
        when(repository.findById(1)).thenReturn(makeBarber(1));
        when(repository.deleteById(1)).thenReturn(true);

        Result<Void> result = service.delete(1);

        assertTrue(result.isSuccess());
        assertTrue(result.getMessages().isEmpty());
        verify(repository).deleteById(1);
    }

    @Test
    void shouldNotDeleteInvalidId() {
        Result<Void> result = service.delete(0);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Invalid barber ID"), result.getMessages());
        verifyNoInteractions(repository);
    }

    @Test
    void shouldNotDeleteNotFound() {
        when(repository.findById(999)).thenReturn(null);

        Result<Void> result = service.delete(999);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.NOT_FOUND, result.getResultType());
        verify(repository, never()).deleteById(anyInt());
    }

    @Test
    void shouldNotDeleteWhenRepositoryDeleteFails() {
        when(repository.findById(1)).thenReturn(makeBarber(1));
        when(repository.deleteById(1)).thenReturn(false);

        Result<Void> result = service.delete(1);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Delete failed"), result.getMessages());
    }

    // =========================
    // HELPERS
    // =========================

    private void assertCreateInvalid(Barber barber, String expectedMessage) {
        Result<Barber> result = service.create(barber);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of(expectedMessage), result.getMessages());
        verify(repository, never()).add(any());
    }

    private Barber makeBarber(int barberId) {
        Barber barber = new Barber();
        barber.setBarberId(barberId);
        barber.setFirstName("Tony");
        barber.setLastName("DeVito");
        barber.setAvailabilityStatus("AVAILABLE");
        barber.setSpecialization("FADES");
        return barber;
    }
}
