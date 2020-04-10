package de.solid.backend.rest.model.provider;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import de.solid.backend.common.Crops;
import de.solid.backend.dao.ProviderEntity;
import de.solid.backend.rest.model.AddressResponseModel;
import de.solid.backend.rest.model.BaseResponseModel;
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
public class ProviderResponseModel
    extends BaseResponseModel<ProviderResponseModel, ProviderEntity> {

  private long providerId;

  private String farmName;

  private List<Crops> crops;

  private AddressResponseModel address;

  private String url;

  private int minWorkPeriod;

  private float hourlyRate;

  private boolean pickupPossible;

  private int pickupRange;

  private boolean overnightPossible;

  private double latitude;

  private double longitude;

  private double distance;

  @Override
  protected void mapAdditionalAttributes(ProviderResponseModel model, ProviderEntity entity) {
    if (entity.getAddress() != null) {
      model.setAddress(new AddressResponseModel().fromEntity(entity.getAddress()));
    }
    if (entity.getCrops() != null) {
      model.setCrops(Arrays.asList(entity.getCrops().split("\\|\\|\\|")).stream()
          .map(v -> Crops.valueOf(v)).collect(Collectors.toList()));
    }
    model.setProviderId(entity.getT_id());
  }
}
