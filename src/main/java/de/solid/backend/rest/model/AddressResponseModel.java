package de.solid.backend.rest.model;

import javax.json.bind.annotation.JsonbNillable;
import de.solid.backend.dao.AddressEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * repsonse model for address data
 *
 */
@Getter
@Setter
@ToString
@JsonbNillable(value = true)
public class AddressResponseModel extends BaseResponseModel<AddressResponseModel, AddressEntity> {

  private String street;

  private String housenr;

  private String zip;

  private String city;

  private long addressId;

  @Override
  protected void mapAdditionalAttributes(AddressResponseModel model, AddressEntity entity) {
    model.setAddressId(entity.getT_id());
  }

}
