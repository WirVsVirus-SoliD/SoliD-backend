package de.solid.backend.rest.model.helper;

import java.util.Date;
import de.solid.backend.dao.InquiryEntity;
import de.solid.backend.rest.model.BaseResponseModel;
import de.solid.backend.rest.model.provider.ProviderResponseModel;
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
public class InquiryResponseModel extends BaseResponseModel<InquiryResponseModel, InquiryEntity> {

  private long inquiryId;

  private ProviderResponseModel provider;

  private Date applyDate;

  private boolean contacted;

  @Override
  protected void mapAdditionalAttributes(InquiryResponseModel model, InquiryEntity entity) {
    model.setInquiryId(entity.getT_id());
    model.setApplyDate(entity.getT_dateCreated());
    model.setProvider(new ProviderResponseModel().fromEntity(entity.getProvider()));
  }
}