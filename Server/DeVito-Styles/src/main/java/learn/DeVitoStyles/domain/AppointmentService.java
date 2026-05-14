package learn.DeVitoStyles.domain;

import learn.DeVitoStyles.data.AppointmentRepository;
import learn.DeVitoStyles.data.UserRepository;
import learn.DeVitoStyles.models.Appointment;
import learn.DeVitoStyles.models.User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public AppointmentService(AppointmentRepository appointmentRepository, UserRepository userRepository, EmailService emailService) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public Result<List<Appointment>> getAll() {
        Result<List<Appointment>> result = new Result<>();

        List<Appointment> appointments = appointmentRepository.findAll();

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

        result.setpayload(appointments);
        return result;
    }

    public Result<Appointment> add(Appointment appointment) {
        Result<Appointment> result = validateAppointment(appointment);

        if (!result.isSuccess()) {
            return result;
        }

        if (appointment.getAppointmentDatetime().isBefore(LocalDateTime.now())) {
            result.addErrorMessage("Appointment cannot be in the past", ResultType.INVALID);
            return result;
        }

        // Prevent Double booking
        LocalDate date = appointment.getAppointmentDatetime().toLocalDate();

        List<Appointment> existing = appointmentRepository.findByBarberIdAndDate(appointment.getBarberId(), date);

        boolean conflict = existing.stream()
                        .anyMatch(a -> a.getAppointmentDatetime()
                                .equals(appointment.getAppointmentDatetime()));

        if (conflict) {
            result.addErrorMessage("Time slot already booked", ResultType.INVALID);
            return result;
        }

        appointment.setStatus("BOOKED");
        Appointment created = appointmentRepository.add(appointment);

        //Email

        if (created == null) {
            result.addErrorMessage("Failed to create appointment", ResultType.INVALID);
            return result;
        }


        // Get user email from DB
        Optional<User> user = userRepository.findById(created.getUserId());

        if (user.isPresent() && user.get().getEmail() != null) {
            String message =
                    "Your appointment is confirmed.\n\n" +
                    "Date/Time: " + created.getAppointmentDatetime() + "\n" +
                    "Status: " + created.getStatus() + "\n\n" +
                    "Thank you for choosing DeVito Styles.";

            emailService.sendAppointmentConfirmation(user.get().getEmail(), message);
        }

        result.setpayload(created);
        return result;
    }

    public Result<Void> cancel(int appointmentId) {
        Result<Void> result = new Result<>();

        Appointment existing = appointmentRepository.findById(appointmentId);

        if (existing == null) {
            result.addErrorMessage("Appointment not found", ResultType.NOT_FOUND);
            return result;
        }

        existing.setStatus("CANCELLED");

        boolean success = appointmentRepository.update(existing);

        if (!success) {
            result.addErrorMessage("Failed to cancel appointment", ResultType.INVALID);
            return result;
        }

        Optional<User> user = userRepository.findById(existing.getUserId());

        if (user.isPresent() && user.get().getEmail() != null) {
            String message = "Your appointment has been cancelled.\n\n" +
                    "Date/Time: " + existing.getAppointmentDatetime();

            emailService.sendCancellationEmail(user.get().getEmail(), message);
        }

        return result;
    }

    public Result<Appointment> update(Appointment appointment) {
        Result<Appointment> result = new Result<>();

        if (appointment == null || appointment.getAppointmentId() <= 0) {
            result.addErrorMessage("Invalid appointment", ResultType.INVALID);
            return result;
        }

        boolean success = appointmentRepository.update(appointment);

        if (!success) {
            result.addErrorMessage("Appointment not found", ResultType.NOT_FOUND);
            return result;
        }

        Optional<User> user = userRepository.findById(appointment.getUserId());

        if (user.isPresent() && user.get().getEmail() != null) {
            String message = "Your appointment has been updated.\n\n" +
                    "New Date/Time: " + appointment.getAppointmentDatetime() +
                    "\n\nStatus: " + appointment.getStatus();

            emailService.sendUpdatedAppointmentEmail(user.get().getEmail(), message);
        }

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
}
