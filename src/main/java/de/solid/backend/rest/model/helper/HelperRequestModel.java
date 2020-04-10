package de.solid.backend.rest.model.helper;

import de.solid.backend.common.EmploymentStatus;
import de.solid.backend.dao.HelperEntity;
import de.solid.backend.rest.model.AccountRequestModel;
import de.solid.backend.rest.model.BaseRequestModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * request model for helper data
 *
 */
@Getter
@Setter
@ToString
public class HelperRequestModel extends BaseRequestModel<HelperRequestModel, HelperEntity> {

  private EmploymentStatus EmploymentStatus;

  private boolean fullTime;

  private int pickupRange;

  private boolean driverLicense;

  private boolean pickupRequired;

  private AccountRequestModel account;

  @Override
  protected void mapAdditionalAttributes(HelperEntity entity) {}
}
