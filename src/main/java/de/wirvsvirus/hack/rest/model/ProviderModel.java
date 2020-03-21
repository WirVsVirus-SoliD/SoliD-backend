package de.wirvsvirus.hack.rest.model;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;

import de.wirvsvirus.hack.backend.dao.ProviderEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProviderModel extends ProviderRegisterModel {

	private Integer providerId;

	private float distance;

	private double latitude;

	private double longitude;

	public static ProviderModel fromRegisterModel(
			ProviderRegisterModel requestModel) {
		ProviderModel resultModel = new ProviderModel();
		BeanUtils.copyProperties(requestModel, resultModel);
		return resultModel;
	}

	public static ProviderModel fromEntity(ProviderEntity entity) {
		ProviderModel model = new ProviderModel();
		BeanUtils.copyProperties(entity, model);
		model.setAddress(new Address(entity.getAddressStreet(),
				entity.getAddressHouseNo(), entity.getAddressZip(),
				entity.getAddressCity()));
		model.setCrops(Arrays.asList(entity.getCrops().split("\\|\\|\\|"))
				.stream().map(v -> Crops.valueOf(v))
				.collect(Collectors.toList()));

		return model;
	}

	public static ProviderEntity toEntity(ProviderModel model) {
		ProviderEntity entity = ProviderEntity.builder().build();
		if (model.getProviderId() != null) {
			entity.setT_id(model.getProviderId());
		}
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
