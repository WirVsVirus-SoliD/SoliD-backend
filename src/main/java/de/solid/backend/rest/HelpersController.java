package de.solid.backend.rest;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import de.solid.backend.rest.model.helper.HelperRequestModel;
import de.solid.backend.rest.model.helper.InquiryRequestModel;
import de.solid.backend.rest.service.HelperService;
import io.quarkus.security.Authenticated;

@Path("/helpers")
@Produces(MediaType.TEXT_PLAIN)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class HelpersController extends BaseController {

  @Inject
  private HelperService helperService;

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(description = "create a new helper dataset")
  @APIResponse(responseCode = "201")
  @PermitAll
  public Response registerHelper(
      @RequestBody(description = "the helper model to save") HelperRequestModel model) {
    this.helperService.registerHelper(model);
    return HTTP_CREATED();
  }

  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(
      description = "update helper with provided model, helper is retrieved with email from JWT")
  public Response updateHelper(
      @RequestBody(description = "the helper model to save") HelperRequestModel model) {
    this.helperService.updateHelper(model, getAuthenticatedUserEmail());
    return HTTP_OK();
  }

  @Operation(
      description = "delete all helper dataset, account, keycloak login, removes from inquires, helper is retrieved with email from JWT")
  @DELETE
  public Response deleteHelper() {
    this.helperService.deleteHelper(getAuthenticatedUserEmail());
    return HTTP_OK();
  }

  @Operation(
      description = "inquire given helper for provided provider, helper is retrieved with email from JWT")
  @POST
  @Path("/inquire")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response inquireForProvider(@RequestBody InquiryRequestModel model) {
    this.helperService.inquireForProvider(model, getAuthenticatedUserEmail());
    return HTTP_OK();
  }

  @Operation(
      description = "get providers the given helper inquired for, helper is retrieved with email from JWT")
  @GET
  @Path("/inquired")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getInquiriesApplied() {
    return HTTP_OK(this.helperService.getProvidersInquiredFor(getAuthenticatedUserEmail()));
  }

  @Operation(
      description = "remove an inquired helper from the inquiry dataset but keeps the inquiry dataset, helper is retrieved with email from JWT")
  @DELETE
  @Path("/inquire/{providerid}")
  public Response removeFromInquiry(@Parameter(
      description = "id of the inquiry dataset") @PathParam("providerid") long providerid) {
    this.helperService.removeFromInquiry(providerid, getAuthenticatedUserEmail());
    return HTTP_OK();
  }
}
