package name.auh.bus.data.repository;

import name.auh.bus.data.bean.Direction;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DirectionRepository extends CrudRepository<Direction, String> {

    List<Direction> findByLineEquals(String line);

}
