package de.wirvsvirus.hack.rest.model;

import java.util.Date;

import org.springframework.beans.BeanUtils;

import de.wirvsvirus.hack.backend.dao.OfferEntity;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OfferModel {

	private int offerId;

	private UserModel user;

	private ProviderModel provider;

	private Date applyDate;

	private boolean contacted;

	public static OfferModel fromEntity(OfferEntity entity) {
		OfferModel model = OfferModel.builder().build();
		BeanUtils.copyProperties(entity, model);
		model.setOfferId(entity.getT_id());
		model.setApplyDate(entity.getT_dateCreated());
		model.setUser(UserModel.fromEntity(entity.getUser()));
		model.setProvider(ProviderModel.fromEntity(entity.getProvider()));
		return model;
	}
}