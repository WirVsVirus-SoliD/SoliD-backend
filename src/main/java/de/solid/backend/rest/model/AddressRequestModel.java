package de.solid.backend.rest.model;

import de.solid.backend.dao.AddressEntity;
import de.solid.backend.rest.model.base.BaseRequestModel;
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
public class AddressRequestModel
		extends
			BaseRequestModel<AddressRequestModel, AddressEntity> {

	private String street;

	private String housenr;

	private String zip;

	private String city;

	@Override
	protected void mapAdditionalAttributes(AddressEntity entityl) {
	}

	public String getGeocodeRequestParam() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getStreet());
		buffer.append("+");
		buffer.append(getHousenr());
		buffer.append("+");
		buffer.append(getZip());
		buffer.append("+");
		buffer.append(getCity());

		return buffer.toString();
	}
}
