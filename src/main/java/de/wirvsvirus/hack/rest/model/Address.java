package de.wirvsvirus.hack.rest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Address {
	private String street;
	private String housenr;
	private String zip;
	private String city;

}
