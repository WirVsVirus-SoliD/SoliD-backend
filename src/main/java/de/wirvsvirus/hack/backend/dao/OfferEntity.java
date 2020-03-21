package de.wirvsvirus.hack.backend.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "solid_offers")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfferEntity extends AbstractEntity {

	@ManyToOne
	private UserEntity user;

	@ManyToOne
	private ProviderEntity provider;

	@Column
	private boolean contacted;
}