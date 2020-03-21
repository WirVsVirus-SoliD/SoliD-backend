package de.wirvsvirus.hack.backend.dao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import de.wirvsvirus.hack.backend.dao.OfferEntity;

public interface OfferRepository extends JpaRepository<OfferEntity, Integer> {

	@Query(value = "select oe.* from solid_offer oe where oe.user_t_id = ?1 and oe.provider_t_id=?2", nativeQuery = true)
	public OfferEntity findByUserAndProvider(int userId, int providerId);

	@Query(value = "select oe.* from solid_offer oe where oe.user_t_id = ?1", nativeQuery = true)
	public List<OfferEntity> findByUserId(int userId);

	@Query(value = "select oe.* from solid_offer oe where oe.provider_t_id = ?1", nativeQuery = true)
	public List<OfferEntity> findByProviderId(int providerId);
}
