package name.auh.bus.data.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StationId implements Serializable {

    String id;

    String dir;

    String line;

}
