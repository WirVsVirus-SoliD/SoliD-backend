package de.solid.backend.rest.model.helper;

import javax.json.bind.annotation.JsonbNillable;
import de.solid.backend.common.EmploymentStatus;
import de.solid.backend.dao.HelperEntity;
import de.solid.backend.rest.model.AccountResponseModel;
import de.solid.backend.rest.model.BaseResponseModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * response model for public helper data
 *
 */
@Getter
@Setter
@ToString
@JsonbNillable(value = true)
public class HelperResponseModel extends BaseResponseModel<HelperResponseModel, HelperEntity> {

  private long helperId;

  private String firstName;

  private String lastName;

  private String mobileNumber;

  private String email;

  private EmploymentStatus EmploymentStatus;

  private Boolean fullTime;

  private Boolean partTime;

  private Integer pickupRange;

  private Boolean driverLicense;

  private Boolean pickupRequired;

  private Boolean driverActivity;

  private AccountResponseModel account;

  @Override
  protected void mapAdditionalAttributes(HelperResponseModel model, HelperEntity entity) {
    model.setHelperId(entity.getT_id());
    model.setAccount(new AccountResponseModel().fromEntity(entity.getAccount()));
  }
}
