package de.solid.backend.rest;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.solid.backend.rest.model.helper.HelperRequestModel;
import de.solid.backend.rest.model.helper.HelperResponseModel;
import de.solid.backend.rest.service.HelperService;
import io.quarkus.security.Authenticated;

@Path("/helpers")
@Authenticated
@Tag(name = "Helpers", description = "all helper related method")
public class HelpersController extends BaseController {

  private static final Logger _log = LoggerFactory.getLogger(HelpersController.class);

  @Inject
  private HelperService helperService;

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(description = "create a new helper dataset")
  @APIResponse(responseCode = "201")
  @PermitAll
  @APIResponses(
      value = {@APIResponse(responseCode = "201", description = "helper successfully created")})
  public Response registerHelper(
      @RequestBody(description = "the helper model to save") HelperRequestModel model) {
    _log.info("createHelper was called for email {}", model.getAccount().getEmail());
    this.helperService.registerHelper(model);
    return HTTP_CREATED();
  }

  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(
      description = "update helper with provided model, helper is retrieved with email from JWT")
  @APIResponses(value = {
      @APIResponse(responseCode = "200", description = "helper successfully updated, return model",
          content = @Content(mediaType = MediaType.APPLICATION_JSON,
              schema = @Schema(implementation = HelperResponseModel.class))),
      @APIResponse(responseCode = "400",
          description = "required argument email not set (in case of email update)"),
      @APIResponse(responseCode = "404", description = "helper with account from jwt not found"),
      @APIResponse(responseCode = "409", description = "email to update already exists")})
  public Response updateHelper(
      @RequestBody(description = "the helper model to save") HelperRequestModel model) {
    _log.info("updateHelper was called for helper with email {}", getAuthenticatedUserEmail());
    return HTTP_OK(this.helperService.updateHelper(model, getAuthenticatedUserEmail()));
  }

  @Operation(
      description = "delete all helper dataset, account, keycloak login, removes from inquires, helper is retrieved with email from JWT")
  @DELETE
  @APIResponses(value = {
      @APIResponse(responseCode = "200", description = "helper successfully deleted"),
      @APIResponse(responseCode = "404", description = "helper with account from jwt not found")})
  public Response deleteHelper() {
    _log.info("deleteHelper was called for provider with email {}", getAuthenticatedUserEmail());
    this.helperService.deleteHelper(getAuthenticatedUserEmail());
    return HTTP_OK();
  }
}
