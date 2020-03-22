package de.wirvsvirus.hack.rest.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import de.wirvsvirus.hack.backend.dao.FavoriteEntity;
import de.wirvsvirus.hack.backend.dao.HelperEntity;
import de.wirvsvirus.hack.backend.dao.InquireEntity;
import de.wirvsvirus.hack.backend.dao.repository.FavoritesRepository;
import de.wirvsvirus.hack.backend.dao.repository.HelpersRepository;
import de.wirvsvirus.hack.backend.dao.repository.InquiresRepository;
import de.wirvsvirus.hack.backend.dao.repository.ProvidersRepository;
import de.wirvsvirus.hack.rest.model.FavoriteRequestModel;
import de.wirvsvirus.hack.rest.model.FavoriteResponseModel;
import de.wirvsvirus.hack.rest.model.HelperRequestModel;
import de.wirvsvirus.hack.rest.model.HelperResponseModel;
import de.wirvsvirus.hack.rest.model.InquiryRequestModel;
import de.wirvsvirus.hack.rest.model.InquiryResponseModel;
import de.wirvsvirus.hack.rest.model.ProviderResponseModel;
import io.swagger.annotations.ApiOperation;

@CrossOrigin
@RestController
@RequestMapping("/helpers")
public class HelpersController {

	@Autowired
	private HelpersRepository helpersRepository;

	@Autowired
	private InquiresRepository inquiresRepository;

	@Autowired
	private FavoritesRepository favoritesRepository;

	@Autowired
	private ProvidersRepository providersRepository;

	@ApiOperation(value = "remove a favorite with given favoriteId")
	@RequestMapping(path = "/favorites/{favoriteId}", method = RequestMethod.DELETE, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> deleteFavorite(@RequestParam int favoriteId) {
		this.favoritesRepository.deleteById(favoriteId);
		return ResponseEntity.ok("");
	}

	@ApiOperation(value = "get all favorites for given helper")
	@RequestMapping(path = "/{helperId}/favorites", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<FavoriteResponseModel> getFavorites(
			@PathVariable("helperId") int helperId) {
		List<FavoriteEntity> oe = this.favoritesRepository
				.findByHelperId(helperId);
		return oe.stream()
				.map(entity -> FavoriteResponseModel.fromEntity(entity))
				.collect(Collectors.toList());
	}

	@ApiOperation(value = "saves the given provider as favorite for the given helper")
	@RequestMapping(path = "/favorites", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<FavoriteResponseModel> markFavorite(
			@RequestBody FavoriteRequestModel favoriteRequestModel) {
		try {
			FavoriteEntity fe = this.favoritesRepository
					.findByHelperAndProvider(favoriteRequestModel.getHelperId(),
							favoriteRequestModel.getProviderId());
			if (fe == null) {
				fe = FavoriteEntity.builder().build();
				fe.setHelper(this.helpersRepository
						.getOne(favoriteRequestModel.getHelperId()));
				fe.setProvider(this.providersRepository
						.getOne(favoriteRequestModel.getProviderId()));
				fe = this.favoritesRepository.save(fe);
			}
			return ResponseEntity.status(HttpStatus.CREATED)
					.body(FavoriteResponseModel.fromEntity(fe));
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@ApiOperation(value = "get helper with given id")
	@RequestMapping(path = "/{helperId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public HelperResponseModel getHelpers(
			@PathVariable("helperId") int helperId) {
		return HelperResponseModel.fromEntity(getHelperEntity(helperId));
	}

	@ApiOperation(value = "given helper applies for provider")
	@RequestMapping(path = "/inquire", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<InquiryResponseModel> inquireForProvider(
			@RequestBody InquiryRequestModel model) {
		InquireEntity entity = this.inquiresRepository.findByHelperAndProvider(
				model.getHelperId(), model.getProviderId());
		if (entity == null) {
			try {
				entity = InquireEntity.builder().build();
				entity.setHelper(
						this.helpersRepository.getOne(model.getHelperId()));
				entity.setProvider(
						this.providersRepository.getOne(model.getProviderId()));
				entity = this.inquiresRepository.save(entity);
			} catch (Exception e) {
				throw new ResponseStatusException(
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		InquiryResponseModel offerModel = InquiryResponseModel
				.fromEntity(entity);

		return ResponseEntity.status(HttpStatus.CREATED).body(offerModel);
	}

	@ApiOperation(value = "delete an inquired helper")
	@RequestMapping(path = "/inquire/{inquiryId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> deleteInquiry(
			@PathVariable("inquiryId") int inquiryId) {
		this.inquiresRepository.deleteById(inquiryId);
		return ResponseEntity.ok("");
	}

	@ApiOperation(value = "get providers the given helper inquired for")
	@RequestMapping(path = "/{helperId}/inquired", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<ProviderResponseModel> getOffersApplied(
			@PathVariable("helperId") int helperId) {
		List<InquireEntity> oe = this.inquiresRepository
				.findByHelperId(helperId);
		return oe.stream()
				.map(entity -> ProviderResponseModel
						.fromEntity(entity.getProvider()))
				.collect(Collectors.toList());
	}

	@ApiOperation(value = "register a helper")
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<HelperResponseModel> registerUser(
			@RequestBody HelperRequestModel helperModel) {
		if (this.helpersRepository
				.findByMobileNumber(helperModel.getMobileNumber()) != null) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"Helper already registered");
		}
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(inserOrUpdateHelper(HelperEntity.builder().build(),
						helperModel));
	}

	@ApiOperation(value = "update the given helper with provided model")
	@RequestMapping(path = "/{helperId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> updateUser(
			@PathVariable("helperId") int helperId,
			@RequestBody HelperRequestModel helperModel) {
		inserOrUpdateHelper(getHelperEntity(helperId), helperModel);
		return ResponseEntity.ok("");
	}

	@ApiOperation(value = "delete helper with given id")
	@RequestMapping(path = "/{helperId}", method = RequestMethod.DELETE, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> deleteUser(
			@PathVariable("helperId") int helperId) {
		this.helpersRepository.deleteById(helperId);
		return ResponseEntity.ok("");
	}

	private HelperResponseModel inserOrUpdateHelper(HelperEntity helperEntity,
			HelperRequestModel helperModel) {
		BeanUtils.copyProperties(helperModel, helperEntity);
		helperEntity = this.helpersRepository.save(helperEntity);
		return HelperResponseModel.fromEntity(helperEntity);
	}

	private HelperEntity getHelperEntity(int helperId) {
		Optional<HelperEntity> ueOpt = this.helpersRepository
				.findById(helperId);
		if (ueOpt.isPresent()) {
			return ueOpt.get();
		}
		throw new ResponseStatusException(HttpStatus.NOT_FOUND);
	}
}
