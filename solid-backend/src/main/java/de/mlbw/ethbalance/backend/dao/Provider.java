package de.mlbw.ethbalance.backend.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "provider")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Provider extends AbstractEntity {

	@Column
	private String name;

	@Column
	private String contactFirstName;

	@Column
	private String contactLastName;

	@Column
	private String email;
}