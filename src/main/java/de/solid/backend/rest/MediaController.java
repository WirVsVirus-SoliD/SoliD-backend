package de.solid.backend.rest;

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
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
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
