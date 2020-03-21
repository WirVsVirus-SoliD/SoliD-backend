package de.wirvsvirus.hack.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.wirvsvirus.hack.backend.dao.repository.ProviderRepository;
import de.wirvsvirus.hack.rest.model.ProviderModel;

@Service
public class ProviderService {
	@Autowired
	private ProviderRepository providerRepository;

	public ProviderModel register(final ProviderModel model) {

		return model;
	}

	public void removeWallet(final ProviderModel model) {
		this.providerRepository.deleteById(model.getId());
	}
}
