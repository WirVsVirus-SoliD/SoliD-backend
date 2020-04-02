package de.solid.backend.rest.clients.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeycloakGetJWTResponseModel {

  private String access_token;

  private int expires_in;

  private int refresh_expires_in;

  private String refresh_token;

  private String token_type;

  private int not_before_policy;

  private String session_state;

  private String scope;

}
