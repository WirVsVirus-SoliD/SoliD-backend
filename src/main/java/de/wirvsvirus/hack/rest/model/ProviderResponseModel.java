package de.wirvsvirus.hack.rest.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;

import de.wirvsvirus.hack.backend.dao.ProviderEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProviderResponseModel {

	private Integer providerId;

	private String farmName;

	private List<Crops> crops;

	private String contactFirstName;

	private String contactLastName;

	private String email;

	private Address address;

	private String url;

	private String phone;

	private int minWorkPeriod;

	private float hourlyRate;

	private boolean pickupPossible;

	private int pickupRange;

	private boolean overnightPossible;

	private double distance;

	private double latitude;

	private double longitude;

	public static ProviderResponseModel fromEntity(ProviderEntity entity) {
		ProviderResponseModel model = new ProviderResponseModel();
		BeanUtils.copyProperties(entity, model);
		model.setAddress(new Address(entity.getAddressStreet(),
				entity.getAddressHouseNo(), entity.getAddressZip(),
				entity.getAddressCity()));
		model.setCrops(Arrays.asList(entity.getCrops().split("\\|\\|\\|"))
				.stream().map(v -> Crops.valueOf(v))
				.collect(Collectors.toList()));
		model.setProviderId(entity.getT_id());

		return model;
	}
}
