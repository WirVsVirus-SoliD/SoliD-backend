package de.wirvsvirus.hack.backend;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Component;

import de.wirvsvirus.hack.backend.dao.AbstractEntity;
import de.wirvsvirus.hack.backend.dao.repository.ProvidersRepository;

@Configuration
@EnableJpaRepositories(basePackageClasses = {ProvidersRepository.class})
@EntityScan(basePackageClasses = {AbstractEntity.class})
@Component
@ComponentScan
public class BackendConfiguration {
	@Bean
	@Primary
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource dataSource() {
		return DataSourceBuilder.create().build();
	}
}
