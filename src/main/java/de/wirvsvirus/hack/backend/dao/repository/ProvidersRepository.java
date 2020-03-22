package de.wirvsvirus.hack.backend.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.wirvsvirus.hack.backend.dao.ProviderEntity;

public interface ProvidersRepository
		extends
			JpaRepository<ProviderEntity, Integer> {

}
