package de.solid.backend.rest;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import de.solid.backend.clients.model.KeycloakGetJWTResponseModel;
import de.solid.backend.rest.model.auth.LoginRequestModel;
import de.solid.backend.rest.model.auth.RefreshRequestModel;
import de.solid.backend.rest.model.auth.JWTResponseModel;
import de.solid.backend.rest.service.AccountService;
import de.solid.backend.rest.service.KeycloakService;
import de.solid.backend.rest.service.TicketService;

@Path("/auth")
public class AuthController extends BaseController {

  @Inject
  private KeycloakService keycloakService;

  @Inject
  private TicketService ticketService;

  @Inject
  private AccountService accountService;

  @Operation(description = "Retrieve a JWT access token via username/password ")
  @POST
  @Path("/login")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response login(@RequestBody LoginRequestModel model) {
    KeycloakGetJWTResponseModel response = this.keycloakService.getJWTLogin(model);
    return HTTP_OK(JWTResponseModel.builder().accessToken(response.getAccess_token())
        .accessTokenExpiresIn(response.getExpires_in()).refreshToken(response.getRefresh_token())
        .refreshTokenExpiresIn(response.getRefresh_expires_in()).build());
  }

  @Operation(description = "Retrieve a JWT access token via refresh_token")
  @POST
  @Path("/refresh")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response refresh(@RequestBody RefreshRequestModel model) {
    KeycloakGetJWTResponseModel response = this.keycloakService.getJWTRefresh(model);
    return HTTP_OK(JWTResponseModel.builder().accessToken(response.getAccess_token())
        .accessTokenExpiresIn(response.getExpires_in()).refreshToken(response.getRefresh_token())
        .refreshTokenExpiresIn(response.getRefresh_expires_in()).build());
  }

  @Operation(description = "Activates user correlated with given uuid")
  @GET
  @Path("/activate/{uuid}")
  @Transactional
  public Response activateUser(@PathParam("uuid") String uuid) {
    long relatedAccount = this.ticketService.validateTicket(uuid);
    this.accountService.activateAccount(relatedAccount);
    return HTTP_OK();
  }
}
