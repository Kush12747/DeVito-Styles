package learn.DeVitoStyles.data.repositories;

import learn.DeVitoStyles.data.interfaces.ServiceRepository;
import learn.DeVitoStyles.data.mappers.ServiceMapper;
import learn.DeVitoStyles.models.Service;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ServiceJdbcClientRepository implements ServiceRepository {
    private final JdbcClient jdbcClient;

    public ServiceJdbcClientRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    // display all service
    @Override
    public List<Service> findAll() {
        final String sql = """
                SELECT service_id, name, duration_minutes, price, description
                FROM service;
                """;

        return jdbcClient.sql(sql)
                .query(new ServiceMapper())
                .list();
    }

    // Single service details
    @Override
    public Service findById(int serviceId) {
        final String sql = """
                SELECT * FROM service WHERE service_id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(serviceId)
                .query(new ServiceMapper())
                .optional()
                .orElse(null);
    }

    // Admin adds service
    @Override
    public Service create(Service service) {
        final String sql = """
                INSERT INTO service (name, duration_minutes, price, description)
                VALUES (?, ?, ?, ?);
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rowsAffected = jdbcClient.sql(sql)
                .param(service.getName())
                .param(service.getDurationMinutes())
                .param(service.getPrice())
                .param(service.getDescription())
                .update(keyHolder, "service_id");

        if (rowsAffected == 0) {
            return null;
        }

        service.setServiceId(keyHolder.getKey().intValue());

        return service;
    }

    // Admin updates service
    @Override
    public boolean update(Service service) {
        final String sql = """
                UPDATE service SET
                    name = ?,
                    duration_minutes = ?,
                    price = ?,
                    description = ?
                WHERE service_id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(service.getName())
                .param(service.getDurationMinutes())
                .param(service.getPrice())
                .param(service.getDescription())
                .param(service.getServiceId())
                .update() > 0;
    }

    // Admin remove service
    @Override
    public boolean deleteById(int serviceId) {
        final String sql = """
                DELETE FROM service WHERE service_id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(serviceId)
                .update() > 0;
    }
}
