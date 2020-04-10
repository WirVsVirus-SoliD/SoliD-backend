package de.solid.backend.rest.model.helper;

import de.solid.backend.common.EmploymentStatus;
import de.solid.backend.dao.HelperEntity;
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
public class HelperResponseModel extends BaseResponseModel<HelperResponseModel, HelperEntity> {

  private long helperId;

  private long accountId;

  private String firstName;

  private String lastName;

  private String mobileNumber;

  private String email;

  private EmploymentStatus EmploymentStatus;

  private boolean fullTime;

  private int pickupRange;

  private boolean driverLicense;

  private boolean pickupRequired;

  @Override
  protected void mapAdditionalAttributes(HelperResponseModel model, HelperEntity entity) {
    model.setHelperId(entity.getT_id());
    model.setAccountId(entity.getAccount().getT_id());
  }
}
