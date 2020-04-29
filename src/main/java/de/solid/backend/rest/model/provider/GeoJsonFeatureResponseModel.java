package de.solid.backend.rest.model.provider;

import java.util.Arrays;
import java.util.List;
import javax.json.bind.annotation.JsonbNillable;
import de.solid.backend.dao.ProviderEntity;
import de.solid.backend.rest.model.BaseResponseModel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * response model according to geoJsonFeature
 *
 */
@Getter
@Setter
@ToString
@JsonbNillable(value = true)
public class GeoJsonFeatureResponseModel
    extends BaseResponseModel<GeoJsonFeatureResponseModel, ProviderEntity> {

  private String type = "Feature";

  private PublicProviderResponseModel properties;

  private Geometry geometry;

  @Getter
  @Setter
  @RequiredArgsConstructor
  public class Geometry {

    private String type = "Point";

    @NonNull
    private List<Double> coordinates;
  }

  @Override
  protected void mapAdditionalAttributes(GeoJsonFeatureResponseModel model, ProviderEntity entity) {
    model.setProperties(new PublicProviderResponseModel().fromEntity(entity));
    model.setGeometry(new Geometry(Arrays.asList(entity.getLatitude(), entity.getLongitude())));
  }
}
