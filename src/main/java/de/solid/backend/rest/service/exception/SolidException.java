package de.solid.backend.rest.service.exception;

import java.util.UUID;
import lombok.Getter;

/*
 * base exception class, provides arguments to determine the exact cause and persists an uuid, which
 * is used to find the corresponding stacktrace from an api call
 * 
 */
@Getter
public class SolidException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private String exceptionUUID;

  private Class<?> exceptionClass;

  private String method;

  public SolidException() {
    this.exceptionUUID = UUID.randomUUID().toString();
  }

  public SolidException(Class<?> clazz, String method, String message) {
    this(clazz, method, message, null);
  }

  public SolidException(Class<?> clazz, String method, String message, Throwable e) {
    super(message, e);
    this.exceptionClass = clazz;
    this.method = method;
    this.exceptionUUID = UUID.randomUUID().toString();
  }

  public String getExceptionClass() {
    return this.exceptionClass.getName();
  }
}
