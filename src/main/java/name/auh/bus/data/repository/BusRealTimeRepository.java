package name.auh.bus.data.repository;

import name.auh.bus.data.bean.BusRealTime;
import name.auh.bus.data.bean.BusRealTimeId;
import org.springframework.data.repository.CrudRepository;

public interface BusRealTimeRepository extends CrudRepository<BusRealTime, BusRealTimeId> {

}
