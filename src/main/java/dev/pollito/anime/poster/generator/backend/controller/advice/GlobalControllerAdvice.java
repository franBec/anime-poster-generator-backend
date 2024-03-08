package dev.pollito.anime.poster.generator.backend.controller.advice;

import static dev.pollito.anime.poster.generator.backend.util.ControllerAdviceUtil.getBadRequestError;
import static dev.pollito.anime.poster.generator.backend.util.ControllerAdviceUtil.getGenericError;

import dev.pollito.anime.poster.generator.backend.models.Error;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Order()
@RestControllerAdvice
@Slf4j
public class GlobalControllerAdvice {
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<Error> handle(MissingServletRequestParameterException e) {
    return getBadRequestError(e);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Error> handle(ConstraintViolationException e) {
    return getBadRequestError(e);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<Error> handle(MethodArgumentTypeMismatchException e) {
    return getBadRequestError(e);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Error> handle(MethodArgumentNotValidException e) {
    return getBadRequestError(e);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Error> handle(Exception e) {
    log.error(
        "A generic error is about to be returned. This may be caused by an unhandled exception", e);
    return getGenericError(e);
  }
}
