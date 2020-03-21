package de.wirvsvirus.hack.rest.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProviderModel {

	private Integer id;

	private String name;

	private String contactFirstName;

	private String contactLastName;

	private String email;
}
