package de.solid.backend.rest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.HttpStatus;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import de.solid.backend.dao.HelperEntity;
import de.solid.backend.dao.InquiryEntity;
import de.solid.backend.dao.ProviderEntity;
import de.solid.backend.dao.repository.HelpersRepository;
import de.solid.backend.dao.repository.InquiriesRepository;
import de.solid.backend.dao.repository.ProvidersRepository;
import de.solid.backend.rest.model.HelperRequestModel;
import de.solid.backend.rest.model.HelperResponseModel;
import de.solid.backend.rest.model.InquiryRequestModel;
import de.solid.backend.rest.model.InquiryResponseModel;
import de.solid.backend.rest.model.ProviderResponseModel;
import io.quarkus.security.Authenticated;

@OpenAPIDefinition(tags = {
		@Tag(name = "widget", description = "Widget operations."),
		@Tag(name = "gasket", description = "Operations related to gaskets")}, info = @Info(title = "Example API", version = "1.0.1", contact = @Contact(name = "Example API Support", url = "http://exampleurl.com/contact", email = "techsupport@example.com"), license = @License(name = "Apache 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0.html")))
@Path("/helpers")
@Produces(MediaType.TEXT_PLAIN)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class HelpersController extends BaseController {

	@Inject
	private HelpersRepository helpersRepository;

	@Inject
	private InquiriesRepository inquiriesRepository;

	@Inject
	private ProvidersRepository providersRepository;

	@Inject
	@Claim("email")
	private String email;

	@Operation(description = "get helper with given id")
	@GET
	@Path("/{helperid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getHelper(
			@Parameter(description = "id of the helper dataset") @PathParam("helperid") long helperId) {
		HelperEntity helperEntity = helpersRepository.findById(helperId);
		if (helperEntity != null) {
			return HTTP_OK(new HelperResponseModel().fromEntity(helperEntity));
		}
		return NOT_FOUND();
	}

	@Operation(description = "delete helper with given id")
	@DELETE
	@Transactional
	public Response deleteHelper() {
		HelperEntity helperEntity = helpersRepository.findByEmail(email);
		if (helperEntity != null) {
			this.helpersRepository.delete(helperEntity);
			return HTTP_OK();
		}
		return NOT_FOUND();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(description = "create a new helper dataset")
	@APIResponse(responseCode = "201")
	@Transactional
	public Response registerHelper(
			@RequestBody(description = "the helper model to save") HelperRequestModel helperModel) {
		if (helpersRepository
				.findByMobileNumber(helperModel.getMobileNumber()) != null) {
			return HTTP_CONFLICT(
					"Helper with this mobile number already registered");
		}
		HelperEntity entity = helperModel.toEntity(Optional.empty());
		this.helpersRepository.persist(entity);
		return HTTP_OK(new HelperResponseModel().fromEntity(entity));
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(description = "update helper with provided model")
	@Transactional
	public Response updateHelper(
			@RequestBody(description = "the helper model to save") HelperRequestModel helperModel) {
		HelperEntity helperEntity = this.helpersRepository.findByEmail(email);
		if (helperEntity != null) {
			helperEntity = helperModel.toEntity(Optional.of(helperEntity));
			this.helpersRepository.persist(helperEntity);
			return HTTP_OK();
		}
		return NOT_FOUND();
	}

	@Operation(description = "inquire given helper for provided provider")
	@POST
	@Path("/inquire")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public Response inquireForProvider(@RequestBody InquiryRequestModel model) {
		HelperEntity helperEntity = this.helpersRepository.findByEmail(email);
		if (helperEntity != null) {
			ProviderEntity providerEntity = this.providersRepository
					.findById(model.getProviderId());
			if (providerEntity != null) {
				InquiryEntity entity = this.inquiriesRepository
						.findByHelperAndProvider(helperEntity.getT_id(),
								model.getProviderId());
				if (entity == null) {
					entity = InquiryEntity.builder().build();
					entity.setHelper(helperEntity);
					entity.setProvider(providerEntity);
					this.inquiriesRepository.persist(entity);
					return Response.status(HttpStatus.SC_CREATED).entity(
							new InquiryResponseModel().fromEntity(entity))
							.build();
				} else {
					return HTTP_CONFLICT("Helper already inquired");
				}
			}
		}
		return NOT_FOUND();
	}

	@Operation(description = "get providers the given helper inquired for")
	@GET
	@Path("/inquired")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getInquiriesApplied() {
		HelperEntity helperEntity = this.helpersRepository.findByEmail(email);
		if (helperEntity != null) {
			List<InquiryEntity> entites = this.inquiriesRepository
					.findByHelperId(helperEntity.getT_id());
			return HTTP_OK(entites.stream()
					.map(entity -> new ProviderResponseModel()
							.fromEntity(entity.getProvider()))
					.collect(Collectors.toList()));
		}
		return NOT_FOUND();
	}

	@Operation(description = "remove an inquired helper but keeps the inquiry dataset")
	@DELETE
	@Path("/inquire/{inquiryid}")
	@Transactional
	public Response removeFromInquiry(
			@Parameter(description = "id of the inquiry dataset") @PathParam("inquiryid") long inquiryId) {
		HelperEntity helperEntity = this.helpersRepository.findByEmail(email);
		if (helperEntity != null) {
			InquiryEntity entity = this.inquiriesRepository.findById(inquiryId);
			entity.setHelper(null);
			this.inquiriesRepository.persist(entity);
			return HTTP_OK();
		}
		return NOT_FOUND();
	}
}