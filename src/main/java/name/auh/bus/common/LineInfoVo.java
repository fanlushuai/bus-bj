package name.auh.bus.common;

import lombok.Builder;
import lombok.Data;
import name.auh.bus.data.bean.Direction;
import name.auh.bus.data.bean.Line;
import name.auh.bus.data.bean.Station;

import java.util.List;

@Data
@Builder
public class LineInfoVo {

    Line line;

    List<Station> station;

    List<Direction> direction;
}
