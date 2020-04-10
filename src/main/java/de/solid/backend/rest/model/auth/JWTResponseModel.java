package de.solid.backend.rest.model.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * response model for issued JWT
 *
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JWTResponseModel {

  private String accessToken;

  private int accessTokenExpiresIn;

  private String refreshToken;

  private int refreshTokenExpiresIn;

  // provider oder helper
  private String model_type;

  private Object model;
}
