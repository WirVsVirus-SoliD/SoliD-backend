package de.wirvsvirus.hack.rest.model;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;

import de.wirvsvirus.hack.backend.dao.ProviderEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProviderRequestModel {

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

	public static ProviderEntity toEntity(ProviderRequestModel model) {
		ProviderEntity entity = ProviderEntity.builder().build();
		BeanUtils.copyProperties(model, entity);
		entity.setAddressCity(model.getAddress().getCity());
		entity.setAddressHouseNo(model.getAddress().getHousenr());
		entity.setAddressStreet(model.getAddress().getStreet());
		entity.setAddressZip(model.getAddress().getZip());
		entity.setCrops(model.getCrops().stream().map(v -> v.toString())
				.collect(Collectors.joining("|||")));

		return entity;
	}
}
