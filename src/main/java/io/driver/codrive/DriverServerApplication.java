package io.driver.codrive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DriverServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DriverServerApplication.class, args);
	}

}
