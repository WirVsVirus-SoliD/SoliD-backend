package de.solid.backend.rest.model.helper;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
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

  private String employmentStatus;

  private Boolean fullTime;

  private Boolean partTime;

  private Boolean driverLicense;

  private Boolean pickupRequired;

  private AccountRequestModel account;

  private Boolean driverActivity;

  @Schema(
      description = "providerId the helper to register visited - will be added as parameter within registration email confirm link and is used to go back to this provider after successful registration",
      example = "1")
  private Long visitedProvider;

  @Override
  protected void mapAdditionalAttributes(HelperEntity entity) {
    entity.setEmploymentStatus(EmploymentStatus.fromString(employmentStatus));
  }
}
