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
@Table(name = "t_srawler")
public class Srawler implements Serializable {

    private static final long serialVersionUID = 1L;

    String status;

    @Id
    String name;

}
