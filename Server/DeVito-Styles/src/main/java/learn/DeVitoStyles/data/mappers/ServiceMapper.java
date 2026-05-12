package learn.DeVitoStyles.data.mappers;

import learn.DeVitoStyles.models.Service;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ServiceMapper implements RowMapper<Service> {
    @Override
    public Service mapRow(ResultSet rs, int rowNum) throws SQLException {
        Service service = new Service();

        service.setServiceId(rs.getInt("service_id"));
        service.setName(rs.getString("name"));
        service.setDurationMinutes(rs.getInt("duration_minutes"));
        service.setPrice(rs.getBigDecimal("price"));
        service.setDescription(rs.getString("description"));

        return service;
    }
}
