package learn.DeVitoStyles.domain;

import learn.DeVitoStyles.data.interfaces.BarberRepository;
import learn.DeVitoStyles.models.Barber;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class BarberServiceTest {

    @Autowired
    BarberService service;

    @MockBean
    BarberRepository repository;

    private Barber makeBarber(int barberID) {
        Barber barber = new Barber();

        barber.setBarberId(barberID);
        barber.setFirstName("Tony");
        barber.setLastName("DeVito");
        barber.setAvailabilityStatus("AVAILABLE");
        barber.setSpecialization("FADES");

        return barber;
    }

    @Test
    void shouldFindAll() {
        List<Barber> expected = List.of(makeBarber(1), makeBarber(2));

        when(repository.findAll()).thenReturn(expected);

        Result<List<Barber>> result = service.findAll();

        assertTrue(result.isSuccess());
        assertEquals(2, result.getpayload().size());
    }

    @Test
    void shouldFindBarberById() {
        Barber barber = makeBarber(1);

        when(repository.findById(barber.getBarberId())).thenReturn(barber);

        Result<Barber> result = service.findById(barber.getBarberId());

        assertTrue(result.isSuccess());
        assertEquals(1, result.getpayload().getBarberId());
    }

    @Test
    void shouldNotFindBarberByInvalidId() {

        Result<Barber> result = service.findById(0);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
    }

    @Test
    void shouldNotFindBarberById() {
        when(repository.findById(999)).thenReturn(null);

        Result<Barber> result = service.findById(999);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.NOT_FOUND, result.getResultType());
    }

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
    }

    @Test
    void shouldNotCreateBlankFirstNameBarber() {

        Barber barber = makeBarber(0);
        barber.setFirstName("");

        Result<Barber> result = service.create(barber);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
    }

    @Test
    void shouldNotCreateBlankLastNameBarber() {

        Barber barber = makeBarber(0);
        barber.setLastName("");

        Result<Barber> result = service.create(barber);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
    }

    @Test
    void shouldNotCreateNullSpecializationBarber() {

        Barber barber = makeBarber(0);
        barber.setSpecialization(null);

        Result<Barber> result = service.create(barber);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
    }

    @Test
    void shouldUpdate_happyPath() {

        Barber barber = makeBarber(1);

        when(repository.findById(1)).thenReturn(barber);
        when(repository.update(barber)).thenReturn(true);

        Result<Barber> result = service.update(barber);

        assertTrue(result.isSuccess());
        assertEquals(1, result.getpayload().getBarberId());
    }

    @Test
    void shouldNotUpdateInvalidBarber() {
        Barber barber = makeBarber(999);

        when(repository.findById(999)).thenReturn(null);

        Result<Barber> result = service.update(barber);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.NOT_FOUND, result.getResultType());
    }

    @Test
    void shouldNotUpdateMissingBarber() {
        Barber barber = makeBarber(999);

        when(repository.findById(999)).thenReturn(null);

        Result<Barber> result = service.update(barber);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.NOT_FOUND, result.getResultType());

    }

    @Test
    void shouldNotUpdate_failedRepositoryUpdate() {

        Barber barber = makeBarber(1);

        when(repository.findById(1)).thenReturn(barber);
        when(repository.update(barber)).thenReturn(false);

        Result<Barber> result = service.update(barber);

        assertFalse(result.isSuccess());
    }

    @Test
    void shouldDeleteBarber() {
        Barber barber = makeBarber(1);

        when(repository.findById(1)).thenReturn(barber);
        when(repository.deleteById(1)).thenReturn(true);

        Result<Void> result = service.delete(1);

        assertTrue(result.isSuccess());
    }

    @Test
    void shouldNotDeleteInvalidId() {
        Result<Void> result = service.delete(0);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
    }

    @Test
    void shouldNotDeleteNotFound() {
        when(repository.findById(999)).thenReturn(null);

        Result<Void> result = service.delete(999);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.NOT_FOUND, result.getResultType());
    }
}