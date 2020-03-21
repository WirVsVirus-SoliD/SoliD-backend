package de.wirvsvirus.hack.rest.model;

import java.util.Date;

import org.springframework.beans.BeanUtils;

import de.wirvsvirus.hack.backend.dao.FavoriteEntity;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FavoriteModel {

	private int favoriteId;

	private UserModel user;

	private ProviderModel provider;

	private Date markedDate;

	public static FavoriteModel fromEntity(FavoriteEntity entity) {
		FavoriteModel model = FavoriteModel.builder().build();
		BeanUtils.copyProperties(entity, model);
		model.setFavoriteId(entity.getT_id());
		model.setMarkedDate(entity.getT_dateCreated());
		model.setUser(UserModel.fromEntity(entity.getUser()));
		model.setProvider(ProviderModel.fromEntity(entity.getProvider()));
		return model;
	}
}