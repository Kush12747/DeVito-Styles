package learn.DeVitoStyles.data.mappers;

import learn.DeVitoStyles.models.Appointment;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AppointmentMapper implements RowMapper<Appointment> {

    @Override
    public Appointment mapRow(ResultSet rs, int rowNum) throws SQLException {
        Appointment appointment = new Appointment();

        appointment.setAppointmentId(rs.getInt("appointment_id"));
        appointment.setUserId(rs.getInt("user_id"));
        appointment.setBarberId(rs.getInt("barber_id"));
        appointment.setServiceId(rs.getInt("service_id"));
        appointment.setAppointmentDatetime(rs.getTimestamp("appointment_datetime").toLocalDateTime());
        appointment.setStatus(rs.getString("status"));
        appointment.setGoogleEventId(rs.getString("google_event_id"));

        return appointment;
    }
}
