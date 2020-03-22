package de.wirvsvirus.hack.rest.model;

import java.util.Date;

import org.springframework.beans.BeanUtils;

import de.wirvsvirus.hack.backend.dao.OfferEntity;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OfferResponseModel {

	private int offerId;

	private UserResponseModel user;

	private ProviderResponseModel provider;

	private Date applyDate;

	private boolean contacted;

	public static OfferResponseModel fromEntity(OfferEntity entity) {
		OfferResponseModel model = OfferResponseModel.builder().build();
		BeanUtils.copyProperties(entity, model);
		model.setOfferId(entity.getT_id());
		model.setApplyDate(entity.getT_dateCreated());
		model.setUser(UserResponseModel.fromEntity(entity.getUser()));
		model.setProvider(
				ProviderResponseModel.fromEntity(entity.getProvider()));
		return model;
	}
}