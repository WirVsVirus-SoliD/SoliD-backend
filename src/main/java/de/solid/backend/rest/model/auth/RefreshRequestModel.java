package de.solid.backend.rest.model.auth;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * request model for issuing JWT with refresh token
 *
 */
@Getter
@Setter
@ToString
public class RefreshRequestModel {

  private String refreshToken;
}
