package de.wirvsvirus.hack.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.wirvsvirus.hack.backend.dao.FavoriteEntity;
import de.wirvsvirus.hack.backend.dao.repository.FavoritesRepository;
import de.wirvsvirus.hack.backend.dao.repository.ProvidersRepository;
import de.wirvsvirus.hack.backend.dao.repository.UsersRepository;
import de.wirvsvirus.hack.rest.model.FavoriteRequestModel;
import de.wirvsvirus.hack.rest.model.FavoriteResponseModel;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/favorites")
public class FavoriteController {

	@Autowired
	private FavoritesRepository favoriteRepository;

	@Autowired
	private UsersRepository userRepository;

	@Autowired
	private ProvidersRepository providerRepository;

	@ApiOperation(value = "saves the given provider as favorite for the given user")
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<FavoriteResponseModel> markFavorite(
			@RequestBody FavoriteRequestModel favoriteRequestModel) {
		FavoriteEntity fe = this.favoriteRepository.findByUserAndProvider(
				favoriteRequestModel.getUserId(),
				favoriteRequestModel.getProviderId());
		if (fe == null) {
			fe = FavoriteEntity.builder().build();
			fe.setUser(this.userRepository
					.getOne(favoriteRequestModel.getUserId()));
			fe.setProvider(this.providerRepository
					.getOne(favoriteRequestModel.getProviderId()));
			fe = this.favoriteRepository.save(fe);
		}
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(FavoriteResponseModel.fromEntity(fe));
	}

	@ApiOperation(value = "remove a favorite with given favoriteId")
	@RequestMapping(method = RequestMethod.DELETE, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> deleteFavorite(@RequestParam int favoriteId) {
		this.favoriteRepository.deleteById(favoriteId);
		return ResponseEntity.ok("");
	}
}
