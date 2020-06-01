package de.solid.backend.rest.model.provider;

import java.util.Date;
import javax.json.bind.annotation.JsonbDateFormat;
import javax.json.bind.annotation.JsonbNillable;
import de.solid.backend.dao.InquiryEntity;
import de.solid.backend.rest.model.BaseResponseModel;
import de.solid.backend.rest.model.helper.HelperResponseModel;
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

  private HelperResponseModel helper;

  @JsonbDateFormat(value = RESPONSE_DATE_FORMAT)
  private Date applyDate;

  private boolean contacted;

  @JsonbDateFormat(value = RESPONSE_DATE_FORMAT)
  private Date contactedDate;

  @Override
  protected void mapAdditionalAttributes(InquiryResponseModel model, InquiryEntity entity) {
    model.setInquiryId(entity.getT_id());
    model.setApplyDate(entity.getT_dateCreated());
    model.setContactedDate(entity.getT_dateChanged());
    if (entity.getHelper() != null) {
      model.setHelper(new HelperResponseModel().fromEntity(entity.getHelper()));
    }
  }
}
