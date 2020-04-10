package de.solid.backend.dao.repository;

import javax.enterprise.context.ApplicationScoped;
import de.solid.backend.dao.HelperEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class HelpersRepository implements PanacheRepository<HelperEntity> {

  public HelperEntity findByPhone(String phone) {
    return find("account.phone", phone).firstResult();
  }

  public HelperEntity findByEmail(String email) {
    return find("account.email", email).firstResult();
  }

  public HelperEntity findByAccount(long accountId) {
    return find("account.t_id", accountId).firstResult();
  }

  public void deleteByAccountId(long accountId) {
    delete("account.t_id", accountId);
  }
}
