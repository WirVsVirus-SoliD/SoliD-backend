package de.solid.backend.dao.repository;

import javax.enterprise.context.ApplicationScoped;
import de.solid.backend.dao.AccountEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class AccountRepository implements PanacheRepository<AccountEntity> {

  public AccountEntity findByEmail(String email) {
    return find("email", email.toLowerCase()).firstResult();
  }
}
