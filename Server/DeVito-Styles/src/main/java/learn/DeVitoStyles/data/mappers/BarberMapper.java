package learn.DeVitoStyles.data.mappers;

import learn.DeVitoStyles.models.Barber;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BarberMapper implements RowMapper<Barber> {

    @Override
    public Barber mapRow(ResultSet rs, int rowNum) throws SQLException {
        Barber barber = new Barber();

        barber.setBarberId(rs.getInt("barber_id"));
        barber.setFirstName(rs.getString("first_name"));
        barber.setLastName(rs.getString("last_name"));
        barber.setAvailabilityStatus(rs.getString("availability_status"));
        barber.setSpecialization(rs.getString("specialization"));
        barber.setImageUrl(rs.getString("image_url"));
        barber.setTitle(rs.getString("title"));
        barber.setBio(rs.getString("bio"));
        barber.setStartYear(rs.getInt("start_year"));
        barber.setInstagramUrl(rs.getString("instagram_url"));
        barber.setDisplayOrder(rs.getInt("display_order"));
        barber.setActive(rs.getBoolean("is_active"));

        return barber;
    }
}