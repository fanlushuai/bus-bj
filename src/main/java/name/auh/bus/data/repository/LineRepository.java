package name.auh.bus.data.repository;

import name.auh.bus.data.bean.Line;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LineRepository extends CrudRepository<Line, String> {

    @Query(nativeQuery = true, value = "select * from t_line where name like :lineName order by name asc limit 20")
    List<Line> findLinesByNameLike(@Param("lineName") String lineName);

}
