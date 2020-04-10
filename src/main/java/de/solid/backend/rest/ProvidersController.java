package de.solid.backend.rest;

import java.io.InputStream;
import java.util.Optional;
import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.io.IOUtils;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.solid.backend.dao.ProviderEntity;
import de.solid.backend.dao.repository.ProvidersRepository;
import de.solid.backend.rest.model.provider.ProviderRequestModel;
import de.solid.backend.rest.service.ProviderService;
import io.quarkus.security.Authenticated;

@Path("/providers")
@Authenticated
public class ProvidersController extends BaseController {

  private static final Logger _log = LoggerFactory.getLogger(ProvidersController.class);

  @Inject
  private ProvidersRepository providersRepository;

  @Inject
  private ProviderService providerService;

  @Operation(description = "create a new provider dataset")
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @PermitAll
  public Response registerProvider(
      @RequestBody(description = "the provider model to save") ProviderRequestModel model) {
    this.providerService.insertProvider(model);
    return HTTP_CREATED();
  }

  @Operation(description = "update given provider with provided model")
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response updateProvider(@RequestBody ProviderRequestModel providerRequestModel) {
    this.providerService.updateProvider(providerRequestModel, getAuthenticatedUserEmail());
    return HTTP_OK();
  }

  @Operation(description = "delete provider")
  @DELETE
  public Response deleteProvider() {
    this.providerService.deleteProvider(getAuthenticatedUserEmail());
    return HTTP_OK();
  }

  @Operation(description = "get providers for given lat, long and radius")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @PermitAll
  public Response getProvidersInRange(@QueryParam("latitude") float latitude,
      @QueryParam("longitude") float longitude, @QueryParam("radius") Optional<Double> radius) {
    return HTTP_OK(this.providerService.getProvidersInRange(latitude, longitude, radius));
  }

  @Operation(description = "upload a provider picture")
  @POST
  @Path("/upload-picture")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Transactional
  public Response uploadPicture(MultipartFormDataInput input) {
    ProviderEntity entity = this.providersRepository.findByEmail(getAuthenticatedUserEmail());
    if (entity != null) {
      try {
        InputPart inputPart = input.getParts().get(0);
        InputStream inputStream = inputPart.getBody(InputStream.class, null);
        byte[] bytes = IOUtils.toByteArray(inputStream);
        entity.setPicture(bytes);
        entity.setPictureName(
            getFilename(inputPart.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION)));
        this.providersRepository.persist(entity);
        return HTTP_CREATED();
      } catch (Exception e) {
        _log.error("Error uploading file for provider " + getAuthenticatedUserEmail(), e);
        return HTTP_INTERNAL();
      }
    }
    return HTTP_NOT_FOUND();
  }

  @Operation(description = "download a provider picture")
  @GET
  @Path("/{providerid}/download-picture")
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  @Transactional
  public Response downloadPicture(@Parameter(
      description = "id of the provider dataset") @PathParam("providerid") long providerId) {
    ProviderEntity entity = this.providersRepository.findById(providerId);
    if (entity != null) {
      return Response.ok().entity(entity.getPicture()).header(HttpHeaders.CONTENT_DISPOSITION,
          "attachment; filename=\"" + entity.getPictureName() + "\"").build();
    } else {
      return HTTP_NOT_FOUND();
    }
  }

  @Operation(description = "get all inquired helpers for given provider")
  @GET
  @Path("/inquired")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getHelpersInquried() {
    return HTTP_OK(this.providerService.getHelpersInquired(getAuthenticatedUserEmail()));
  }

  @PUT
  @Operation(description = "toggle inquired helpers contacted state")
  @Path("/inquire/{inquiryid}")
  public Response toggleContactState(@Parameter(
      description = "id of the inquiry dataset") @PathParam("inquiryid") long inquiryId) {
    this.providerService.toggleHelperContacted(inquiryId, getAuthenticatedUserEmail());
    return HTTP_OK();
  }

  @Operation(description = "remove an inquired helper but keeps the inquiry dataset")
  @DELETE
  @Path("/inquire/{inquiryid}")
  public Response removeFromInquiry(@Parameter(
      description = "id of the inquiry dataset") @PathParam("inquiryid") long inquiryId) {
    this.providerService.removeFromInquiry(inquiryId, getAuthenticatedUserEmail());
    return HTTP_OK();
  }

  private String getFilename(String header) {
    String[] contentDisposition = header.split(";");
    for (String filename : contentDisposition) {
      if ((filename.trim().startsWith("filename"))) {

        String[] name = filename.split("=");

        String finalFileName = name[1].trim().replaceAll("\"", "");
        return finalFileName;
      }
    }
    return "file";
  }



}
