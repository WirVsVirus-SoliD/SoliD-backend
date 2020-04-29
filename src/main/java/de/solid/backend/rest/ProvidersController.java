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
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.solid.backend.rest.model.provider.GeoJsonResponseModel;
import de.solid.backend.rest.model.provider.ProviderRequestModel;
import de.solid.backend.rest.model.provider.PublicProviderResponseModel;
import de.solid.backend.rest.service.ProviderService;
import io.quarkus.security.Authenticated;

@Path("/providers")
@Authenticated
@Tag(name = "Providers", description = "all provider related method")
public class ProvidersController extends BaseController {

  private static final Logger _log = LoggerFactory.getLogger(ProvidersController.class);

  @Inject
  private ProviderService providerService;

  @Operation(description = "create a new provider dataset")
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @PermitAll
  @APIResponses(
      value = {@APIResponse(responseCode = "201", description = "provider successfully created")})
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
  @APIResponses(value = {
      @APIResponse(responseCode = "200",
          description = "provider successfully updated, return model",
          content = @Content(mediaType = MediaType.APPLICATION_JSON,
              schema = @Schema(implementation = PublicProviderResponseModel.class))),
      @APIResponse(responseCode = "400",
          description = "required argument email not set (in case of email update)"),
      @APIResponse(responseCode = "404", description = "provider with account from jwt not found"),
      @APIResponse(responseCode = "409", description = "email to update already exists")})
  public Response updateProvider(@RequestBody ProviderRequestModel providerRequestModel) {
    _log.info("updateProvider was called for provider with email {}", getAuthenticatedUserEmail());
    return HTTP_OK(
        this.providerService.updateProvider(providerRequestModel, getAuthenticatedUserEmail()));
  }

  @Operation(description = "delete provider")
  @DELETE
  @APIResponses(value = {
      @APIResponse(responseCode = "200", description = "provider successfully deleted"),
      @APIResponse(responseCode = "404", description = "provider with account from jwt not found")})
  public Response deleteProvider() {
    _log.info("deleteProvider was called for provider with email {}", getAuthenticatedUserEmail());
    this.providerService.deleteProvider(getAuthenticatedUserEmail());
    return HTTP_OK();
  }

  @Operation(description = "get providers for given lat, long and radius")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @PermitAll
  @APIResponses(value = {@APIResponse(responseCode = "200", description = "list of providers",
      content = @Content(mediaType = MediaType.APPLICATION_JSON,
          schema = @Schema(implementation = GeoJsonResponseModel.class)))})
  public Response getProvidersInRange(@QueryParam("latitude") float latitude,
      @QueryParam("longitude") float longitude, @QueryParam("radius") Optional<Double> radius) {
    _log.info("getProvidersInRange was called with lat {}, long {} and radius {}", latitude,
        longitude, radius);
    return HTTP_OK(this.providerService.getProvidersInRange(latitude, longitude, radius));
  }

  @Operation(description = "get provider for given providerId")
  @GET
  @Path("/{providerId}")
  @Produces(MediaType.APPLICATION_JSON)
  @PermitAll
  @APIResponses(value = {
      @APIResponse(responseCode = "200", description = "public model of provider",
          content = @Content(mediaType = MediaType.APPLICATION_JSON,
              schema = @Schema(implementation = GeoJsonResponseModel.class))),
      @APIResponse(responseCode = "404",
          description = "provider with given providerId does not exist")})
  public Response getProvider(@PathParam("providerId") long providerId) {
    _log.info("getProvider was called with providerId {}", providerId);
    return HTTP_OK(this.providerService.getProviderById(providerId));
  }
}
