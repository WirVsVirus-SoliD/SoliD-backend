package de.solid.backend.dao.repository;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import de.solid.backend.dao.InquiryEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class InquiriesRepository implements PanacheRepository<InquiryEntity> {

  public InquiryEntity findByHelperAndProvider(long helperId, long providerId) {
    return find("helper_t_id = ?1 and provider_t_id = ?2", helperId, providerId).firstResult();
  }

  public List<InquiryEntity> findByHelperId(long helperId) {
    return find("helper_t_id = ?1", helperId).list();
  }

  public List<InquiryEntity> findByProviderId(long providerId) {
    return find("provider_t_id = ?1", providerId).list();
  }
}
