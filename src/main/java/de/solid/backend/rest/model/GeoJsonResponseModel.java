package de.solid.backend.rest.model;

import java.util.Arrays;
import java.util.List;

import de.solid.backend.dao.ProviderEntity;
import de.solid.backend.rest.model.base.BaseResponseModel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class GeoJsonResponseModel
		extends
			BaseResponseModel<GeoJsonResponseModel, ProviderEntity> {

	private String type = "Feature";

	private ProviderResponseModel properties;

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
	protected void mapAdditionalAttributes(GeoJsonResponseModel model,
			ProviderEntity entity) {
		model.setProperties(new ProviderResponseModel().fromEntity(entity));
		model.setGeometry(new Geometry(
				Arrays.asList(entity.getLatitude(), entity.getLongitude())));
	}
}
