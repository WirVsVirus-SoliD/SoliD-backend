package de.solid.backend.rest.model.provider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.json.bind.annotation.JsonbNillable;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * response model according to geoJson
 *
 */
@Getter
@Setter
@RequiredArgsConstructor
@ToString
@JsonbNillable(value = true)
public class GeoJsonResponseModel {

  private String type = "FeatureCollection";

  private CRS crs = new CRS();

  @NonNull
  private List<GeoJsonFeatureResponseModel> features;

  @Getter
  @Setter
  public class CRS {

    private String type = "name";

    private Map<String, String> properties;

    public CRS() {
      properties = new HashMap<>();
      properties.put("name", "EPSG:4326");
    }
  }
}
