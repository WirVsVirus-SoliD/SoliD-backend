package de.solid.backend.rest.service.exception;

/*
 * is thrown, when an entity already exists
 */
public class DuplicateException extends SolidException {

  private static final long serialVersionUID = 1L;

  public DuplicateException(Class<?> clazz, String method, String message) {
    super(clazz, method, message);
  }
}
