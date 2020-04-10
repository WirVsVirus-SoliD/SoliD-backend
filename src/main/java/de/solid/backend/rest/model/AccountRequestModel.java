package de.solid.backend.rest.model;

import de.solid.backend.dao.AccountEntity;
import io.reactivex.annotations.NonNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * request model for general account data
 *
 */
@Getter
@Setter
@ToString
public class AccountRequestModel extends BaseRequestModel<AccountRequestModel, AccountEntity> {

  private String firstName;

  private String lastName;

  @NonNull
  private String email;

  private String phone;

  private String password;

  @Override
  protected void mapAdditionalAttributes(AccountEntity entity) {}
}
