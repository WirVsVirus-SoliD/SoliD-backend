package de.solid.backend.rest.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ResponseJWTModel {

  private String accessToken;

  private int accessTokenExpiresIn;

  private String refreshToken;

  private int refreshTokenExpiresIn;
}
