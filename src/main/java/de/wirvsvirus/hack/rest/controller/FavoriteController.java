package de.wirvsvirus.hack.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.wirvsvirus.hack.backend.dao.FavoriteEntity;
import de.wirvsvirus.hack.backend.dao.repository.FavoriteRepository;
import de.wirvsvirus.hack.backend.dao.repository.ProviderRepository;
import de.wirvsvirus.hack.backend.dao.repository.UserRepository;
import de.wirvsvirus.hack.rest.model.FavoriteModel;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/favorites")
public class FavoriteController {

	@Autowired
	private FavoriteRepository favoriteRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProviderRepository providerRepository;

	@ApiOperation(value = "saves the given provider as favorite for the given user")
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public FavoriteModel markFavorite(@RequestParam int providerId,
			@RequestParam int userId) {
		FavoriteEntity fe = this.favoriteRepository
				.findByUserAndProvider(userId, providerId);
		if (fe == null) {
			fe = FavoriteEntity.builder().build();
			fe.setUser(this.userRepository.getOne(userId));
			fe.setProvider(this.providerRepository.getOne(providerId));
			fe = this.favoriteRepository.save(fe);
		}
		FavoriteModel favoriteModel = FavoriteModel.fromEntity(fe);

		return favoriteModel;
	}

	@ApiOperation(value = "remove a favorite with given favoriteId")
	@RequestMapping(method = RequestMethod.DELETE, produces = MediaType.TEXT_PLAIN_VALUE)
	public String deleteFavorite(@RequestParam int favoriteId) {
		this.favoriteRepository.deleteById(favoriteId);
		return "deleted";
	}
}
