package de.solid.backend.rest;

import java.io.InputStream;
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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
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
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import de.solid.backend.dao.AddressEntity;
import de.solid.backend.dao.InquiryEntity;
import de.solid.backend.dao.ProviderEntity;
import de.solid.backend.dao.repository.AddressRepository;
import de.solid.backend.dao.repository.InquiriesRepository;
import de.solid.backend.dao.repository.ProvidersRepository;
import de.solid.backend.rest.clients.GeocodeResponse;
import de.solid.backend.rest.clients.GeocodeRestClient;
import de.solid.backend.rest.model.AddressRequestModel;
import de.solid.backend.rest.model.GeoJsonResponseModel;
import de.solid.backend.rest.model.InquiryResponseModel;
import de.solid.backend.rest.model.ProviderRequestModel;
import de.solid.backend.rest.model.ProviderResponseModel;

@OpenAPIDefinition(tags = {
		@Tag(name = "widget", description = "Widget operations."),
		@Tag(name = "gasket", description = "Operations related to gaskets")}, info = @Info(title = "Example API", version = "1.0.1", contact = @Contact(name = "Example API Support", url = "http://exampleurl.com/contact", email = "techsupport@example.com"), license = @License(name = "Apache 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0.html")))
@Path("/providers")
// @Authenticated
public class ProvidersController extends BaseController {

	private static final Logger _log = LoggerFactory
			.getLogger(ProvidersController.class);

	@Inject
	private ProvidersRepository providersRepository;

	@Inject
	private AddressRepository addressRepository;

	@Inject
	private InquiriesRepository inquiriesRepository;

	@Inject
	@RestClient
	private GeocodeRestClient geocodeRestClient;

	@Inject
	@Claim("email")
	private String email;

	@Operation(description = "get provider with given id")
	@GET
	@Path("/{providerid}")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public Response getProvider(
			@Parameter(description = "id of the provider dataset") @PathParam("providerid") long providerId) {
		ProviderEntity entity = this.providersRepository.findById(providerId);
		if (entity != null) {
			return HTTP_OK(new ProviderResponseModel().fromEntity(entity));
		}
		return NOT_FOUND();
	}

	@Operation(description = "create a new provider dataset")
	@APIResponse(responseCode = "201")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional
	public Response registerProvider(
			@RequestBody(description = "the provider model to save") ProviderRequestModel model) {
		model.setEmail(email);
		this.insertOrUpdateProvider(model.toEntity(Optional.empty()), model);
		return HTTP_CREATED();
	}

	@Operation(description = "update given provider with provided model")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public Response updateProvider(
			@RequestBody ProviderRequestModel providerRequestModel) {
		ProviderEntity entity = this.providersRepository.findByEmail(email);
		if (entity != null) {
			this.insertOrUpdateProvider(entity, providerRequestModel);
			return HTTP_OK();
		}
		return NOT_FOUND();
	}

	@Operation(description = "delete provider")
	@DELETE
	@Transactional
	public Response deleteProvider(
			@RequestBody ProviderRequestModel providerRequestModel) {
		ProviderEntity entity = this.providersRepository.findByEmail(email);
		if (entity != null) {
			this.providersRepository.delete(entity);
			return HTTP_OK();
		}
		return NOT_FOUND();
	}

	@Operation(description = "get providers for given lat, long and radius")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<GeoJsonResponseModel> getProvidersInRange(
			@QueryParam("latitude") float latitude,
			@QueryParam("longitude") float longitude,
			@QueryParam("radius") Optional<Double> radius) {
		List<ProviderEntity> list = this.providersRepository.findAll().list();
		return list.stream().filter(entity -> {
			if (radius.isPresent()) {
				return calculateDistance(entity, latitude, longitude) <= radius
						.get();
			} else
				return true;
		}).map(entity -> {
			GeoJsonResponseModel model = new GeoJsonResponseModel().fromEntity(entity);
			model.getProperties().setDistance(
					calculateDistance(entity, latitude, longitude));
			return model;
		}).collect(Collectors.toList());
	}

