package de.wirvsvirus.hack.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.wirvsvirus.hack.backend.dao.OfferEntity;
import de.wirvsvirus.hack.backend.dao.repository.OfferRepository;
import de.wirvsvirus.hack.backend.dao.repository.ProviderRepository;
import de.wirvsvirus.hack.backend.dao.repository.UserRepository;
import de.wirvsvirus.hack.rest.model.OfferModel;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/offers")
public class OffersController {

	@Autowired
	private OfferRepository offerRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProviderRepository providerRepository;

	@ApiOperation(value = "mark the given user applied for the passed provider")
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public OfferModel applyForOffer(@RequestParam int providerId,
			@RequestParam int userId) {
		OfferEntity oe = this.offerRepository.findByUserAndProvider(userId,
				providerId);
		if (oe == null) {
			oe = OfferEntity.builder().build();
			oe.setUser(this.userRepository.getOne(userId));
			oe.setProvider(this.providerRepository.getOne(providerId));
			oe = this.offerRepository.save(oe);
		}
		OfferModel offerModel = OfferModel.fromEntity(oe);

		return offerModel;
	}

	@ApiOperation(value = "mark the a given users offer as contacted")
	@RequestMapping(path = "/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public OfferModel markContacted(@PathVariable("id") int offerId) {
		OfferEntity entity = this.offerRepository.getOne(offerId);
		entity.setContacted(true);
		entity = this.offerRepository.save(entity);
		return OfferModel.fromEntity(entity);
	}
}
