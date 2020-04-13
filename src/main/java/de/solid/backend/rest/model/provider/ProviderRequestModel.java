package de.solid.backend.rest.model.provider;

import java.util.List;
import java.util.stream.Collectors;
import de.solid.backend.common.Crops;
import de.solid.backend.dao.ProviderEntity;
import de.solid.backend.rest.model.AccountRequestModel;
import de.solid.backend.rest.model.AddressRequestModel;
import de.solid.backend.rest.model.BaseRequestModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * request model for provider data
 *
 */
@Getter
@Setter
@ToString
public class ProviderRequestModel extends BaseRequestModel<ProviderRequestModel, ProviderEntity> {

  private String farmName;

  private List<Crops> crops;

  private AddressRequestModel address;

  private String url;

  private String minWorkPeriod;

  private float hourlyRate;

  private boolean pickupPossible;

  private int pickupRange;

  private boolean overnightPossible;

  private AccountRequestModel account;

  private String description;

  private float overnightPrice;

  @Override
  protected void mapAdditionalAttributes(ProviderEntity entity) {
    if (this.getCrops() != null) {
      entity.setCrops(
          this.getCrops().stream().map(v -> v.toString()).collect(Collectors.joining("|||")));
    }
    entity.setAddress(this.getAddress().toEntity(entity.getAddress()));
  }
}
