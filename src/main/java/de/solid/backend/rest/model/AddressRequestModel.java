package de.solid.backend.rest.model;

import de.solid.backend.dao.AddressEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * request model for address data
 *
 */
@Getter
@Setter
@ToString
public class AddressRequestModel extends BaseRequestModel<AddressRequestModel, AddressEntity> {

  private String street;

  private String housenr;

  private String zip;

  private String city;

  @Override
  protected void mapAdditionalAttributes(AddressEntity entityl) {}
}
