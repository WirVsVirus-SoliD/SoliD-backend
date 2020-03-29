package de.solid.backend.rest.model;

import java.util.List;
import java.util.stream.Collectors;

import de.solid.backend.common.Crops;
import de.solid.backend.dao.ProviderEntity;
import de.solid.backend.rest.model.base.BaseRequestModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderRequestModel
		extends
			BaseRequestModel<ProviderRequestModel, ProviderEntity> {

	private String farmName;

	private List<Crops> crops;

	private String contactFirstName;

	private String contactLastName;

	private String email;

	private AddressRequestModel address;

	private String url;

	private String phone;

	private int minWorkPeriod;

	private float hourlyRate;

	private boolean pickupPossible;

	private int pickupRange;

	private boolean overnightPossible;

	@Override
	protected void mapAdditionalAttributes(ProviderEntity entity) {
		if (this.getCrops() != null) {
			entity.setCrops(this.getCrops().stream().map(v -> v.toString())
					.collect(Collectors.joining("|||")));
		}
	}
}
