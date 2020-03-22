package de.wirvsvirus.hack.backend.dao;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "solid_providers")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderEntity extends AbstractEntity {

	@Column
	private String farmName;

	@Column
	@Lob
	private byte[] picture;

	@Column
	private String pictureName;

	@Column
	private String pictureContentType;

	@Column
	private String contactFirstName;

	@Column
	private String contactLastName;

	@Column
	private String email;

	@Column
	private String crops;

	@Column
	private String addressStreet;

	@Column
	private String addressHouseNo;

	@Column
	private String addressZip;

	@Column
	private String addressCity;

	@Column
	private String url;

	@Column
	private String phone;

	@Column
	private int minWorkPeriod;

	@Column
	private float hourlyRate;

	@Column
	private boolean pickupPossible;

	@Column
	private int pickupRange;

	@Column
	private boolean overnightPossible;

	@Column
	private double latitude;

	@Column
	private double longitude;

	@Column
	@OneToMany(mappedBy = "provider")
	private List<InquireEntity> offer;
}