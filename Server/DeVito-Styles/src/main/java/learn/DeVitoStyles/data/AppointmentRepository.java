package learn.DeVitoStyles.data;

import learn.DeVitoStyles.models.Appointment;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository {

    List<Appointment> findAll();

    Appointment findById(int appointmentId);

    List<Appointment> findByUserId(int userId);

    List<Appointment> findByBarberIdAndDate(int barberId, LocalDate date);

    Appointment add(Appointment appointment);

    boolean update(Appointment appointment);
}
