package de.wirvsvirus.hack.backend.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import de.wirvsvirus.hack.backend.dao.HelperEntity;

public interface HelpersRepository extends JpaRepository<HelperEntity, Integer> {

	@Query
	public HelperEntity findByMobileNumber(String mobileNumber);

}
