package de.solid.backend.clients;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import de.solid.backend.clients.model.GeocodeResponse;

/*
 * REST client for communication with Geocode API to retrieve latitude and longitude for an address
 * 
 */
@Path("/")
@RegisterRestClient
public interface GeocodeRestClient {

  @GET
  @Path("/{address}")
  @Produces(MediaType.APPLICATION_JSON)
  public GeocodeResponse getLatLong(@PathParam("address") String address);

}
