package learn.DeVitoStyles.domain;

import learn.DeVitoStyles.data.BarberRepository;
import learn.DeVitoStyles.models.Barber;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BarberService {
    private final BarberRepository repository;

    public BarberService(BarberRepository repository) {
        this.repository = repository;
    }

    // =========================
    // FIND ALL
    // =========================
    public Result<List<Barber>> findAll() {
        Result<List<Barber>> result = new Result<>();

        List<Barber> barbers = repository.findAll();

        result.setpayload(barbers);
        return result;
    }

    // =========================
    // FIND BY ID
    // =========================
    public Result<Barber> findById(int barberId) {
        Result<Barber> result = new Result<>();

        if (barberId <= 0) {
            result.addErrorMessage("Barber ID must be greater than 0", ResultType.INVALID);
            return result;
        }

        Barber barber = repository.findById(barberId);

        if (barber == null) {
            result.addErrorMessage("Barber not found", ResultType.NOT_FOUND);
            return result;
        }

        result.setpayload(barber);
        return result;
    }

    // =========================
    // CREATE
    // =========================
    public Result<Barber> create(Barber barber) {
        Result<Barber> result = validate(barber);

        if (!result.isSuccess()) {
            return result;
        }

        Barber created = repository.add(barber);
        result.setpayload(created);

        return result;
    }

    // =========================
    // UPDATE
    // =========================
    public Result<Barber> update(Barber barber) {
        Result<Barber> result = validate(barber);

        if (!result.isSuccess()) {
            return result;
        }

        Barber existing = repository.findById(barber.getBarberId());

        if (existing == null) {
            result.addErrorMessage("Barber not found", ResultType.NOT_FOUND);
            return result;
        }

        boolean updated = repository.update(barber);

        if (!updated) {
            result.addErrorMessage("update failed", ResultType.INVALID);
        }

        if (barber.getBarberId() <= 0) {
            result.addErrorMessage("Barber ID is required", ResultType.INVALID);
            return result;
        }

        result.setpayload(barber);
        return result;
    }

    // =========================
    // DELETE
    // =========================
    public Result<Void> delete(int barberId) {
        Result<Void> result = new Result<>();

        if (barberId <= 0) {
            result.addErrorMessage("Invalid barber ID", ResultType.INVALID);
            return result;
        }

        Barber existing = repository.findById(barberId);

        if (existing == null) {
            result.addErrorMessage("Barber not found", ResultType.NOT_FOUND);
            return result;
        }

        boolean delete = repository.deleteById(barberId);

        if (!delete) {
            result.addErrorMessage("Delete failed", ResultType.INVALID);
        }

        return result;
    }

    private Result<Barber> validate(Barber barber) {
        Result<Barber> result = new Result<>();

        if (barber == null) {
            result.addErrorMessage("Barber cannot be null", ResultType.INVALID);
            return result;
        }

        if (barber.getFirstName() == null || barber.getFirstName().isBlank()) {
            result.addErrorMessage("First name is required", ResultType.INVALID);
        }

        if (barber.getLastName() == null || barber.getLastName().isBlank()) {
            result.addErrorMessage("Last name is required", ResultType.INVALID);
        }

        if (barber.getSpecialization() == null || barber.getSpecialization().isBlank()) {
            result.addErrorMessage("Specialization is required", ResultType.INVALID);
        }

        if (barber.getAvailabilityStatus() == null || barber.getAvailabilityStatus().isBlank()) {
            result.addErrorMessage("Availability is required", ResultType.INVALID);
        }

        return result;
    }
}
