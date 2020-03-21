package de.wirvsvirus.hack.backend.dao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import de.wirvsvirus.hack.backend.dao.FavoriteEntity;

public interface FavoriteRepository
		extends
			JpaRepository<FavoriteEntity, Integer> {
	@Query(value = "select oe.* from solid_favorites oe where oe.user_t_id = ?1 and oe.provider_t_id=?2", nativeQuery = true)
	public FavoriteEntity findByUserAndProvider(int userId, int providerId);

	@Query(value = "select oe.* from solid_favorites oe where oe.user_t_id = ?1", nativeQuery = true)
	public List<FavoriteEntity> findByUserId(int userId);

}
