package de.solid.backend.rest.clients;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GeocodeResponse {

	private double longt;

	private Object standard;

	private String matches;

	private Object alt;

	private Object error;

	private Object suggestion;

	private double latt;
}
