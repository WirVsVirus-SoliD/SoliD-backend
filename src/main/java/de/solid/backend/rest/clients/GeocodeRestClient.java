package de.solid.backend.rest.clients;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import de.solid.backend.rest.clients.model.GeocodeResponse;

@Path("/")
@RegisterRestClient
public interface GeocodeRestClient {

	@GET
	@Path("/{address}")
	@Produces(MediaType.APPLICATION_JSON)
	public GeocodeResponse getLatLong(@PathParam String address);

}
