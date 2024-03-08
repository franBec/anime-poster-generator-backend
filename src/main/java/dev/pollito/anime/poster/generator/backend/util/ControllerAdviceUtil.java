package dev.pollito.anime.poster.generator.backend.util;

import static dev.pollito.anime.poster.generator.backend.util.Constants.SLF4J_MDC_SESSION_ID_KEY;

import dev.pollito.anime.poster.generator.backend.exception.InvalidBase64ImageException;
import dev.pollito.anime.poster.generator.backend.models.Error;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

public class ControllerAdviceUtil {

  private ControllerAdviceUtil() {}

  private static ResponseEntity<Error> buildErrorResponse(
      HttpStatus status, Exception e, String errorMessage) {
    return ResponseEntity.status(status)
        .body(
            new Error()
                .error(e.getClass().getSimpleName())
                .message(errorMessage)
                .path(getCurrentRequestPath())
                .timestamp(OffsetDateTime.now())
                .session(UUID.fromString(MDC.get(SLF4J_MDC_SESSION_ID_KEY))));
  }

  private static String getCurrentRequestPath() {
    ServletRequestAttributes attr =
        (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
    return attr.getRequest().getRequestURI();
  }

  public static ResponseEntity<Error> getGenericError(Exception e) {
    return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e, "An unexpected error occurred.");
  }

  public static ResponseEntity<Error> getBadRequestError(
      MissingServletRequestParameterException e) {
    return buildErrorResponse(HttpStatus.BAD_REQUEST, e, e.getBody().getDetail());
  }

  public static ResponseEntity<Error> getBadRequestError(ConstraintViolationException e) {
    return buildErrorResponse(
        HttpStatus.BAD_REQUEST, e, constraintViolationExceptionMessageFormatter(e));
  }

  private static String constraintViolationExceptionMessageFormatter(
      ConstraintViolationException e) {
    String formattedMessage = "";

    if (!e.getConstraintViolations().isEmpty()) {
      ConstraintViolation<?> violation = e.getConstraintViolations().iterator().next();
      String propertyPath = violation.getPropertyPath().toString();
      String fieldName = propertyPath.substring(propertyPath.lastIndexOf('.') + 1);
      String message = violation.getMessage();
      formattedMessage = fieldName + ": " + message;
    }

    return formattedMessage;
  }

  public static ResponseEntity<Error> getBadRequestError(MethodArgumentTypeMismatchException e) {
    return buildErrorResponse(HttpStatus.BAD_REQUEST, e, e.getCause().getCause().getMessage());
  }

  public static ResponseEntity<Error> getBadRequestError(MethodArgumentNotValidException e) {
    return buildErrorResponse(
        HttpStatus.BAD_REQUEST, e, methodArgumentNotValidExceptionMessageFormatter(e));
  }

  public static ResponseEntity<Error> getBadRequestError(InvalidBase64ImageException e) {
    return buildErrorResponse(HttpStatus.BAD_REQUEST, e, "image provided is not a valid Base64Image");
  }

  @NotNull
  private static String methodArgumentNotValidExceptionMessageFormatter(
      MethodArgumentNotValidException e) {
    return Objects.requireNonNull(e.getDetailMessageArguments())[1]
        .toString()
        .replace("[", "")
        .replace("]", "");
  }
}
