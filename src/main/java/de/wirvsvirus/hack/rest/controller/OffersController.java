package de.wirvsvirus.hack.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.wirvsvirus.hack.backend.dao.OfferEntity;
import de.wirvsvirus.hack.backend.dao.repository.OffersRepository;
import de.wirvsvirus.hack.backend.dao.repository.ProvidersRepository;
import de.wirvsvirus.hack.backend.dao.repository.UsersRepository;
import de.wirvsvirus.hack.rest.model.OfferRequestModel;
import de.wirvsvirus.hack.rest.model.OfferResponseModel;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/offers")
public class OffersController {

	@Autowired
	private OffersRepository offerRepository;

	@Autowired
	private UsersRepository userRepository;

	@Autowired
	private ProvidersRepository providerRepository;

	@ApiOperation(value = "mark the given user applied for the passed provider")
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<OfferResponseModel> applyForOffer(
			@RequestBody OfferRequestModel model) {
		OfferEntity oe = this.offerRepository.findByUserAndProvider(
				model.getUserId(), model.getProviderId());
		if (oe == null) {
			oe = OfferEntity.builder().build();
			oe.setUser(this.userRepository.getOne(model.getUserId()));
			oe.setProvider(
					this.providerRepository.getOne(model.getProviderId()));
			oe = this.offerRepository.save(oe);
		}
		OfferResponseModel offerModel = OfferResponseModel.fromEntity(oe);

		return ResponseEntity.status(HttpStatus.CREATED).body(offerModel);
	}

	@ApiOperation(value = "mark the a given users offer as contacted")
	@RequestMapping(path = "/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> markContacted(
			@PathVariable("id") int offerId) {
		OfferEntity entity = this.offerRepository.getOne(offerId);
		entity.setContacted(true);
		entity = this.offerRepository.save(entity);
		return ResponseEntity.ok("");
	}
}
