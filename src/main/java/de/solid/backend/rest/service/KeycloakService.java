package de.solid.backend.rest.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import org.apache.http.HttpStatus;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.solid.backend.clients.KeycloakRestClient;
import de.solid.backend.clients.model.KeycloakGetJWTResponseModel;
import de.solid.backend.clients.model.KeycloakUserRequestModel;
import de.solid.backend.clients.model.KeycloakUserRequestModel.Credentials;
import de.solid.backend.rest.model.auth.LoginRequestModel;
import de.solid.backend.rest.model.auth.RefreshRequestModel;
import de.solid.backend.rest.service.exception.RestClientException;
import de.solid.backend.rest.service.exception.UnauthorizedException;
import io.quarkus.cache.CacheResult;

/**
 * handles communication with keycloak server
 *
 */
@ApplicationScoped
public class KeycloakService {

  private static final Logger _log = LoggerFactory.getLogger(KeycloakService.class);

  private static final String GRANT_TYPE_PASSWORD = "password";

  private static final String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";

  @ConfigProperty(name = "quarkus.oidc.client-id")
  private String client_id;

  @ConfigProperty(name = "quarkus.oidc.credentials.secret")
  private String client_secret;

  @ConfigProperty(name = "keycloak.useradmin.login")
  private String keycloakUserAdminLogin;

  @ConfigProperty(name = "keycloak.useradmin.passwd")
  private String keycloakUserAdminPassword;

  @Inject
  @RestClient
  private KeycloakRestClient keycloakRestClient;

  /**
   * retrieve JWT based on username/password
   * 
   * @param model
   * @return
   */
  public KeycloakGetJWTResponseModel getJWTLogin(LoginRequestModel model) {
    return this.keycloakRestClient.getJWTToken(client_id, client_secret, model.getEmail(),
        model.getPassword(), null, GRANT_TYPE_PASSWORD);
  }

  /**
   * retrieve JWT based on refresh token
   * 
   * @param model
   * @return
   */
  public KeycloakGetJWTResponseModel getJWTRefresh(RefreshRequestModel model) {
    return this.keycloakRestClient.getJWTToken(client_id, client_secret, null, null,
        model.getRefreshToken(), GRANT_TYPE_REFRESH_TOKEN);
  }

  /**
   * create keycloak user with given data
   * 
   * @param firstName
   * @param lastName
   * @param email
   * @param password
   * @return keycloak userId
   * @throws unchecked KeycloakClientException when user cannot be created (internal error)
   */
  public String createUser(String firstName, String lastName, String email, String password) {
    KeycloakUserRequestModel model = KeycloakUserRequestModel.builder().email(email).enabled(false)
        .firstName(firstName).lastName(lastName).emailVerified(false)
        .credentials(Arrays.asList(new Credentials(password))).build();
    Response response = this.keycloakRestClient.createUser(getAuthorizationHeaderValue(), model);
    if (HttpStatus.SC_CREATED != response.getStatus()) {
      throw new RestClientException(this.getClass(), "createUser",
          String.format("email %s cannot be created - got response from keycloak %s", email,
              response.getStatus()));
    }
    _log.info(String.format("Successfully created keycloak user with email %s", email));
    return getUserIdByEmail(email);
  }

  /**
   * update the given keycloak user - set the solid-account id to allow relation of solid-account
   * <-> keycloak-user from bother sides
   * 
   * @param accountId
   * @param keycloakUserId
   */
  public void setAccountId(Long accountId, String keycloakUserId) {
    KeycloakUserRequestModel model =
        KeycloakUserRequestModel.builder().attributes(Map.of("accountId", accountId)).build();
    Response response =
        this.keycloakRestClient.updateUser(getAuthorizationHeaderValue(), keycloakUserId, model);
    if (HttpStatus.SC_NO_CONTENT != response.getStatus()) {
      throw new RestClientException(this.getClass(), "setAccountId", String.format(
          "for user with keycloakId %s accountId could not be updated - got response from keycloak %s",
          keycloakUserId, response.getStatus()));
    }
    _log.info(
        String.format("Successfully updated keycloak user with keycloakUserId %s", keycloakUserId));
  }

