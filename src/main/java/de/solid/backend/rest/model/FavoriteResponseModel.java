package de.solid.backend.rest.model;

import java.util.Date;
import javax.json.bind.annotation.JsonbDateFormat;
import javax.json.bind.annotation.JsonbNillable;
import de.solid.backend.dao.FavoriteEntity;
import de.solid.backend.rest.model.provider.PublicProviderResponseModel;
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
@JsonbNillable(value = true)
public class FavoriteResponseModel
    extends BaseResponseModel<FavoriteResponseModel, FavoriteEntity> {

  private long favoriteId;

  private PublicProviderResponseModel provider;

  @JsonbDateFormat(value = RESPONSE_DATE_FORMAT)
  private Date markedDate;

  @Override
  protected void mapAdditionalAttributes(FavoriteResponseModel model, FavoriteEntity entity) {
    model.setFavoriteId(entity.getT_id());
    model.setMarkedDate(entity.getT_dateCreated());
    model.setProvider(new PublicProviderResponseModel().fromEntity(entity.getProvider()));
  }
}
