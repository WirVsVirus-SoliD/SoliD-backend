package de.solid.backend.rest.clients;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import de.solid.backend.rest.clients.model.KeycloakGetJWTResponseModel;

@Path("/")
@RegisterRestClient
public interface KeycloakRestClient {

  /**
   * retrieve jwt either with username / password -> grant_type: password
   * 
   * refresh_token -> grant_type: refresh_token
   * 
   * @param client_id
   * @param client_secret
   * @param username
   * @param password
   * @param grant_type
   * @param refresh_token
   * @return
   */
  @POST
  @Path("/protocol/openid-connect/token")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public KeycloakGetJWTResponseModel getJWTToken(@FormParam("client_id") String client_id,
      @FormParam("client_secret") String client_secret, @FormParam("username") String username,
      @FormParam("password") String password, @FormParam("refresh_token") String refresh_token,
      @FormParam("grant_type") String grant_type);

}
