package de.solid.backend.rest.model;

import java.util.Date;
import de.solid.backend.dao.FavoriteEntity;
import de.solid.backend.rest.model.helper.HelperResponseModel;
import de.solid.backend.rest.model.provider.ProviderResponseModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * response model for favorite data
 *
 */
@Getter
@Setter
@ToString
public class FavoriteResponseModel
    extends BaseResponseModel<FavoriteResponseModel, FavoriteEntity> {

  private long favoriteId;

  private HelperResponseModel helper;

  private ProviderResponseModel provider;

  private Date markedDate;

  protected FavoriteResponseModel fromEntityInternal(FavoriteEntity entity) {
    FavoriteResponseModel model = new FavoriteResponseModel();
    copyProperties(entity, model);
    return model;
  }

  @Override
  protected void mapAdditionalAttributes(FavoriteResponseModel model, FavoriteEntity entity) {
    model.setFavoriteId(entity.getT_id());
    model.setMarkedDate(entity.getT_dateCreated());
    model.setHelper(new HelperResponseModel().fromEntity(entity.getHelper()));
    model.setProvider(new ProviderResponseModel().fromEntity(entity.getProvider()));
  }
}
