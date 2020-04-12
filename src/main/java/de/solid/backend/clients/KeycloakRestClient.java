package de.solid.backend.clients;

import java.util.List;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import de.solid.backend.clients.model.KeycloakGetJWTResponseModel;
import de.solid.backend.clients.model.KeycloakUserRequestModel;

/*
 * REST client for communication with keycloak server
 * 
 */
@Path("/")
@RegisterRestClient
public interface KeycloakRestClient {

  @POST
  @Path("/realms/solid/protocol/openid-connect/token")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public KeycloakGetJWTResponseModel getJWTToken(@FormParam("client_id") String client_id,
      @FormParam("client_secret") String client_secret, @FormParam("username") String username,
      @FormParam("password") String password, @FormParam("refresh_token") String refresh_token,
      @FormParam("grant_type") String grant_type);

  @POST
  @Path("/admin/realms/solid/users")
  @Consumes(MediaType.APPLICATION_JSON)
  @ClientHeaderParam(name = "Content-Type", value = MediaType.APPLICATION_JSON)
  public Response createUser(@HeaderParam("Authorization") String authorization,
      @RequestBody KeycloakUserRequestModel model);

  @PUT
  @Path("/admin/realms/solid/users/{keycloakuserid}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response updateUser(@HeaderParam("Authorization") String authorization,
      @PathParam("keycloakuserid") String keycloakUserId,
      @RequestBody KeycloakUserRequestModel attributes);

  @DELETE
  @Path("/admin/realms/solid/users/{keycloakuserid}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response deleteUser(@HeaderParam("Authorization") String authorization,
      @PathParam("keycloakuserid") String keycloakUserId);

  @GET
  @Path("/admin/realms/solid/users")
  @Produces(MediaType.APPLICATION_JSON)
  public List<Map<String, Object>> getUserByEmail(
      @HeaderParam("Authorization") String authorization, @QueryParam("email") String email);

  @GET
  @Path("/admin/realms/solid/{keycloakuserid}")
  @Produces(MediaType.APPLICATION_JSON)
  public List<Map<String, Object>> getUser(@HeaderParam("Authorization") String authorization,
      @PathParam("keycloakuserid") String keycloakUserId);

  @POST
  @Path("/realms/solid/protocol/openid-connect/token/introspect")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.APPLICATION_JSON)
  public Map<String, Object> validateToken(@FormParam("client_id") String client_id,
      @FormParam("client_secret") String client_secret, @FormParam("token") String token);

}
