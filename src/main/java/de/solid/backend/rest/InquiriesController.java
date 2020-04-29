package de.solid.backend.rest;

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
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.solid.backend.rest.model.helper.InquiryRequestModel;
import de.solid.backend.rest.model.helper.InquiryResponseModel;
import de.solid.backend.rest.service.HelperService;
import de.solid.backend.rest.service.InquiryService;
import de.solid.backend.rest.service.ProviderService;
import io.quarkus.security.Authenticated;

@Path("/inquiries")
@Authenticated
@Tag(name = "Inquiries", description = "methods to handle inquiries")
public class InquiriesController extends BaseController {

  private static final Logger _log = LoggerFactory.getLogger(InquiriesController.class);

  @Inject
  private HelperService helperService;

  @Inject
  private ProviderService providerService;

  @Inject
  private InquiryService inquiryService;

  @Operation(
      description = "inquire given helper for provided provider, helper is retrieved with email from JWT")
  @POST
  @Path("/")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @APIResponses(value = {
      @APIResponse(responseCode = "200",
          description = "successfully inquired user for given helper, return model",
          content = @Content(mediaType = MediaType.APPLICATION_JSON,
              schema = @Schema(implementation = InquiryResponseModel.class))),
      @APIResponse(responseCode = "404", description = "helper with account from jwt not found"),
      @APIResponse(responseCode = "409",
          description = "helper with account from jwt already inquired for given provider")})
  public Response inquireForProvider(@RequestBody InquiryRequestModel model) {
    _log.info("inquireForProvider was called for account with email {}",
        getAuthenticatedUserEmail());
    return HTTP_OK(this.helperService.inquireForProvider(model, getAuthenticatedUserEmail()));
  }

  @Operation(
      description = "based on the jwt (either helper or provider) the list of inquiries is returned")
  @GET
  @Path("/")
  @Produces(MediaType.APPLICATION_JSON)
  @APIResponses(value = {
      @APIResponse(responseCode = "200", description = "list of inquiries",
          content = @Content(mediaType = MediaType.APPLICATION_JSON,
              schema = @Schema(oneOf = {InquiryResponseModel.class,
                  de.solid.backend.rest.model.provider.InquiryResponseModel.class}))),
      @APIResponse(responseCode = "404",
          description = "helper or provider with account from jwt not found")})
  public Response getInquiries() {
    _log.info("getInquiries was called for account with email {}", getAuthenticatedUserEmail());
    return HTTP_OK(this.inquiryService.getInquires(getAuthenticatedUserEmail()));
  }

  @Operation(
      description = "remove an inquired helper or provider with account from jwt, but keeps the inquiry dataset")
  @DELETE
  @Path("/{inquiryid}")
  @APIResponses(value = {@APIResponse(responseCode = "200", description = "removed helper"),
      @APIResponse(responseCode = "404",
          description = "helper or provider with account from jwt not found")})
  public Response removeFromInquiry(@Parameter(
      description = "id of the inquiry dataset") @PathParam("inquiryid") long inquiryId) {
    _log.info("removeFromInquiry was called for provider with account {} and inquiryId {}",
        getAuthenticatedUserEmail(), inquiryId);
    this.inquiryService.removeInquiry(inquiryId, getAuthenticatedUserEmail());
    return HTTP_OK();
  }

  @PUT
  @Operation(description = "toggle inquired helpers contacted state")
  @Path("/{inquiryid}")
  @APIResponses(value = {
      @APIResponse(responseCode = "200", description = "contacted state toggled"),
      @APIResponse(responseCode = "404", description = "provider with account from jwt not found")})
  public Response toggleHelperContactedState(@Parameter(
      description = "id of the inquiry dataset") @PathParam("inquiryid") long inquiryId) {
    _log.info("toggleHelperContactedState was called for provider with account {} and inquiryId {}",
        getAuthenticatedUserEmail(), inquiryId);
    this.providerService.toggleHelperContacted(inquiryId, getAuthenticatedUserEmail());
    return HTTP_OK();
  }

}
