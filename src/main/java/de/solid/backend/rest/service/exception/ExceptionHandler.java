package de.solid.backend.rest.service.exception;

import java.util.Map;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * global exception handler, depending on the catched exception the corresponding HTTP status code
 * is mapped and returned in case of known exceptions a uuid is forwared within the response to
 * correlate the api call with the stacktrace
 *
 */
@Provider
public class ExceptionHandler implements ExceptionMapper<Throwable> {

  private static final Logger _log = LoggerFactory.getLogger(ExceptionHandler.class);

  @Override
  public Response toResponse(Throwable exception) {
    int status = HttpStatus.SC_INTERNAL_SERVER_ERROR;
    String exceptionUUID = "";

    if (exception instanceof SolidException) {
      SolidException solidException = (SolidException) exception;
      exceptionUUID = solidException.getExceptionUUID();
      _log.error("[{}] - {}.{}: {}", solidException.getExceptionUUID(),
          solidException.getExceptionClass(), solidException.getMethod(),
          solidException.getLocalizedMessage(), solidException);
      status = exceptionToErrorCode.get(solidException.getClass());
    } else {
      _log.error("Unexpected error occured: ", exception);
    }
    return Response.status(status).entity(exceptionUUID).build();
  }

  private Map<Class<?>, Integer> exceptionToErrorCode = Map.of(NoSuchEntityException.class,
      HttpStatus.SC_NOT_FOUND, TimeoutException.class, HttpStatus.SC_REQUEST_TIMEOUT,
      DuplicateException.class, HttpStatus.SC_CONFLICT, RequiredArgumentException.class,
      HttpStatus.SC_BAD_REQUEST, UnauthorizedException.class, HttpStatus.SC_FORBIDDEN);
}
