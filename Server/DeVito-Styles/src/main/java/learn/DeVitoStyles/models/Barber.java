package learn.DeVitoStyles.models;

import java.util.Objects;

public class Barber {
    private int barberId;
    private String firstName;
    private String lastName;
    private String availabilityStatus;
    private String specialization;

    public Barber() {
    }

    public Barber(int barberId, String firstName, String lastName, String availabilityStatus, String specialization) {
        this.barberId = barberId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.availabilityStatus = availabilityStatus;
        this.specialization = specialization;
    }

    public int getBarberId() {
        return barberId;
    }

    public void setBarberId(int barberId) {
        this.barberId = barberId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(String availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Barber barber = (Barber) o;
        return barberId == barber.barberId && Objects.equals(firstName, barber.firstName) &&
                Objects.equals(lastName, barber.lastName) &&
                Objects.equals(availabilityStatus, barber.availabilityStatus) &&
                Objects.equals(specialization, barber.specialization);
    }

    @Override
    public int hashCode() {
        return Objects.hash(barberId, firstName, lastName, availabilityStatus, specialization);
    }
}
