package de.solid.backend.rest.service.exception;

/*
 * is thrown, when the requested entity does not exist in the database
 * 
 */
public class NoSuchEntityException extends SolidException {

  private static final long serialVersionUID = 1L;

  public NoSuchEntityException(Class<?> clazz, String method, String message) {
    super(clazz, method, message);
  }
}
