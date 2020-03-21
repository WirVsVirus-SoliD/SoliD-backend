package de.wirvsvirus.hack.rest.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProviderRegisterModel {

	private String farmName;

	private List<Crops> crops;

	private String contactFirstName;

	private String contactLastName;

	private String email;

	private Address address;

	private String url;

	private String phone;

	private int minWorkPeriod;

	private float hourlyRate;

	private boolean pickupPossible;

	private int pickupRange;

	private boolean overnightPossible;
}
