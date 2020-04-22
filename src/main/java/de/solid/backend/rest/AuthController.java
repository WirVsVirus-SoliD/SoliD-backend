package de.solid.backend.rest;

import java.util.Map;
import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import de.solid.backend.clients.model.KeycloakGetJWTResponseModel;
import de.solid.backend.dao.AccountEntity;
import de.solid.backend.dao.repository.HelpersRepository;
import de.solid.backend.dao.repository.ProvidersRepository;
import de.solid.backend.rest.model.auth.JWTResponseModel;
import de.solid.backend.rest.model.auth.LoginRequestModel;
import de.solid.backend.rest.model.auth.PasswordResetRequestModel;
import de.solid.backend.rest.model.auth.RefreshRequestModel;
import de.solid.backend.rest.model.helper.HelperResponseModel;
import de.solid.backend.rest.model.provider.ProviderResponseModel;
import de.solid.backend.rest.service.AccountService;
import de.solid.backend.rest.service.KeycloakService;
import de.solid.backend.rest.service.TicketService;
import de.solid.backend.rest.service.exception.NoSuchEntityException;
import io.quarkus.mailer.MailTemplate;

@Path("/auth")
@Tag(name = "Authentication", description = "authentication related method")
public class AuthController extends BaseController {

  @Inject
  private KeycloakService keycloakService;

  @Inject
  private TicketService ticketService;

  @Inject
  private AccountService accountService;

  @Inject
  private ProvidersRepository providersRepository;

  @Inject
  private HelpersRepository helpersRepository;

  @ConfigProperty(name = "ticket.password.reset.url")
  private String passwordResetUrl;

  @Inject
  private MailTemplate passwordResetMail;


  @Operation(
      description = "reset password using given values for the account related to ticket with given uuid")
  @POST
  @Path("/reset")
  @Consumes(MediaType.APPLICATION_JSON)
  @APIResponses(
      value = {@APIResponse(responseCode = "200", description = "password reset successfully"),
          @APIResponse(responseCode = "404", description = "user with email to reset not found"),
          @APIResponse(responseCode = "408", description = "reset ticket expired"),
          @APIResponse(responseCode = "409",
              description = "account password was already reset using this tickets")})
  @PermitAll
  @Transactional
  public Response reset(@RequestBody PasswordResetRequestModel model) {
    long accountId = this.ticketService.validateTicket(model.getUuid());
    this.accountService.resetPassword(accountId, model.getPassword());
    return HTTP_OK();
  }


  @Operation(description = "initialize password reset workflow")
  @GET
  @Path("/init-reset")
  @APIResponses(value = {
      @APIResponse(responseCode = "200", description = "password reset successfully initialized"),
      @APIResponse(responseCode = "404", description = "user with email to reset not found")})
  @PermitAll
  @Transactional
  public Response initReset(@QueryParam("email") String email) {
    AccountEntity account = this.accountService.findByEmail(email);
    if (account != null) {
      String uuid = this.ticketService.createAccountResetTicket(account.getT_id());
      this.passwordResetMail.to(account.getEmail()).data("firstName", account.getFirstName())
          .subject("soliD - Passwort vergessen")
          .data("passwordResetUrl", passwordResetUrl.replace("{uuid}", uuid)).send();
      return HTTP_OK();
    }
    return HTTP_NOT_FOUND();
  }

  @Operation(description = "Retrieve a JWT access token via username/password")
  @POST
  @Path("/login")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @APIResponses(value = {@APIResponse(responseCode = "200",
      description = "returns JWT (access and refresh token) and the logged in model (helper, provider)",
      content = @Content(mediaType = MediaType.APPLICATION_JSON,
          schema = @Schema(implementation = JWTResponseModel.class))),
      @APIResponse(responseCode = "403", description = "invalid credentials supplied")})
  public Response login(@RequestBody LoginRequestModel model) {
    KeycloakGetJWTResponseModel response = this.keycloakService.getJWTLogin(model);
    Object responseModel = this.getResponseObject(response.getAccess_token());
    return HTTP_OK(JWTResponseModel.builder().accessToken(response.getAccess_token())
        .accessTokenExpiresIn(response.getExpires_in()).refreshToken(response.getRefresh_token())
        .refreshTokenExpiresIn(response.getRefresh_expires_in())
        .type(this.getModelType(responseModel)).data(responseModel).build());
  }

  @Operation(description = "Retrieve a JWT access token via refresh_token")
  @POST
  @Path("/refresh")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @APIResponses(value = {@APIResponse(responseCode = "200",
      description = "returns JWT (access and refresh token) and the logged in model (helper, provider)",
      content = @Content(mediaType = MediaType.APPLICATION_JSON,
          schema = @Schema(implementation = JWTResponseModel.class))),
      @APIResponse(responseCode = "403", description = "invalid refresh token supplied")})
  public Response refresh(@RequestBody RefreshRequestModel model) {
    KeycloakGetJWTResponseModel response = this.keycloakService.getJWTRefresh(model);
    Object responseModel = this.getResponseObject(response.getAccess_token());
    return HTTP_OK(JWTResponseModel.builder().accessToken(response.getAccess_token())
        .accessTokenExpiresIn(response.getExpires_in()).refreshToken(response.getRefresh_token())
        .refreshTokenExpiresIn(response.getRefresh_expires_in())
        .type(this.getModelType(responseModel)).data(responseModel).build());
  }

  @Operation(description = "Activates account correlated with given uuid")
  @GET
  @Path("/activate")
  @Transactional
  @APIResponses(
      value = {@APIResponse(responseCode = "200", description = "account successfully activated"),
          @APIResponse(responseCode = "404",
              description = "activation ticket for given uuid cannot be found"),
          @APIResponse(responseCode = "408", description = "activation ticket expired"),
          @APIResponse(responseCode = "409", description = "account already activated")})
  public Response activateUser(@QueryParam("token") String uuid) {
    long relatedAccount = this.ticketService.validateTicket(uuid);
    this.accountService.activateAccount(relatedAccount);
    return HTTP_OK();
  }

  @Operation(description = "Validates the given JWT")
  @GET
  @Path("/validate")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  @APIResponses(
      value = {
          @APIResponse(responseCode = "200",
              description = "JWT validated, returns either helper or provider model",
              content = @Content(mediaType = MediaType.APPLICATION_JSON,
                  schema = @Schema(
                      anyOf = {HelperResponseModel.class, ProviderResponseModel.class}))),
          @APIResponse(responseCode = "403", description = "JWT is invalid")})
  public Response validateToken() {
    Object responseModel = this.getResponseObject(this.jwt.getRawToken());
    this.keycloakService.validateToken(this.jwt.getRawToken());
    return HTTP_OK(Map.of("type", this.getModelType(responseModel), "data", responseModel));
  }

  private String getModelType(Object model) {
    if (model instanceof HelperResponseModel) {
      return "helper";
    }
    return "provider";
  }

  private Object getResponseObject(String jwt) {
    String email = this.keycloakService.validateToken(jwt);
    Object model = null;

    if (this.helpersRepository.findByEmail(email) != null) {
      model = new HelperResponseModel().fromEntity(this.helpersRepository.findByEmail(email));
    } else if (this.providersRepository.findByEmail(email) != null) {
      model = new ProviderResponseModel().fromEntity(this.providersRepository.findByEmail(email));
    } else {
      throw new NoSuchEntityException(this.getClass(), "getResponseObject", String
          .format("Cannot find user with email %s in database!\nPassed in JWT %s", email, jwt));
    }
    return model;
  }
}
