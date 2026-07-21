package learn.DeVitoStyles.data.repositories;

import learn.DeVitoStyles.data.interfaces.BarberRepository;
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

    private static final String BASE_SELECT = """
            SELECT barber_id, first_name, last_name, availability_status,
                       specialization, image_url, title, bio, start_year,
                       instagram_url, display_order, is_active
                FROM barber
            """;

    public BarberJdbcClientRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    private Object[] toParams(Barber barber) {
        return new Object[] {
            barber.getFirstName(),
            barber.getLastName(),
            barber.getAvailabilityStatus(),
            barber.getSpecialization(),
            barber.getImageUrl(),
            barber.getTitle(),
            barber.getBio(),
            barber.getStartYear(),
            barber.getInstagramUrl(),
            barber.getDisplayOrder(),
            barber.isActive(),
        };
    }


    @Override
    public List<Barber> findAll() {
        final String sql = BASE_SELECT;

        return jdbcClient.sql(sql)
                .query(new BarberMapper())
                .list();
    }

    @Override
    public Barber findById(int barberId) {
        final String sql = BASE_SELECT + " WHERE barber_id = ?;";

        return jdbcClient.sql(sql)
                .param(barberId)
                .query(new BarberMapper())
                .optional()
                .orElse(null);
    }

    @Override
    public Barber add(Barber barber) {
        final String sql = """
                INSERT INTO barber (
                    first_name, last_name, availability_status,
                    specialization, image_url, title, bio,
                    start_year, instagram_url, display_order, is_active
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rowsAffected = jdbcClient.sql(sql)
                .params(toParams(barber))
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
                UPDATE barber
                SET first_name = ?,
                    last_name = ?,
                    availability_status = ?,
                    specialization = ?,
                    image_url = ?,
                    title = ?,
                    bio = ?,
                    start_year = ?,
                    instagram_url = ?,
                    display_order = ?,
                    is_active = ?
                WHERE barber_id = ?
                """;

        return jdbcClient.sql(sql)
                .params(toParams(barber))
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
