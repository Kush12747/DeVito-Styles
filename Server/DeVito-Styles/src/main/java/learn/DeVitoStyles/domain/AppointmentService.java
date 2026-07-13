package learn.DeVitoStyles.domain;

import learn.DeVitoStyles.data.*;
import learn.DeVitoStyles.models.Appointment;
import learn.DeVitoStyles.models.Barber;
import learn.DeVitoStyles.models.User;
import org.springframework.stereotype.Service;
import learn.DeVitoStyles.google.GoogleCalendarService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.time.format.DateTimeFormatter;

@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final GoogleCalendarService googleCalendarService;
    private final BarberRepository barberRepository;
    private final EmailService emailService;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MMMM d, yyyy 'at' h:mm a");

    public AppointmentService(AppointmentRepository appointmentRepository, ReviewRepository reviewRepository, UserRepository userRepository, ServiceRepository serviceRepository, EmailService emailService, GoogleCalendarService googleCalendarService, BarberRepository barberRepository) {
        this.appointmentRepository = appointmentRepository;
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.serviceRepository = serviceRepository;
        this.emailService = emailService;
        this.googleCalendarService = googleCalendarService;
        this.barberRepository = barberRepository;
    }

    public Result<List<Appointment>> getAll() {
        Result<List<Appointment>> result = new Result<>();

        List<Appointment> appointments = appointmentRepository.findAll();

        appointments.forEach(this::populateHasReview);

        result.setpayload(appointments);
        return result;
    }

    public Result<Appointment> getById(int appointmentId) {
        Result<Appointment> result = new Result<>();

        if (appointmentId <= 0) {
            result.addErrorMessage("Appointment ID must be greater than 0", ResultType.INVALID);
            return result;
        }

        Appointment appointment = appointmentRepository.findById(appointmentId);

        populateHasReview(appointment);

        if (appointment == null) {
            result.addErrorMessage("Appointment not found", ResultType.NOT_FOUND);
            return result;
        }

        result.setpayload(appointment);
        return result;
    }

    public Result<List<Appointment>> getByUserId(int userId) {
        Result<List<Appointment>> result = new Result<>();

        if (userId <= 0) {
            result.addErrorMessage("User ID must be valid", ResultType.INVALID);
            return result;
        }

        List<Appointment> appointments = appointmentRepository.findByUserId(userId);

        appointments.forEach(this::populateHasReview);

        result.setpayload(appointments);
        return result;
    }

    public Result<List<Appointment>> getByBarberAndDate(int barberId, LocalDate date) {
        Result<List<Appointment>> result = new Result<>();

        if (barberId <= 0) {
            result.addErrorMessage("Barber ID must be valid", ResultType.INVALID);
            return result;
        }

        if (date == null) {
            result.addErrorMessage("Date is required", ResultType.INVALID);
            return result;
        }

        List<Appointment> appointments = appointmentRepository.findByBarberIdAndDate(barberId, date);

        appointments.forEach(this::populateHasReview);

        result.setpayload(appointments);
        return result;
    }

    public Result<Appointment> add(Appointment appointment) throws Exception {
        Result<Appointment> result = validateAppointment(appointment);

        if (!result.isSuccess()) {
            return result;
        }

        if (appointment.getAppointmentDatetime().isBefore(LocalDateTime.now())) {
            result.addErrorMessage("Appointment cannot be in the past", ResultType.INVALID);
            return result;
        }

        if (appointmentRepository.existsByBarberIdAndAppointmentDatetime(
                appointment.getBarberId(),
                appointment.getAppointmentDatetime())) {

            result.addErrorMessage("Time slot already booked", ResultType.INVALID);
            return result;
        }

        appointment.setStatus("BOOKED");

        User user = userRepository.findById(appointment.getUserId()).orElse(null);

        learn.DeVitoStyles.models.Service service = serviceRepository.findById(appointment.getServiceId());

        Barber barber = barberRepository.findById(appointment.getBarberId());

        if (user == null || service == null || barber == null) {
            result.addErrorMessage("Unable to load appointment details", ResultType.INVALID);
            return result;
        }

        String customerName = user.getFirstName() + " " + user.getLastName();
        String barberName = barber.getFirstName() + " " + barber.getLastName();

        try {

            String googleEventId = googleCalendarService.createAppointment(
                    customerName,
                    user.getEmail(),
                    barberName,
                    service.getName(),
                    appointment.getAppointmentDatetime(),
                    service.getDurationMinutes());

            appointment.setGoogleEventId(googleEventId);

        } catch (Exception e) {
            result.addErrorMessage("Google Calendar event creation failed", ResultType.INVALID);
            return result;
        }

        Appointment created = appointmentRepository.add(appointment);

        if (created == null) {
            result.addErrorMessage("Failed to create appointment", ResultType.INVALID);
            return result;
        }

        // Get user email from DB
        sendEmail(created.getUserId(), "CONFIRM", created);

        result.setpayload(created);
        return result;
    }

    public Result<Void> cancel(int appointmentId) throws IOException {
        Result<Void> result = new Result<>();

        Appointment existing = appointmentRepository.findById(appointmentId);

        if (existing == null) {
            result.addErrorMessage("Appointment not found", ResultType.NOT_FOUND);
            return result;
        }

        existing.setStatus("CANCELLED");

        googleCalendarService.deleteAppointment(existing.getGoogleEventId());

        boolean success = appointmentRepository.update(existing);

        if (!success) {
            result.addErrorMessage("Failed to cancel appointment", ResultType.INVALID);
            return result;
        }

        sendEmail(existing.getUserId(), "CANCEL", existing);

        return result;
    }

    public Result<Appointment> update(Appointment appointment) {
        Result<Appointment> result = validateAppointment(appointment);

        if (!result.isSuccess()) {
            return result;
        }

        if (appointment.getAppointmentId() <= 0) {
            result.addErrorMessage("Appointment ID is required", ResultType.INVALID);
            return result;
        }

        // Prevent Double booking
        if (hasConflict(appointment)) {
            result.addErrorMessage("Time slot already booked", ResultType.INVALID);
            return result;
        }
//        if (appointmentRepository.existsByBarberIdAndAppointmentDatetime(
//                appointment.getBarberId(),
//                appointment.getAppointmentDatetime())) {
//
//            result.addErrorMessage("Time slot already booked", ResultType.INVALID);
//            return result;
//        }

        boolean success = appointmentRepository.update(appointment);

        if (!success) {
            result.addErrorMessage("Appointment not found", ResultType.NOT_FOUND);
            return result;
        }

        sendEmail(appointment.getUserId(), "UPDATE", appointment);

        result.setpayload(appointment);
        return result;
    }

    private Result<Appointment> validateAppointment(Appointment appointment) {
        Result<Appointment> result = new Result<>();

        if (appointment == null) {
            result.addErrorMessage("Appointment cannot be null", ResultType.INVALID);
            return result;
        }

        if (appointment.getUserId() <= 0 || appointment.getBarberId() <= 0 || appointment.getServiceId() <= 0) {
            result.addErrorMessage("Invalid IDs provided", ResultType.INVALID);
            return result;
        }

        if (appointment.getAppointmentDatetime() == null) {
            result.addErrorMessage("Appointment datetime is required", ResultType.INVALID);
            return result;
        }

        return result;
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(FORMATTER);
    }

    private void sendEmail(int userId, String subjectType, Appointment appointment) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty() || user.get().getEmail() == null) {
            return;
        }

        String formattedDateTime = formatDateTime(appointment.getAppointmentDatetime());

        String message = switch (subjectType) {
            case "CONFIRM" ->
                    "Your appointment is confirmed.\n\n" +
                            "Date/Time: " + formattedDateTime +
                            "\nStatus: " + appointment.getStatus() +
                            "\n\nThank you for choosing DeVito Styles.";

            case "UPDATE" ->
                    "Your appointment has been updated.\n\n" +
                            "New Date/Time: " + formattedDateTime +
                            "\n\nStatus: " + appointment.getStatus();

            case "CANCEL" ->
                    "Your appointment has been cancelled.\n\n" +
                            "Date/Time: " + formattedDateTime +
                            "\nStatus: " + appointment.getStatus();

            default -> "";
        };

        switch (subjectType) {

            case "CONFIRM" -> emailService.sendAppointmentConfirmation(
                    user.get().getEmail(),
                    message
            );

            case "UPDATE" -> emailService.sendUpdatedAppointmentEmail(
                    user.get().getEmail(),
                    message
            );

            case "CANCEL" -> emailService.sendCancellationEmail(
                    user.get().getEmail(),
                    message
            );
        }
    }

    private void populateHasReview(Appointment appointment) {

        if (appointment == null) {
            return;
        }

        appointment.setHasReview(
                reviewRepository.findByAppointmentId(
                        appointment.getAppointmentId()
                ) != null
        );
    }

    private boolean hasConflict(Appointment appointment) {
        LocalDate date = appointment.getAppointmentDatetime().toLocalDate();

        List<Appointment> existing = appointmentRepository.findByBarberIdAndDate(appointment.getBarberId(), date);

        return existing.stream()
                .anyMatch(a -> a.getAppointmentDatetime()
                        .equals(appointment.getAppointmentDatetime()) &&
                        a.getAppointmentId() != appointment.getAppointmentId());
    }
}