package de.solid.backend.rest;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.springframework.util.StringUtils;
import de.solid.backend.rest.clients.KeycloakRestClient;
import de.solid.backend.rest.clients.model.KeycloakGetJWTResponseModel;
import de.solid.backend.rest.model.RequestJWTModel;
import de.solid.backend.rest.model.ResponseJWTModel;

@Path("/auth")
public class AuthController extends BaseController {

  private static final String GRANT_TYPE_PASSWORD = "password";

  private static final String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";

  @ConfigProperty(name = "quarkus.oidc.client-id")
  private String client_id;

  @ConfigProperty(name = "quarkus.oidc.credentials.secret")
  private String client_secret;


  @Inject
  @RestClient
  private KeycloakRestClient keycloakRestClient;

  @Operation(
      description = "Retrieve a JWT access token either via username/password or via refresh_token")
  @POST
  @Path("/request")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response getJWTToken(@RequestBody RequestJWTModel model) {
    String grant_type = GRANT_TYPE_PASSWORD;
    if (!StringUtils.isEmpty(model.getRefresh_token())) {
      grant_type = GRANT_TYPE_REFRESH_TOKEN;
    }

    KeycloakGetJWTResponseModel response =
        this.keycloakRestClient.getJWTToken(client_id, client_secret, model.getUsername(),
            model.getPassword(), model.getRefresh_token(), grant_type);
    return HTTP_OK(ResponseJWTModel.builder().accessToken(response.getAccess_token())
        .accessTokenExpiresIn(response.getExpires_in()).refreshToken(response.getRefresh_token())
        .refreshTokenExpiresIn(response.getRefresh_expires_in()).build());
  }
}
