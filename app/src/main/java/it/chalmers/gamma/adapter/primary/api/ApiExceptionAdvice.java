package it.chalmers.gamma.adapter.primary.api;

import it.chalmers.gamma.app.authentication.AccessGuard;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(basePackages = "it.chalmers.gamma.adapter.primary.api")
public class ApiExceptionAdvice {

  record ApiErrorBody(int status, String error, String message) {}

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ApiErrorBody> handleResponseStatusException(ResponseStatusException ex) {
    return createResponse(ex.getStatusCode(), ex.getReason());
  }

  @ExceptionHandler(AccessGuard.AccessDeniedException.class)
  public ResponseEntity<ApiErrorBody> handleAccessDeniedException(
      AccessGuard.AccessDeniedException ex) {
    return createResponse(HttpStatus.FORBIDDEN, "FORBIDDEN");
  }

  private ResponseEntity<ApiErrorBody> createResponse(HttpStatusCode statusCode, String reason) {
    HttpStatus status = HttpStatus.resolve(statusCode.value());
    String error = status != null ? status.getReasonPhrase() : "Unknown";
    String message = reason != null ? reason : error;
    ApiErrorBody body = new ApiErrorBody(statusCode.value(), error, message);

    return ResponseEntity.status(statusCode).contentType(MediaType.APPLICATION_JSON).body(body);
  }
}
