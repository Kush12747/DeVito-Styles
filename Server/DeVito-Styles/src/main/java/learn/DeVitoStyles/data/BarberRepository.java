package learn.DeVitoStyles.data;

import learn.DeVitoStyles.models.Barber;

import java.util.List;

public interface BarberRepository {

    List<Barber> findAll();

    Barber findById(int barberId);

    Barber add(Barber barber);

    boolean update(Barber barber);

    boolean deleteById(int barberId);
}
