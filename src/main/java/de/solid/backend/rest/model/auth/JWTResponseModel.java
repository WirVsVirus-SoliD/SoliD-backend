package de.solid.backend.rest.model.auth;

import javax.json.bind.annotation.JsonbNillable;
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
@JsonbNillable(value = true)
public class JWTResponseModel {

  private String accessToken;

  private int accessTokenExpiresIn;

  private String refreshToken;

  private int refreshTokenExpiresIn;

  // provider or helper
  private String type;

  private Object data;
}
