package de.solid.backend.rest;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import org.apache.http.HttpStatus;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;

public abstract class BaseController {

  public Response HTTP_OK() {
    return Response.ok().build();
  }

  public Response HTTP_OK(Object result) {
    return Response.ok().entity(result).build();
  }

  public Response HTTP_CREATED() {
    return Response.status(HttpStatus.SC_CREATED).build();
  }

  public Response HTTP_CONFLICT(String errorMessage) {
    return Response.status(HttpStatus.SC_CONFLICT).entity("{\"error\":\"" + errorMessage + "\"}")
        .build();
  }

  public Response HTTP_NOT_FOUND() {
    return Response.status(HttpStatus.SC_NOT_FOUND).build();
  }

  public Response HTTP_INTERNAL() {
    return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
  }

  @Inject
  protected JsonWebToken jwt;

  public String getAuthenticatedUserEmail() {
    return jwt.claim(Claims.email.toString())
        .orElseThrow(() -> new RuntimeException("cannot obtain jwt")).toString();
  }
}
