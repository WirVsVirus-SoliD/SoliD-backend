package de.wirvsvirus.hack.rest.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import de.wirvsvirus.hack.backend.dao.repository.FavoriteRepository;
import de.wirvsvirus.hack.backend.dao.repository.OfferRepository;
import de.wirvsvirus.hack.backend.dao.repository.UserRepository;
import de.wirvsvirus.hack.rest.model.ProviderModel;
import de.wirvsvirus.hack.rest.model.UserModel;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/users")
public class UsersController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private OfferRepository offerRepository;

	@Autowired
	private FavoriteRepository favoriteRepository;

	@ApiOperation(value = "get user with given id")
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public UserModel getUser(@RequestParam int userId) {
		UserModel userModel = UserModel.builder().build();
		BeanUtils.copyProperties(getUserEntity(userId), userModel);
		return userModel;
	}

	@ApiOperation(value = "get all offers for given user")
	@RequestMapping(path = "/{id}/offers", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<ProviderModel> getOffers(@PathVariable("id") int userId) {
		List<OfferEntity> oe = this.offerRepository.findByUserId(userId);
		return oe.stream()
				.map(entity -> ProviderModel.fromEntity(entity.getProvider()))
				.collect(Collectors.toList());
	}

	@ApiOperation(value = "get all favorites for given user")
	@RequestMapping(path = "/{id}/favorites", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<ProviderModel> getFavorites(@PathVariable("id") int userId) {
		List<FavoriteEntity> oe = this.favoriteRepository.findByUserId(userId);
		return oe.stream()
				.map(entity -> ProviderModel.fromEntity(entity.getProvider()))
				.collect(Collectors.toList());
	}

	@ApiOperation(value = "register a user")
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public UserModel registerUser(@RequestBody UserModel userModel) {
		if (this.userRepository
				.findByMobileNumber(userModel.getMobileNumber()) != null) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"User already registered");
		}
		return inserOrUpdateUser(UserEntity.builder().build(), userModel);
	}

	@ApiOperation(value = "update the given user with provided model")
	@RequestMapping(path = "/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public UserModel updateUser(@PathVariable("id") int userId,
			@RequestBody UserModel userModel) {
		return inserOrUpdateUser(getUserEntity(userId), userModel);
	}

	@ApiOperation(value = "delete user with given id")
	@RequestMapping(path = "/{id}", method = RequestMethod.DELETE, produces = MediaType.TEXT_PLAIN_VALUE)
	public String deleteUser(@PathVariable("id") int userId) {
		this.userRepository.deleteById(userId);
		return "deleted";
	}

	private UserModel inserOrUpdateUser(UserEntity userEntity,
			UserModel userModel) {
		BeanUtils.copyProperties(userModel, userEntity);
		userEntity = this.userRepository.save(userEntity);
		userModel = UserModel.fromEntity(userEntity);
		return userModel;
	}

	private UserEntity getUserEntity(int userId) {
		Optional<UserEntity> ueOpt = this.userRepository.findById(userId);
		if (ueOpt.isPresent()) {
			return ueOpt.get();
		}
		throw new ResponseStatusException(HttpStatus.NOT_FOUND);
	}
}
