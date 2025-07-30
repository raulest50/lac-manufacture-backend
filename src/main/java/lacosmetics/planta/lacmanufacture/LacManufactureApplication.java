package lacosmetics.planta.lacmanufacture;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LacManufactureApplication {

	public static void main(String[] args) {
		SpringApplication.run(LacManufactureApplication.class, args);
	}

}
