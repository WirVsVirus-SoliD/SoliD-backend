package de.solid.backend.rest.service.exception;

/*
 * is thrown, when a required argument is not provided
 * 
 */
public class RequiredArgumentException extends SolidException {

  private static final long serialVersionUID = 1L;

  public RequiredArgumentException(Class<?> clazz, String method, String message) {
    super(clazz, method, message);
  }
}
