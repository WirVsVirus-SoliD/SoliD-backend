package de.solid.backend.clients.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/*
 * request model for issuing a JWT
 * 
 */
@Getter
@Setter
@Builder
public class KeycloakGetJWTRequestModel {

  private String client_id;

  private String client_secret;

  private String username;

  private String password;

  private String grant_type;

}
