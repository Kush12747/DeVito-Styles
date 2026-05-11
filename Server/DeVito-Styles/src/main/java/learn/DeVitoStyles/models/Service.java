package learn.DeVitoStyles.models;

import java.math.BigDecimal;
import java.util.Objects;

public class Service {
    private int serviceId;
    private String name;
    private int durationMinutes;
    private BigDecimal price;
    private String description;

    public Service() {
    }

    public Service(int serviceId, String name, int durationMinutes, BigDecimal price, String description) {
        this.serviceId = serviceId;
        this.name = name;
        this.durationMinutes = durationMinutes;
        this.price = price;
        this.description = description;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Service service = (Service) o;
        return serviceId == service.serviceId && durationMinutes == service.durationMinutes &&
                Objects.equals(name, service.name) && Objects.equals(price, service.price) &&
                Objects.equals(description, service.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceId, name, durationMinutes, price, description);
    }
}
