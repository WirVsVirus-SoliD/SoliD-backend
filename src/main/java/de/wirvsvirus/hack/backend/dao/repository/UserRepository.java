package de.wirvsvirus.hack.backend.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import de.wirvsvirus.hack.backend.dao.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

	@Query
	public UserEntity findByMobileNumber(String mobileNumber);

}
