package de.solid.backend.rest.service.exception;

/*
 * is thrown, when exceptions communicating with rest clients (keycloak, geocode) occur
 * 
 */
public class RestClientException extends SolidException {

  private static final long serialVersionUID = 1L;

  public RestClientException(Class<?> clazz, String method, String message) {
    super(clazz, method, message);
  }
}
