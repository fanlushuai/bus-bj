package name.auh.bus.data.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Transient;
import java.io.Serializable;

@Data
@Entity
@IdClass(BusRealTimeId.class)
@AllArgsConstructor
@NoArgsConstructor
public class BusRealTime implements Serializable {

    private static final long serialVersionUID = 1L;

    String busRunTime;

    String busRunInfo;

    @Id
    String line;

    @Id
    String dir;

    @Id
    String stop;

    //不是数据库字段，进行忽略
    @Transient
    String name;

    long updateTime;


}
