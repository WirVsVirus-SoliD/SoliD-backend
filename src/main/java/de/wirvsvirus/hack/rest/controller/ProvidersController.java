package de.wirvsvirus.hack.rest.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import de.wirvsvirus.hack.application.ApplicationConfiguration;
import de.wirvsvirus.hack.backend.dao.InquireEntity;
import de.wirvsvirus.hack.backend.dao.ProviderEntity;
import de.wirvsvirus.hack.backend.dao.repository.InquiresRepository;
import de.wirvsvirus.hack.backend.dao.repository.ProvidersRepository;
import de.wirvsvirus.hack.rest.model.Address;
import de.wirvsvirus.hack.rest.model.InquiryResponseModel;
import de.wirvsvirus.hack.rest.model.ProviderRequestModel;
import de.wirvsvirus.hack.rest.model.ProviderResponseModel;
import io.swagger.annotations.ApiOperation;

@CrossOrigin
@RestController
@RequestMapping("/providers")
public class ProvidersController {

	@Autowired
	private ApplicationConfiguration appConfig;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private InquiresRepository inquiresRepository;

	@Autowired
	private ProvidersRepository providersRepository;

	@ApiOperation(value = "get all inquired helpers for given provider")
	@RequestMapping(path = "/{providerId}/inquired", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<InquiryResponseModel> getInquiredHelpers(
			@PathVariable("providerId") int providerId) {
		List<InquireEntity> oe = this.inquiresRepository
				.findByProviderId(providerId);
		return oe.stream()
				.map(entity -> InquiryResponseModel.fromEntity(entity))
				.collect(Collectors.toList());
	}

	@ApiOperation(value = "toggle inquired helpers contacted state")
	@RequestMapping(path = "/inquire/{inquiryId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> toggleContactState(
			@PathVariable("inquiryId") int inquiryId) {
		InquireEntity entity = this.inquiresRepository.getOne(inquiryId);
		entity.setContacted(!entity.isContacted());
		entity = this.inquiresRepository.save(entity);
		return ResponseEntity.ok("");
	}

	@ApiOperation(value = "delete an inquired helper")
	@RequestMapping(path = "/inquire/{inquiryId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> deleteInquiry(
			@PathVariable("inquiryId") int inquiryId) {
		this.inquiresRepository.deleteById(inquiryId);
		return ResponseEntity.ok("");
	}

	@ApiOperation(value = "get provider with given id")
	@RequestMapping(path = "/{providerId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ProviderResponseModel getProvider(
			@PathVariable("providerId") int providerId) {
		ProviderEntity pe = getProviderEntity(providerId);
		return ProviderResponseModel.fromEntity(pe);
	}

	@ApiOperation(value = "get providers for given lat, long and radius")
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<ProviderResponseModel> getProvidersInRange(
			@RequestParam float latitude, @RequestParam float longitude,
			@RequestParam double radius) {
		List<ProviderEntity> list = this.providersRepository.findAll();
		return list.stream().filter(entity -> calculateDistance(entity,
				latitude, longitude) <= radius).map(entity -> {
					ProviderResponseModel model = ProviderResponseModel
							.fromEntity(entity);
					model.setDistance(
							calculateDistance(entity, latitude, longitude));
					return model;
				}).collect(Collectors.toList());
	}

	private double calculateDistance(ProviderEntity entity, double latitude,
			double longitude) {
		return LatLngTool.distance(
				new LatLng(entity.getLatitude(), entity.getLongitude()),
				new LatLng(latitude, longitude), LengthUnit.KILOMETER);
	}

	@ApiOperation(value = "delete provider with given id")
	@RequestMapping(path = "/{providerId}", method = RequestMethod.DELETE, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> deleteProvider(
			@PathVariable("providerId") int providerId) {
		this.providersRepository.deleteById(providerId);
		return ResponseEntity.ok("");
	}

	@ApiOperation(value = "register a new provider")
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ProviderResponseModel> registerProvider(
			@RequestBody ProviderRequestModel providerRequestModel) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(insertOrUpdate(null, providerRequestModel));
	}

	@ApiOperation(value = "upload a provider picture")
	@RequestMapping(path = "/{providerId}/upload-picture", method = RequestMethod.POST)
	@Transactional
	public ResponseEntity<String> uploadPicture(
			@PathVariable("providerId") int providerId,
			@RequestParam("file") MultipartFile file) {
		ProviderEntity entity = this.providersRepository.getOne(providerId);
		try {
			entity.setPicture(file.getBytes());
			entity.setPictureName(file.getName());
			entity.setPictureContentType(file.getContentType());
			this.providersRepository.save(entity);
			return ResponseEntity.status(HttpStatus.CREATED).body("");
		} catch (IOException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@ApiOperation(value = "download a provider picture")
	@GetMapping("/{providerId}/download-picture")
	@Transactional
	public ResponseEntity<org.springframework.core.io.Resource> downloadFile(
			@PathVariable("providerId") int providerId) {
		ProviderEntity entity = this.providersRepository.getOne(providerId);
		return ResponseEntity.ok()
				.contentType(MediaType
						.parseMediaType(entity.getPictureContentType()))
				.header(HttpHeaders.CONTENT_DISPOSITION,
						"attachment; filename=\"" + entity.getPictureName()
								+ "\"")
				.body(new ByteArrayResource(entity.getPicture()));
	}

	@ApiOperation(value = "update given provider with provided model")
	@RequestMapping(path = "/{providerId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> updateProvider(
			@PathVariable("providerId") int providerId,
			@RequestBody ProviderRequestModel providerRequestModel) {
		insertOrUpdate(this.getProviderEntity(providerId),
				providerRequestModel);
		return ResponseEntity.ok("");
	}

	private ProviderResponseModel insertOrUpdate(ProviderEntity providerEntity,
			ProviderRequestModel model) {
		ProviderEntity entity = ProviderRequestModel.toEntity(model);
		if (providerEntity != null) {
			entity.setT_id(providerEntity.getT_id());
			entity.setT_dateCreated(providerEntity.getT_dateCreated());
			entity.setT_userCreated(providerEntity.getT_userCreated());
			entity.setT_manadator(providerEntity.getT_manadator());
		}
		// check new address lat and long
		Pair<Double, Double> latLong = getLatLongForAddress(model.getAddress());
		entity.setLatitude(latLong.getFirst());
		entity.setLongitude(latLong.getSecond());
		entity = this.providersRepository.saveAndFlush(entity);

		return ProviderResponseModel.fromEntity(entity);
	}

	private ProviderEntity getProviderEntity(int providerId) {
		Optional<ProviderEntity> peOpt = this.providersRepository
				.findById(providerId);
		if (peOpt.isPresent()) {
			return peOpt.get();
		}
		throw new ResponseStatusException(HttpStatus.NOT_FOUND);
	}

	private Pair<Double, Double> getLatLongForAddress(Address address) {
		String result = restTemplate
				.getForEntity(getLatLongUrl(address), String.class).getBody();
		JsonObject latLongCall = new Gson().fromJson(result, JsonObject.class);
		if (latLongCall == null || latLongCall.has("error")) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Adresse konnte nicht ermittelt werden");
		}
		return Pair.of(latLongCall.get("latt").getAsDouble(),
				latLongCall.get("longt").getAsDouble());
	}

	private String getLatLongUrl(Address address) {
		StringBuffer addressUrlPrepare = new StringBuffer();
		addressUrlPrepare.append(address.getStreet());
		addressUrlPrepare.append("+");
		addressUrlPrepare.append(address.getHousenr());
		addressUrlPrepare.append("+");
		addressUrlPrepare.append(address.getZip());
		addressUrlPrepare.append("+");
		addressUrlPrepare.append(address.getCity());

		String url = appConfig.getRestEndpoint().replace("{ADR}",
				addressUrlPrepare.toString());
		return url;
	}

}
