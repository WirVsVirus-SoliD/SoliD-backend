package de.wirvsvirus.hack.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import de.wirvsvirus.hack.backend.BackendConfiguration;
import de.wirvsvirus.hack.rest.RestConfiguration;

@SpringBootApplication
@EnableAutoConfiguration
@Import({RestConfiguration.class, BackendConfiguration.class})
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
