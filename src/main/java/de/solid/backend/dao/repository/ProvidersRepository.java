package de.solid.backend.dao.repository;

import javax.enterprise.context.ApplicationScoped;
import de.solid.backend.dao.ProviderEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class ProvidersRepository implements PanacheRepository<ProviderEntity> {

  public ProviderEntity findByEmail(String email) {
    return find("account.email", email).firstResult();
  }

  public ProviderEntity findByAccount(long accountId) {
    return find("account.t_id", accountId).firstResult();
  }

  public void deleteByAccountId(long accountId) {
    delete("account.t_id", accountId);
  }
}
