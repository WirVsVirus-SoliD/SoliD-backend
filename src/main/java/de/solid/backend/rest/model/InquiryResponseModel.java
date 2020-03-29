package de.solid.backend.rest.model;

import java.util.Date;

import de.solid.backend.dao.InquiryEntity;
import de.solid.backend.rest.model.base.BaseResponseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquiryResponseModel
		extends
			BaseResponseModel<InquiryResponseModel, InquiryEntity> {

	private long inquiryId;

	private HelperResponseModel helper;

	private Date applyDate;

	private boolean contacted;

	@Override
	protected void mapAdditionalAttributes(InquiryResponseModel model,
			InquiryEntity entity) {
		model.setInquiryId(entity.getT_id());
		model.setApplyDate(entity.getT_dateCreated());
		model.setHelper(
				new HelperResponseModel().fromEntity(entity.getHelper()));
	}
}