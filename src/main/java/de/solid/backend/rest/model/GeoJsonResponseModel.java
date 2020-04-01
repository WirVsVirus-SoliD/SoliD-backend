package de.solid.backend.rest.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class GeoJsonResponseModel {

	private String type = "FeatureCollection";

	private CRS crs = new CRS();

	@NonNull
	private List<GeoJsonFeatureModel> features;

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
