package de.solid.backend.rest;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import de.solid.backend.rest.model.FavoriteRequestModel;
import de.solid.backend.rest.model.FavoriteResponseModel;
import de.solid.backend.rest.service.HelperService;
import io.quarkus.security.Authenticated;

@Path("/favorites")
@Authenticated
@Tag(name = "Favorites", description = "handle helper related favorites management")
public class FavoritesController extends BaseController {

  @Inject
  private HelperService helperService;

  @Operation(description = "saves the given provider as favorite for the given helper")
  @POST
  @Path("/")
  @Consumes(MediaType.APPLICATION_JSON)
  @Transactional
  @APIResponses(value = {
      @APIResponse(responseCode = "200",
          description = "successfully marked as favorite, return model",
          content = @Content(mediaType = MediaType.APPLICATION_JSON,
              schema = @Schema(implementation = FavoriteResponseModel.class))),
      @APIResponse(responseCode = "404", description = "helper with account from jwt not found"),
      @APIResponse(responseCode = "409",
          description = "provider is already marked as favorite for helper with account from jwt")})
  public Response markFavorite(@RequestBody FavoriteRequestModel model) {
    this.helperService.markFavorite(model, getAuthenticatedUserEmail());
    return HTTP_OK();
  }

  @DELETE
  @Path("/{favoriteid}")
  @Operation(description = "remove a favorite with given favoriteId")
  @Transactional
  @APIResponses(value = {
      @APIResponse(responseCode = "200", description = "successfully removed favorite"),
      @APIResponse(responseCode = "404", description = "helper with account from jwt not found"),
      @APIResponse(responseCode = "403",
          description = "tried to remove favorite but favorite-entry does not belong to helper with account from jwt")})
  public Response deleteFavorite(@Parameter(
      description = "id of the favorite dataset") @PathParam("favoriteid") long favoriteId) {
    this.helperService.deleteFavorite(favoriteId, getAuthenticatedUserEmail());
    return HTTP_OK();
  }

  @GET
  @Path("/")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(description = "get all marked favorites for this helper")
  @APIResponses(value = {
      @APIResponse(responseCode = "200", description = "list of favorite entries",
          content = @Content(mediaType = MediaType.APPLICATION_JSON,
              schema = @Schema(implementation = FavoriteResponseModel.class))),
      @APIResponse(responseCode = "404",
          description = "helper with given account from jwt not found")})
  public Response getFavorites() {
    return HTTP_OK(this.helperService.getFavorites(getAuthenticatedUserEmail()));
  }
}
