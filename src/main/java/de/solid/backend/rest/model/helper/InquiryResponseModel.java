package de.solid.backend.rest.model.helper;

import java.util.Date;
import javax.json.bind.annotation.JsonbDateFormat;
import javax.json.bind.annotation.JsonbNillable;
import de.solid.backend.dao.InquiryEntity;
import de.solid.backend.rest.model.BaseResponseModel;
import de.solid.backend.rest.model.provider.PublicProviderResponseModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * response model for inquiry data
 * 
 */
@Getter
@Setter
@ToString
@JsonbNillable(value = true)
public class InquiryResponseModel extends BaseResponseModel<InquiryResponseModel, InquiryEntity> {

  private long inquiryId;

  private PublicProviderResponseModel provider;

  @JsonbDateFormat(value = RESPONSE_DATE_FORMAT)
  private Date applyDate;

  private boolean contacted;

  @Override
  protected void mapAdditionalAttributes(InquiryResponseModel model, InquiryEntity entity) {
    model.setInquiryId(entity.getT_id());
    model.setApplyDate(entity.getT_dateCreated());
    if (entity.getProvider() != null) {
      model.setProvider(new PublicProviderResponseModel().fromEntity(entity.getProvider()));
    }
  }
}
