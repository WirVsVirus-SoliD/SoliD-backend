package de.wirvsvirus.hack.rest.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FavoriteRequestModel {

	private int helperId;

	private int providerId;
}