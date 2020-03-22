package de.wirvsvirus.hack.rest.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import de.wirvsvirus.hack.backend.dao.FavoriteEntity;
import de.wirvsvirus.hack.backend.dao.OfferEntity;
import de.wirvsvirus.hack.backend.dao.UserEntity;
import de.wirvsvirus.hack.backend.dao.repository.FavoritesRepository;
import de.wirvsvirus.hack.backend.dao.repository.OffersRepository;
import de.wirvsvirus.hack.backend.dao.repository.UsersRepository;
import de.wirvsvirus.hack.rest.model.ProviderResponseModel;
import de.wirvsvirus.hack.rest.model.UserRequestModel;
import de.wirvsvirus.hack.rest.model.UserResponseModel;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/users")
public class UsersController {

	@Autowired
	private UsersRepository userRepository;

	@Autowired
	private OffersRepository offerRepository;

	@Autowired
	private FavoritesRepository favoriteRepository;

	@ApiOperation(value = "get user with given id")
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public UserResponseModel getUser(@RequestParam int userId) {
		return UserResponseModel.fromEntity(getUserEntity(userId));
	}

	@ApiOperation(value = "get all offers for given user")
	@RequestMapping(path = "/{id}/offers", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<ProviderResponseModel> getOffers(
			@PathVariable("id") int userId) {
		List<OfferEntity> oe = this.offerRepository.findByUserId(userId);
		return oe.stream()
				.map(entity -> ProviderResponseModel
						.fromEntity(entity.getProvider()))
				.collect(Collectors.toList());
	}

	@ApiOperation(value = "get all favorites for given user")
	@RequestMapping(path = "/{id}/favorites", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<ProviderResponseModel> getFavorites(
			@PathVariable("id") int userId) {
		List<FavoriteEntity> oe = this.favoriteRepository.findByUserId(userId);
		return oe.stream()
				.map(entity -> ProviderResponseModel
						.fromEntity(entity.getProvider()))
				.collect(Collectors.toList());
	}

	@ApiOperation(value = "register a user")
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserResponseModel> registerUser(
			@RequestBody UserRequestModel userModel) {
		if (this.userRepository
				.findByMobileNumber(userModel.getMobileNumber()) != null) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"User already registered");
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(
				inserOrUpdateUser(UserEntity.builder().build(), userModel));
	}

	@ApiOperation(value = "update the given user with provided model")
	@RequestMapping(path = "/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> updateUser(@PathVariable("id") int userId,
			@RequestBody UserRequestModel userModel) {
		inserOrUpdateUser(getUserEntity(userId), userModel);
		return ResponseEntity.ok("");
	}

	@ApiOperation(value = "delete user with given id")
	@RequestMapping(path = "/{id}", method = RequestMethod.DELETE, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> deleteUser(@PathVariable("id") int userId) {
		this.userRepository.deleteById(userId);
		return ResponseEntity.ok("");
	}

	private UserResponseModel inserOrUpdateUser(UserEntity userEntity,
			UserRequestModel userModel) {
		BeanUtils.copyProperties(userModel, userEntity);
		userEntity = this.userRepository.save(userEntity);
		return UserResponseModel.fromEntity(userEntity);
	}

	private UserEntity getUserEntity(int userId) {
		Optional<UserEntity> ueOpt = this.userRepository.findById(userId);
		if (ueOpt.isPresent()) {
			return ueOpt.get();
		}
		throw new ResponseStatusException(HttpStatus.NOT_FOUND);
	}
}
