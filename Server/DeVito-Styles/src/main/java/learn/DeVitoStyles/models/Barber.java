package learn.DeVitoStyles.models;

import java.util.Objects;

public class Barber {
    private int barberId;
    private String firstName;
    private String lastName;
    private String availabilityStatus;
    private String specialization;
    private String imageUrl;
    private String title;
    private String bio;
    private Integer startYear;
    private String instagramUrl;
    private int displayOrder;
    private boolean isActive;

    public Barber() {
    }

    public Barber(int barberId, String firstName, String lastName, String availabilityStatus, String specialization, String imageUrl, String title, String bio, Integer startYear, String instagramUrl, int displayOrder, boolean isActive) {
        this.barberId = barberId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.availabilityStatus = availabilityStatus;
        this.specialization = specialization;
        this.imageUrl = imageUrl;
        this.title = title;
        this.bio = bio;
        this.startYear = startYear;
        this.instagramUrl = instagramUrl;
        this.displayOrder = displayOrder;
        this.isActive = isActive;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Integer getStartYear() {
        return startYear;
    }

    public void setStartYear(Integer startYear) {
        this.startYear = startYear;
    }

    public String getInstagramUrl() {
        return instagramUrl;
    }

    public void setInstagramUrl(String instagramUrl) {
        this.instagramUrl = instagramUrl;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
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
        return barberId == barber.barberId && startYear == barber.startYear && displayOrder == barber.displayOrder && isActive == barber.isActive && Objects.equals(firstName, barber.firstName) && Objects.equals(lastName, barber.lastName) && Objects.equals(availabilityStatus, barber.availabilityStatus) && Objects.equals(specialization, barber.specialization) && Objects.equals(imageUrl, barber.imageUrl) && Objects.equals(title, barber.title) && Objects.equals(bio, barber.bio) && Objects.equals(instagramUrl, barber.instagramUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(barberId, firstName, lastName, availabilityStatus, specialization, imageUrl, title, bio, startYear, instagramUrl, displayOrder, isActive);
    }
}
