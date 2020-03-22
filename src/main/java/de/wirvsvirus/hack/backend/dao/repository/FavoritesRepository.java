package de.wirvsvirus.hack.backend.dao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import de.wirvsvirus.hack.backend.dao.FavoriteEntity;

public interface FavoritesRepository
		extends
			JpaRepository<FavoriteEntity, Integer> {
	@Query(value = "select oe.* from solid_favorites oe where oe.helper_t_id = ?1 and oe.provider_t_id=?2", nativeQuery = true)
	public FavoriteEntity findByHelperAndProvider(int userId, int providerId);

	@Query(value = "select oe.* from solid_favorites oe where oe.helper_t_id = ?1", nativeQuery = true)
	public List<FavoriteEntity> findByHelperId(int helperId);

}
