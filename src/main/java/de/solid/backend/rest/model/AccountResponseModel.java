package de.solid.backend.rest.model;

import javax.json.bind.annotation.JsonbNillable;
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
@JsonbNillable(value = true)
public class AccountResponseModel extends BaseResponseModel<AccountResponseModel, AccountEntity> {

  private long accountId;

  private String firstName;

  private String lastName;

  @NonNull
  private String email;

  private String phone;

  @Override
  protected void mapAdditionalAttributes(AccountResponseModel model, AccountEntity entity) {
    model.setAccountId(entity.getT_id());
  }


}
