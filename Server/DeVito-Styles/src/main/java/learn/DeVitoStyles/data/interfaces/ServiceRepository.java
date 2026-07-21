package learn.DeVitoStyles.data.interfaces;

import learn.DeVitoStyles.models.Service;

import java.util.List;

public interface ServiceRepository {

    List<Service> findAll();

    Service findById(int serviceId);

    Service create(Service service);

    boolean update(Service service);

    boolean deleteById(int serviceId);
}
