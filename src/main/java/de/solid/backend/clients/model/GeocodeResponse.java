package de.solid.backend.clients.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/*
 * response model for geocode api call
 * 
 */
@Getter
@Setter
@ToString
public class GeocodeResponse {

  private double longt;

  private Object standard;

  private String matches;

  private Object alt;

  private Object error;

  private Object suggestion;

  private double latt;
}
