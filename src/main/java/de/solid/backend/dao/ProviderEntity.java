package de.solid.backend.dao;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "solid_provider")
public class ProviderEntity extends AbstractEntity {

	private String farmName;

	@Lob
	private byte[] picture;

	private String pictureName;

	private String contactFirstName;

	private String contactLastName;

	private String email;

	private String crops;

	private String url;

	private String phone;

	private int minWorkPeriod;

	private float hourlyRate;

	private boolean pickupPossible;

	private int pickupRange;

	private boolean overnightPossible;

	private double latitude;

	private double longitude;

	@OneToOne
	private AddressEntity address;

	@OneToMany(mappedBy = "provider")
	private List<InquiryEntity> inquiries;
}