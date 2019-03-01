package name.auh.bus.data.repository;

import name.auh.bus.data.bean.Station;
import name.auh.bus.data.bean.StationId;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StationRepository extends CrudRepository<Station, StationId> {

    List<Station> findStationsByNameLike(String name);

    List<Station> findStationsByNameLikeAndLineEquals(String name, String line);

    List<Station> findStationsByLine(String line);

    List<Station> findStationsByLineAndDir(String line, String direction);

}
