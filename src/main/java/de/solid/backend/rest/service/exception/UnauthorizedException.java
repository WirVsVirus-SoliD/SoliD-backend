package de.solid.backend.rest.service.exception;

public class UnauthorizedException extends SolidException {

  private static final long serialVersionUID = 1L;

  public UnauthorizedException(Class<?> clazz, String method, String message) {
    super(clazz, method, message);
  }
}
