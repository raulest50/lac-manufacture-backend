package exotic.app.planta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ExoticManufactureApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExoticManufactureApplication.class, args);
	}

}
