package name.auh.bus.data.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Getter
@Setter
@ToString
@Entity
@Table(name = "t_direction")
public class Direction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    String id;

    String desc;

    String line;

}
