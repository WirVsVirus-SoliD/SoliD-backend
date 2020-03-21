package de.mlbw.ethbalance.backend.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.mlbw.ethbalance.backend.dao.Provider;

public interface ProviderRepository extends JpaRepository<Provider, Integer> {

}
