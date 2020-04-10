package de.solid.backend.rest;

import java.util.Optional;
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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.solid.backend.rest.model.provider.ProviderRequestModel;
import de.solid.backend.rest.service.ProviderService;
import io.quarkus.security.Authenticated;

@Path("/providers")
@Authenticated
public class ProvidersController extends BaseController {

  private static final Logger _log = LoggerFactory.getLogger(ProvidersController.class);

  @Inject
  private ProviderService providerService;

  @Operation(description = "create a new provider dataset")
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @PermitAll
  public Response registerProvider(
      @RequestBody(description = "the provider model to save") ProviderRequestModel model) {
    _log.info("createProvider was called for email {}", model.getAccount().getEmail());
    this.providerService.insertProvider(model);
    return HTTP_CREATED();
  }

  @Operation(description = "update given provider with provided model")
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response updateProvider(@RequestBody ProviderRequestModel providerRequestModel) {
    _log.info("updateProvider was called for provider with email {}", getAuthenticatedUserEmail());
    this.providerService.updateProvider(providerRequestModel, getAuthenticatedUserEmail());
    return HTTP_OK();
  }

  @Operation(description = "delete provider")
  @DELETE
  public Response deleteProvider() {
    _log.info("deleteProvider was called for provider with email {}", getAuthenticatedUserEmail());
    this.providerService.deleteProvider(getAuthenticatedUserEmail());
    return HTTP_OK();
  }

  @Operation(description = "get providers for given lat, long and radius")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @PermitAll
  public Response getProvidersInRange(@QueryParam("latitude") float latitude,
      @QueryParam("longitude") float longitude, @QueryParam("radius") Optional<Double> radius) {
    _log.info("getProvidersInRange was called with lat {}, long {} and radius {}", latitude,
        longitude, radius);
    return HTTP_OK(this.providerService.getProvidersInRange(latitude, longitude, radius));
  }

  @Operation(description = "get all inquired helpers for given provider")
  @GET
  @Path("/inquired")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getHelpersInquried() {
    _log.info("getHelpersInquried was called for provider with email {}",
        getAuthenticatedUserEmail());
    return HTTP_OK(this.providerService.getHelpersInquired(getAuthenticatedUserEmail()));
  }

  @PUT
  @Operation(description = "toggle inquired helpers contacted state")
  @Path("/inquire/{inquiryid}")
  public Response toggleContactState(@Parameter(
      description = "id of the inquiry dataset") @PathParam("inquiryid") long inquiryId) {
    _log.info("toggleHelperContacted was called for provider with email {} and inquiryId {}",
        getAuthenticatedUserEmail(), inquiryId);
    this.providerService.toggleHelperContacted(inquiryId, getAuthenticatedUserEmail());
    return HTTP_OK();
  }

  @Operation(description = "remove an inquired helper but keeps the inquiry dataset")
  @DELETE
  @Path("/inquire/{inquiryid}")
  public Response removeFromInquiry(@Parameter(
      description = "id of the inquiry dataset") @PathParam("inquiryid") long inquiryId) {
    _log.info("removeFromInquiry was called for provider with email {} and inquiryId {}",
        getAuthenticatedUserEmail(), inquiryId);
    this.providerService.removeFromInquiry(inquiryId, getAuthenticatedUserEmail());
    return HTTP_OK();
  }
}
