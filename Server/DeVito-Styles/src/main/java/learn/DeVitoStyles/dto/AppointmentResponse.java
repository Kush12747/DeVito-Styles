package learn.DeVitoStyles.dto;

import java.time.LocalDateTime;

public class AppointmentResponse {

    private int appointmentId;
    private String customerName;
    private String customerEmail;
    private String barberName;
    private String serviceName;
    private int durationMinutes;
    private LocalDateTime appointmentDatetime;
    private String status;
    private String googleCalendarUrl;

    public AppointmentResponse() {
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getBarberName() {
        return barberName;
    }

    public void setBarberName(String barberName) {
        this.barberName = barberName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public LocalDateTime getAppointmentDatetime() {
        return appointmentDatetime;
    }

    public void setAppointmentDatetime(LocalDateTime appointmentDatetime) {
        this.appointmentDatetime = appointmentDatetime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGoogleCalendarUrl() {
        return googleCalendarUrl;
    }

    public void setGoogleCalendarUrl(String googleCalendarUrl) {
        this.googleCalendarUrl = googleCalendarUrl;
    }
}
