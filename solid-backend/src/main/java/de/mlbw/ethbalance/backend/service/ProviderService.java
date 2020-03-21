package de.mlbw.ethbalance.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.mlbw.ethbalance.api.model.ProviderModel;
import de.mlbw.ethbalance.backend.dao.repository.ProviderRepository;

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
