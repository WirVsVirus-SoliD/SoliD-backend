package de.wirvsvirus.hack.rest.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRequestModel {

	private String firstName;

	private String lastName;

	private String mobileNumber;

	private String email;

	private EmploymentStatus EmploymentStatus;

	private boolean fullTime;

	private int pickupRange;

	private boolean driverLicense;

	private boolean pickupRequired;
}