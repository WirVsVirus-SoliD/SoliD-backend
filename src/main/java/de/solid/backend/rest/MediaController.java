package de.solid.backend.rest;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import de.solid.backend.dao.MediaEntity;

@Path("/media")
public class MediaController extends BaseController {

  @Inject
  private MediaService mediaService;

  @Operation(description = "upload a provider picture")
  @POST
  @Path("/upload-picture")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response uploadPicture(MultipartFormDataInput input) {
    this.mediaService.persistMedia(input);
    return HTTP_CREATED();
  }

  @Operation(description = "download a picture")
  @GET
  @Path("/{accountid}/download-picture")
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  public Response downloadPicture(@Parameter(
      description = "id of the account dataset") @PathParam("accountid") long accountId) {
    MediaEntity entity = this.mediaService.getMediaEntity(accountId);
    return Response.ok().entity(entity.getPicture()).header(HttpHeaders.CONTENT_DISPOSITION,
        "attachment; filename=\"" + entity.getPictureName() + "\"").build();
  }
}
