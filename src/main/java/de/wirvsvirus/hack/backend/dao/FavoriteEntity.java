package de.wirvsvirus.hack.backend.dao;

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
@Table(name = "solid_favorites")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteEntity extends AbstractEntity {

	@ManyToOne
	private UserEntity user;

	@ManyToOne
	private ProviderEntity provider;
}