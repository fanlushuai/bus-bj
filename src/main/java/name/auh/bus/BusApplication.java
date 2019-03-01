package name.auh.bus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
public class BusApplication {

    public static void main(String[] args) {
        SpringApplication.run(BusApplication.class, args);
    }

}

