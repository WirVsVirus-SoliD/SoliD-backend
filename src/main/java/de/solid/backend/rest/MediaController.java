package de.solid.backend.rest;

import java.util.Map;
import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.http.HttpStatus;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import de.solid.backend.dao.MediaEntity;
import io.quarkus.security.Authenticated;

@Path("/media")
@Authenticated
public class MediaController extends BaseController {

  @Inject
  private MediaService mediaService;

  @Operation(description = "upload a provider picture")
  @POST
  @Path("/upload-picture")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @APIResponses(value = {@APIResponse(responseCode = "201", description = "Upload successful"),
      @APIResponse(responseCode = "400", description = "invalid media supplied",
          content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA))})
  public Response uploadPicture(MultipartFormDataInput input) {
    this.mediaService.persistMedia(input, getAuthenticatedUserEmail());
    return HTTP_CREATED();
  }

  @Operation(description = "download a picture")
  @GET
  @Path("/{accountid}/download-picture")
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  @Transactional
  @PermitAll
  @APIResponses(value = {
      @APIResponse(responseCode = "200",
          description = "JVM system properties of a particular host.",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = Map.class))),
      @APIResponse(responseCode = "204", description = "account has no picture")})
  public Response downloadPicture(@Parameter(
      description = "id of the account dataset") @PathParam("accountid") long accountId) {
    MediaEntity entity = this.mediaService.getMediaEntity(accountId);
    if (entity != null) {
      return Response.ok().entity(entity.getMedia()).header(HttpHeaders.CONTENT_DISPOSITION,
          "attachment; filename=\"" + entity.getMediaName() + "\"").build();
    } else
      return Response.status(HttpStatus.SC_NO_CONTENT).build();
  }
}
