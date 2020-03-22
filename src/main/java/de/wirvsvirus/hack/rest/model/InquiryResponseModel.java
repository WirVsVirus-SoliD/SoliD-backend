package de.wirvsvirus.hack.rest.model;

import java.util.Date;

import org.springframework.beans.BeanUtils;

import de.wirvsvirus.hack.backend.dao.InquireEntity;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InquiryResponseModel {

	private int offerId;

	private HelperResponseModel helper;

	private Date applyDate;

	private boolean contacted;

	public static InquiryResponseModel fromEntity(InquireEntity entity) {
		InquiryResponseModel model = InquiryResponseModel.builder().build();
		BeanUtils.copyProperties(entity, model);
		model.setOfferId(entity.getT_id());
		model.setApplyDate(entity.getT_dateCreated());
		model.setHelper(HelperResponseModel.fromEntity(entity.getHelper()));
		return model;
	}
}