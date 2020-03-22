package de.wirvsvirus.hack.backend.dao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import de.wirvsvirus.hack.backend.dao.InquireEntity;

public interface InquiresRepository
		extends
			JpaRepository<InquireEntity, Integer> {

	@Query(value = "select oe.* from solid_inquires oe where oe.helper_t_id = ?1 and oe.provider_t_id=?2", nativeQuery = true)
	public InquireEntity findByHelperAndProvider(int helperId, int providerId);

	@Query(value = "select oe.* from solid_inquires oe where oe.helper_t_id = ?1", nativeQuery = true)
	public List<InquireEntity> findByHelperId(int helperId);

	@Query(value = "select oe.* from solid_inquires oe where oe.provider_t_id = ?1", nativeQuery = true)
	public List<InquireEntity> findByProviderId(int providerId);
}
