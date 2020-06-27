package de.solid.backend.rest.model.provider;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.json.bind.annotation.JsonbNillable;
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
@JsonbNillable(value = true)
public class PublicProviderResponseModel
    extends BaseResponseModel<PublicProviderResponseModel, ProviderEntity> {

  private long providerId;

  private long accountId;

  private String farmName;

  private List<Crops> crops;

  private AddressResponseModel address;

  private String url;

  private String minWorkPeriod;

  private Float hourlyRate;

  private Integer pickupRange;

  private Double latitude;

  private Double longitude;

  private Double distance;

  private String workingConditions;

  private String overnightInformation;

  private String providingInformation;

  private String languages;

  private String otherInformation;

  private Float overnightPrice;

  private Boolean bio;

  private List<String> workActivities;

  @Override
  protected void mapAdditionalAttributes(PublicProviderResponseModel model, ProviderEntity entity) {
    if (entity.getAddress() != null) {
      model.setAddress(new AddressResponseModel().fromEntity(entity.getAddress()));
    }
    if (entity.getCrops() != null) {
      model.setCrops(Arrays.asList(entity.getCrops().split("\\|\\|\\|")).stream()
          .map(v -> Crops.valueOf(v)).collect(Collectors.toList()));
    }
    if (entity.getWorkActivities() != null) {
      model.setWorkActivities(Arrays.asList(entity.getWorkActivities().split("\\|\\|\\|")));
    }
    model.setProviderId(entity.getT_id());
    model.setAccountId(entity.getAccount().getT_id());
  }
}