	@Operation(description = "upload a provider picture")
	@POST
	@Path("/upload-picture")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Transactional
	public Response uploadPicture(MultipartFormDataInput input) {
		ProviderEntity entity = this.providersRepository.findByEmail(email);
		if (entity != null) {
			try {
				InputPart inputPart = input.getParts().get(0);
				InputStream inputStream = inputPart.getBody(InputStream.class,
						null);
				byte[] bytes = IOUtils.toByteArray(inputStream);
				entity.setPicture(bytes);
				entity.setPictureName(getFilename(inputPart.getHeaders()
						.getFirst(HttpHeaders.CONTENT_DISPOSITION)));
				this.providersRepository.persist(entity);
				return HTTP_CREATED();
			} catch (Exception e) {
				_log.error("Error uploading file for provider " + email, e);
				return HTTP_INTERNAL();
			}
		}
		return NOT_FOUND();
	}

	@Operation(description = "download a provider picture")
	@GET
	@Path("/{providerid}/download-picture")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Transactional
	public Response downloadPicture(
			@Parameter(description = "id of the provider dataset") @PathParam("providerid") long providerId) {
		ProviderEntity entity = this.providersRepository.findById(providerId);
		if (entity != null) {
			return Response.ok().entity(entity.getPicture()).header(
					HttpHeaders.CONTENT_DISPOSITION,
					"attachment; filename=\"" + entity.getPictureName() + "\"")
					.build();
		} else {
			return NOT_FOUND();
		}
	}

	@Operation(description = "get all inquired helpers for given provider")
	@GET
	@Path("/inquired")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public Response getHelpersInquried() {
		ProviderEntity entity = this.providersRepository.findByEmail(email);
		if (entity != null) {
			List<InquiryEntity> entites = this.inquiriesRepository
					.findByProviderId(entity.getT_id());
			return HTTP_OK(entites.stream()
					.map(en -> new InquiryResponseModel().fromEntity(en))
					.collect(Collectors.toList()));
		}
		return NOT_FOUND();
	}

	@PUT
	@Operation(description = "toggle inquired helpers contacted state")
	@Path("/inquire/{inquiryid}")
	@Transactional
	public Response toggleContactState(
			@Parameter(description = "id of the inquiry dataset") @PathParam("inquiryid") long inquiryId) {
		InquiryEntity entity = this.inquiriesRepository.findById(inquiryId);
		if (entity != null) {
			ProviderEntity providerEntity = this.providersRepository
					.findByEmail(email);
			if (entity.getProvider().getT_id() == providerEntity.getT_id()) {
				entity.setContacted(!entity.isContacted());
				this.inquiriesRepository.persist(entity);
				return HTTP_OK();
			}
		}
		return NOT_FOUND();
	}

	@Operation(description = "remove an inquired helper but keeps the inquiry dataset")
	@DELETE
	@Path("/inquire/{inquiryid}")
	@Transactional
	public Response removeFromInquiry(
			@Parameter(description = "id of the inquiry dataset") @PathParam("inquiryid") long inquiryId) {
		ProviderEntity entity = this.providersRepository.findByEmail(email);
		if (entity != null) {
			InquiryEntity inquiryEntity = this.inquiriesRepository
					.findById(inquiryId);
			inquiryEntity.setProvider(null);
			this.inquiriesRepository.persist(inquiryEntity);
			return HTTP_OK();
		}
		return NOT_FOUND();
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

	private void insertOrUpdateProvider(ProviderEntity entity,
			ProviderRequestModel model) {
		AddressEntity address = model.getAddress().toEntity(Optional.empty());
		this.addressRepository.persist(address);
		entity.setAddress(address);
		this.setLatLong(model.getAddress(), entity);
		this.providersRepository.persist(entity);
	}

	private void setLatLong(AddressRequestModel address,
			ProviderEntity entity) {
		GeocodeResponse geocodeCallResult = this.geocodeRestClient
				.getLatLong(address.getGeocodeRequestParam());
		if (geocodeCallResult.getError() != null) {
			_log.warn(String.format(
					"Cannot retrieve lat and long for address %s", address));
		} else {
			entity.setLatitude(geocodeCallResult.getLatt());
			entity.setLongitude(geocodeCallResult.getLongt());
		}
	}

	private double calculateDistance(ProviderEntity entity, double latitude,
			double longitude) {
		return LatLngTool.distance(
				new LatLng(entity.getLatitude(), entity.getLongitude()),
				new LatLng(latitude, longitude), LengthUnit.KILOMETER);
	}

}