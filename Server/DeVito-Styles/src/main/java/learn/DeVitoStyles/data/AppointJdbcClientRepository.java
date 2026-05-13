package learn.DeVitoStyles.data;

import learn.DeVitoStyles.data.mappers.AppointmentMapper;
import learn.DeVitoStyles.models.Appointment;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class AppointJdbcClientRepository implements AppointmentRepository {
    private final JdbcClient jdbcClient;

    private static final String BASE_SELECT = """
    SELECT appointment_id,
           user_id,
           barber_id,
           service_id,
           appointment_datetime,
           status
    FROM appointment
    """;

    public AppointJdbcClientRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    // Get every appointment from the DB
    // Used for the dashboard page
    // Admin should see all appointments
    @Override
    public List<Appointment> findAll() {

        return jdbcClient.sql(BASE_SELECT)
                .query(new AppointmentMapper())
                .list();
    }

    /*
    Purpose:
    Gets one specific appointment.

    Used For
        appointment details page
        editing an appointment
        canceling an appointment
     */
    @Override
    public Appointment findById(int appointmentId) {
        final String sql = BASE_SELECT + " WHERE appointment_id = ?;";

        return jdbcClient.sql(sql)
                .param(appointmentId)
                .query(new AppointmentMapper())
                .optional()
                .orElse(null);
    }

    //Purpose
    //  Gets all appointments belonging to one user/customer.
    //Used For
    //  “My Appointments” page.
    @Override
    public List<Appointment> findByUserId(int userId) {
        final String sql = BASE_SELECT + " WHERE user_id = ? ORDER BY appointment_datetime;";

        return jdbcClient.sql(sql)
                .param(userId)
                .query(new AppointmentMapper())
                .list();
    }

    /*
    Purpose
        Gets all appointments for a specific day.
    Used For
        Generating available time slots.
     */
    @Override
    public List<Appointment> findByBarberIdAndDate(int barberId, LocalDate date) {
        final String sql = BASE_SELECT + " WHERE barber_id = ? AND DATE(appointment_datetime) = ?;";

        return jdbcClient.sql(sql)
                .param(barberId)
                .param(date)
                .query(new AppointmentMapper())
                .list();
    }

    // Creates a new appointment booking.
    @Override
    public Appointment add(Appointment appointment) {
        final String sql = """
                INSERT INTO appointment
                (user_id, barber_id, service_id, appointment_datetime, status)
                VALUES (?, ?, ?, ?, ?);
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rowsAffected = jdbcClient.sql(sql)
                .param(appointment.getUserId())
                .param(appointment.getBarberId())
                .param(appointment.getServiceId())
                .param(appointment.getAppointmentDatetime())
                .param(appointment.getStatus())
                .update(keyHolder, "appointment_id");

        if (rowsAffected == 0) {
            return null;
        }

        appointment.setAppointmentId(keyHolder.getKey().intValue());

        return appointment;
    }

    // Edits an existing appointment.
    @Override
    public boolean update(Appointment appointment) {
        final String sql = """
                UPDATE appointment
                SET user_id = ?,
                    barber_id = ?,
                    service_id = ?,
                    appointment_datetime = ?,
                    status = ?
                WHERE appointment_id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(appointment.getUserId())
                .param(appointment.getBarberId())
                .param(appointment.getServiceId())
                .param(appointment.getAppointmentDatetime())
                .param(appointment.getStatus())
                .param(appointment.getAppointmentId())
                .update() > 0;
    }
}