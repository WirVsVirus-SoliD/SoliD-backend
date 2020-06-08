package de.solid.backend.clients.model;

import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/*
 * request model for creating or updating a keycloak user
 * 
 */
@Getter
@Setter
@Builder(toBuilder = true)
public class KeycloakUserRequestModel {

  private boolean enabled;

  private String email;

  private boolean emailVerified;

  private String firstName;

  private String lastName;

  private List<Credentials> credentials;

  private Map<String, Object> attributes;

  @Getter
  @Setter
  @RequiredArgsConstructor
  public static class Credentials {

    private String type = "password";

    @NonNull
    private String value;

    private boolean temporary = false;
  }
}
