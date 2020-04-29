package de.solid.backend.rest.model.provider;

import javax.json.bind.annotation.JsonbNillable;
import de.solid.backend.dao.ProviderEntity;
import de.solid.backend.rest.model.AccountResponseModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * response model for public provider data
 *
 */
@Getter
@Setter
@ToString
@JsonbNillable(value = true)
public class ProviderResponseModel extends PublicProviderResponseModel {

  private AccountResponseModel account;

  public ProviderResponseModel fromEntity(ProviderEntity entity) {
    PublicProviderResponseModel publicModel = new PublicProviderResponseModel().fromEntity(entity);
    ProviderResponseModel model = new ProviderResponseModel();
    this.copyProperties(publicModel, model);
    model.setAccount(new AccountResponseModel().fromEntity(entity.getAccount()));
    return model;
  }
}
