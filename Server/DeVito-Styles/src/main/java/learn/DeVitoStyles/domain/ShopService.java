package learn.DeVitoStyles.domain;

import learn.DeVitoStyles.data.interfaces.ServiceRepository;
import learn.DeVitoStyles.models.Service;

import java.math.BigDecimal;
import java.util.List;

@org.springframework.stereotype.Service
public class ShopService {
    private final ServiceRepository repository;

    public ShopService(ServiceRepository repository) {
        this.repository = repository;
    }

    // =========================
    // FIND ALL
    // =========================
    public Result<List<Service>> findAll() {
        Result<List<Service>> result = new Result<>();

        List<Service> services = repository.findAll();

        result.setpayload(services);
        return result;
    }

    // =========================
    // FIND BY ID
    // =========================
    public Result<Service> findById(int serviceId) {
        Result<Service> result = new Result<>();

        if (serviceId <= 0) {
            result.addErrorMessage("Service ID must be greater than 0", ResultType.INVALID);
            return result;
        }

        Service service = repository.findById(serviceId);

        if (service == null) {
            result.addErrorMessage("Service not found", ResultType.NOT_FOUND);
            return result;
        }

        result.setpayload(service);
        return result;
    }

    // =========================
    // CREATE
    // =========================
    public Result<Service> create(Service service) {
        Result<Service> result = validate(service);

        if (!result.isSuccess()) {
            return result;
        }

        Service created = repository.create(service);
        result.setpayload(created);

        return result;
    }

    // =========================
    // UPDATE
    // =========================
    public Result<Service> update(Service service) {
        Result<Service> result = validate(service);

        if (!result.isSuccess()) {
            return result;
        }

        Service existing = repository.findById(service.getServiceId());

        if (existing == null) {
            result.addErrorMessage("Service not found", ResultType.NOT_FOUND);
            return result;
        }

        boolean updated = repository.update(service);

        if (!updated) {
            result.addErrorMessage("update failed", ResultType.INVALID);
        }

        result.setpayload(service);
        return result;

    }

    // =========================
    // DELETE
    // =========================
    public Result<Void> deleteById(int serviceId) {
        Result<Void> result = new Result<>();

        if (serviceId <= 0) {
            result.addErrorMessage("Invalid service ID", ResultType.INVALID);
            return result;
        }

        Service existing = repository.findById(serviceId);

        if (existing == null) {
            result.addErrorMessage("Service not found", ResultType.NOT_FOUND);
            return result;
        }

        boolean delete = repository.deleteById(serviceId);

        if (!delete) {
            result.addErrorMessage("Delete failed", ResultType.INVALID);
        }

        return result;
    }

    private Result<Service> validate(Service service) {
        Result<Service> result = new Result<>();

        if (service == null) {
            result.addErrorMessage("Service cannot be null", ResultType.INVALID);
            return result;
        }

        if (service.getName() == null || service.getName().isBlank()) {
            result.addErrorMessage("Name is required", ResultType.INVALID);
        }

        if (service.getDurationMinutes() <= 0) {
            result.addErrorMessage("Duration must be greater than 0", ResultType.INVALID);
        }

        if (service.getPrice() == null || service.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            result.addErrorMessage("Price must be greater then 0", ResultType.INVALID);
        }

        return result;
    }
}