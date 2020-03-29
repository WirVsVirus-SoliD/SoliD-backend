package de.solid.backend.dao.repository;

import javax.enterprise.context.ApplicationScoped;

import de.solid.backend.dao.AddressEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class AddressRepository implements PanacheRepository<AddressEntity> {

}
