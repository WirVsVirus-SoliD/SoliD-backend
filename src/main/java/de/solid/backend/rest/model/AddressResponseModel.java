package de.solid.backend.rest.model;

import de.solid.backend.dao.AddressEntity;
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
public class AddressResponseModel
		extends
			BaseResponseModel<AddressResponseModel, AddressEntity> {

	private String street;

	private String housenr;

	private String zip;

	private String city;

	private long addressId;

	@Override
	protected void mapAdditionalAttributes(AddressResponseModel model,
			AddressEntity entity) {
		model.setAddressId(entity.getT_id());
	}

}