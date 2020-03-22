package de.wirvsvirus.hack.rest.model;

import java.util.Date;

import org.springframework.beans.BeanUtils;

import de.wirvsvirus.hack.backend.dao.FavoriteEntity;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FavoriteResponseModel {

	private int favoriteId;

	private HelperResponseModel helper;

	private ProviderResponseModel provider;

	private Date markedDate;

	public static FavoriteResponseModel fromEntity(FavoriteEntity entity) {
		FavoriteResponseModel model = FavoriteResponseModel.builder().build();
		BeanUtils.copyProperties(entity, model);
		model.setFavoriteId(entity.getT_id());
		model.setMarkedDate(entity.getT_dateCreated());
		model.setHelper(HelperResponseModel.fromEntity(entity.getHelper()));
		model.setProvider(
				ProviderResponseModel.fromEntity(entity.getProvider()));
		return model;
	}
}