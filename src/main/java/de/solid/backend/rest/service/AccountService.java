package de.solid.backend.rest.service;

import java.util.Date;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import de.solid.backend.dao.AccountEntity;
import de.solid.backend.dao.repository.AccountRepository;
import de.solid.backend.rest.model.AccountRequestModel;
import de.solid.backend.rest.service.exception.DuplicateException;
import de.solid.backend.rest.service.exception.NoSuchEntityException;
import de.solid.backend.rest.service.exception.RequiredArgumentException;

/**
 * provides account related operations and the connection to keycloak user
 * 
 *
 */
@ApplicationScoped
public class AccountService {

  @Inject
  private KeycloakService keycloakService;

  @Inject
  private AccountRepository accountRepository;

  @Transactional
  public void resetPassword(long accountId, String newPassword) {
    AccountEntity entity = this.accountRepository.findById(accountId);
    entity.setLastPasswordReset(new Date());
    this.accountRepository.persist(entity);
    this.keycloakService.updateUser(entity.getKeycloakUserId(), null, null, null, newPassword);
  }

  public AccountEntity createAccount(AccountRequestModel model) {
    this.checkEmailValid(model.getEmail());
    model.setEmail(model.getEmail().toLowerCase());
    this.checkForExistingEmail(model.getEmail());
    String keycloakId = this.keycloakService.createUser(model.getFirstName(), model.getLastName(),
        model.getEmail().toLowerCase(), model.getPassword());
    AccountEntity entity = model.toEntity(null);
    entity.setKeycloakUserId(keycloakId);
    this.accountRepository.persist(entity);
    this.keycloakService.setAccountId(entity.getT_id(), keycloakId);
    return entity;
  }

  public AccountEntity updateAccount(AccountRequestModel model, String authenticatedUserEmail) {
    AccountEntity entity = this.findByEmail(authenticatedUserEmail);
    if (model != null) {
      if (this.stringIsDifferent(model.getEmail(), authenticatedUserEmail)) {
        this.checkEmailValid(model.getEmail());
        model.setEmail(model.getEmail().toLowerCase());
      } else {
        model.setEmail(authenticatedUserEmail);
      }
      entity = model.toEntity(entity);
      this.accountRepository.persist(entity);
      this.keycloakService.updateUser(entity.getKeycloakUserId(), model.getFirstName(),
          model.getLastName(), model.getEmail(), model.getPassword());
    }
    return entity;
  }

  public void activateAccount(long accountId) {
    AccountEntity entity = this.accountRepository.findById(accountId);
    if (entity != null) {
      this.keycloakService.activateUser(entity.getKeycloakUserId());
    } else {
      throw new NoSuchEntityException(this.getClass(), "activateAccount",
          String.format("called with non existing accountId %s", accountId));
    }
  }

  public AccountEntity deleteAccount(String authenticatedUserEmail) {
    AccountEntity entity = findByEmail(authenticatedUserEmail);
    this.deleteAccount(entity);
    return entity;
  }

  public void deleteAccount(long accountId) {
    AccountEntity entity = this.accountRepository.findById(accountId);
    this.deleteAccount(entity);
  }

  private void deleteAccount(AccountEntity entity) {
    this.keycloakService.deleteUser(entity.getKeycloakUserId());
    this.accountRepository.delete(entity);
  }

  private void checkEmailValid(String email) {
    if (StringUtils.isEmpty(email)) {
      throw new RequiredArgumentException(this.getClass(), "checkEmailValid",
          "mandatory field email was not provided");
    }
  }

  private void checkForExistingEmail(String email) {
    if (StringUtils.isEmpty(email) || this.accountRepository.findByEmail(email) != null) {
      throw new DuplicateException(this.getClass(), "checkForExistingEmail",
          String.format("called for already existing email %s", email));
    }
  }

  public AccountEntity findByEmail(String email) {
    AccountEntity entity = this.accountRepository.findByEmail(email);
    if (entity != null) {
      return entity;
    }
    throw new NoSuchEntityException(this.getClass(), "findByEmail",
        String.format("called with non existing email %s", email));
  }

  private boolean stringIsDifferent(String valueA, String valueB) {
    if (valueA != null && valueB != null) {
      if (valueA.toLowerCase().trim().equals(valueB.toLowerCase().trim())) {
        return false;
      }
      return true;
    }
    return false;
  }
}