  /**
   * update keycloak user with given data
   * 
   * @param firstName
   * @param lastName
   * @param email
   * @param password
   * 
   * @throws unchecked KeycloakClientException when user cannot be created (internal error)
   */
  public void updateUser(String keycloakUserId, String firstName, String lastName, String email,
      String password) {
    KeycloakUserRequestModel model = KeycloakUserRequestModel.builder().email(email)
        .firstName(firstName).lastName(lastName).enabled(true).emailVerified(true).build();
    if (password != null) {
      model = model.toBuilder().credentials(Arrays.asList(new Credentials(password))).build();
    }

    Response response =
        this.keycloakRestClient.updateUser(getAuthorizationHeaderValue(), keycloakUserId, model);
    if (HttpStatus.SC_NO_CONTENT != response.getStatus()) {
      throw new RestClientException(this.getClass(), "updateUser", String.format(
          "user with keycloakId %s and email could not be updated - got response from keycloak %s",
          keycloakUserId, email, response.getStatus()));
    }
    _log.info(
        String.format("Successfully updated keycloak user with keycloakUserId %s", keycloakUserId));
  }

  /**
   * get keycloakUserId for given email and same username
   * 
   * @param email
   * @return keycloakUserId or null if user does not exist
   */
  public String getUserIdByEmail(String email) {
    List<Map<String, Object>> result =
        this.keycloakRestClient.getUserByEmail(getAuthorizationHeaderValue(), email);
    if (result != null && result.size() == 1 && result.get(0).containsKey("id")) {
      return result.get(0).get("id").toString();
    }
    throw new RestClientException(this.getClass(), "getUserIdByEmail",
        String.format("cannot retrieve keycloak user with email %s", email));
  }

  /**
   * delete keycloak user
   * 
   * @param keycloakUserId
   * @throws unchecked KeycloakClientException if user could not be deleted (internal error)
   */
  public void deleteUser(String keycloakUserId) {
    if (keycloakUserId != null) {
      Response response =
          this.keycloakRestClient.deleteUser(getAuthorizationHeaderValue(), keycloakUserId);
      if (HttpStatus.SC_NO_CONTENT != response.getStatus()) {
        throw new RestClientException(this.getClass(), "deleteUser",
            String.format(
                "user with keycloakUserId %s could not be deleted - got response from keycloak %s",
                keycloakUserId, response.getStatus()));
      }
      _log.info(String.format("Successfully removed keycloak user with keycloakUserId %s",
          keycloakUserId));
    }
  }

  /**
   * set keycloak user active (can retrieve JWT tokens)
   * 
   * @param keycloakUserId
   * @throws unchecked KeycloakClientException if user could not be activated (internal error)
   */
  public void activateUser(String keycloakUserId) {
    KeycloakUserRequestModel model =
        KeycloakUserRequestModel.builder().enabled(true).emailVerified(true).build();
    Response response =
        this.keycloakRestClient.updateUser(getAuthorizationHeaderValue(), keycloakUserId, model);
    if (HttpStatus.SC_NO_CONTENT != response.getStatus()) {
      throw new RestClientException(this.getClass(), "activateUser",
          String.format("user with keycloak Id %s could not be activated", keycloakUserId));
    }
  }

  public String validateToken(String token) {
    Map<String, Object> result =
        this.keycloakRestClient.validateToken(client_id, client_secret, token);
    if (result.containsKey("active") && Boolean.parseBoolean(result.get("active").toString())) {
      return result.get("email").toString();
    }
    throw new UnauthorizedException(this.getClass(), "validateToken",
        String.format("Passed token %s failed keycloak validation", token));
  }

  @CacheResult(cacheName = "adminJWTCache")
  private String getAuthorizationHeaderValue() {
    KeycloakGetJWTResponseModel adminToken = this.getJWTLogin(LoginRequestModel.builder()
        .email(keycloakUserAdminLogin).password(keycloakUserAdminPassword).build());
    return "Bearer " + adminToken.getAccess_token();
  }
}
