package de.wirvsvirus.hack.backend.dao;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import de.wirvsvirus.hack.rest.model.EmploymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "solid_users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity extends AbstractEntity {

	@Column
	private String firstName;

	@Column
	private String lastName;

	@Column
	private String mobileNumber;

	@Column
	private String email;

	@Column
	private EmploymentStatus EmploymentStatus;

	@Column
	private boolean fullTime;

	@Column
	private int pickupRange;

	@Column
	private boolean driverLicense;

	@Column
	private boolean pickupRequired;

	@Column
	@OneToMany(mappedBy = "user")
	private List<OfferEntity> offer;

}