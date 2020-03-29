package de.solid.backend.dao.repository;

import javax.enterprise.context.ApplicationScoped;

import de.solid.backend.dao.HelperEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class HelpersRepository implements PanacheRepository<HelperEntity> {

	public HelperEntity findByMobileNumber(String mobileNumber) {
		return find("mobileNumber", mobileNumber).firstResult();
	}

	public HelperEntity findByEmail(String email) {
		return find("email", email).firstResult();
	}
}
