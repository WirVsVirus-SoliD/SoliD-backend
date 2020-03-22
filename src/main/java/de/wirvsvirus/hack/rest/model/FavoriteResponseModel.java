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

	private UserResponseModel user;

	private ProviderResponseModel provider;

	private Date markedDate;

	public static FavoriteResponseModel fromEntity(FavoriteEntity entity) {
		FavoriteResponseModel model = FavoriteResponseModel.builder().build();
		BeanUtils.copyProperties(entity, model);
		model.setFavoriteId(entity.getT_id());
		model.setMarkedDate(entity.getT_dateCreated());
		model.setUser(UserResponseModel.fromEntity(entity.getUser()));
		model.setProvider(
				ProviderResponseModel.fromEntity(entity.getProvider()));
		return model;
	}
}