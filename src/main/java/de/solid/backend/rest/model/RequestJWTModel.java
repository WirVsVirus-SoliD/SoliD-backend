package de.solid.backend.rest.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestJWTModel {

  private String username;

  private String password;

  private String refresh_token;
}
