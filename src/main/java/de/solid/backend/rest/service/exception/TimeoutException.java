package de.solid.backend.rest.service.exception;

/*
 * is thrown, when a timeout occurs or an time related entity operation exceeds its limit
 * 
 */
public class TimeoutException extends SolidException {

  private static final long serialVersionUID = 1L;

  public TimeoutException(Class<?> clazz, String method, String message) {
    super(clazz, method, message);
  }
}
