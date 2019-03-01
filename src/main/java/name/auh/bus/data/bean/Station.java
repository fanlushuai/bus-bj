package name.auh.bus.data.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Getter
@Setter
@ToString
@IdClass(StationId.class)
@Table(name = "t_station")
public class Station implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    String id;

    @Id
    String dir;

    @Id
    String line;

    String name;
}
