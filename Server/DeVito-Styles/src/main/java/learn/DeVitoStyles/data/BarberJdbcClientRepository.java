package learn.DeVitoStyles.data;

import learn.DeVitoStyles.data.mappers.BarberMapper;
import learn.DeVitoStyles.models.Barber;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BarberJdbcClientRepository implements BarberRepository {
    private final JdbcClient jdbcClient;

    public BarberJdbcClientRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }


    @Override
    public List<Barber> findAll() {
        final String sql = """
                SELECT barber_id, first_name, last_name, availability_status, specialization
                FROM barber;
                """;

        return jdbcClient.sql(sql)
                .query(new BarberMapper())
                .list();
    }

    @Override
    public Barber findById(int barberId) {
        final String sql = """
                SELECT * FROM barber WHERE barber_id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(barberId)
                .query(new BarberMapper())
                .optional()
                .orElse(null);
    }

    @Override
    public Barber add(Barber barber) {
        final String sql = """
                INSERT INTO barber (first_name, last_name, availability_status, specialization)
                VALUES (?, ?, ?, ?);
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rowsAffected = jdbcClient.sql(sql)
                .param(barber.getFirstName())
                .param(barber.getLastName())
                .param(barber.getAvailabilityStatus())
                .param(barber.getSpecialization())
                .update(keyHolder, "barber_id");

        if (rowsAffected == 0) {
            return null;
        }

        barber.setBarberId(keyHolder.getKey().intValue());

        return barber;
    }

    @Override
    public boolean update(Barber barber) {
        final String sql = """
                UPDATE barber SET
                    first_name = ?,
                    last_name = ?,
                    availability_status = ?,
                    specialization = ?
                WHERE barber_id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(barber.getFirstName())
                .param(barber.getLastName())
                .param(barber.getAvailabilityStatus())
                .param(barber.getSpecialization())
                .param(barber.getBarberId())
                .update() > 0;
    }

    @Override
    public boolean deleteById(int barberId) {
        final String sql = """
                DELETE FROM barber WHERE barber_id = ?;
                """;

        return jdbcClient.sql(sql)
                .param(barberId)
                .update() > 0;
    }
}
