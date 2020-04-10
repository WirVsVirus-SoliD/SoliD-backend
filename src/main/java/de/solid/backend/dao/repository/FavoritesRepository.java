package de.solid.backend.dao.repository;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import de.solid.backend.dao.FavoriteEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class FavoritesRepository implements PanacheRepository<FavoriteEntity> {

  public FavoriteEntity findByHelperAndProvider(long helperId, long providerId) {
    return find("helper_t_id = ?1 and provider_t_id = ?2", helperId, providerId).firstResult();
  }

  public List<FavoriteEntity> findByHelperId(long helperId) {
    return find("helper_t_id = ?1", helperId).list();
  }

  public void deleteById(long id) {
    delete(findById(id));
  }
}
