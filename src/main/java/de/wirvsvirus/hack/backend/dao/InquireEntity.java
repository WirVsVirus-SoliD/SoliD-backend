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
@Table(name = "solid_inquires")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquireEntity extends AbstractEntity {

	@ManyToOne
	private HelperEntity helper;

	@ManyToOne
	private ProviderEntity provider;

	@Column
	private boolean contacted;
}