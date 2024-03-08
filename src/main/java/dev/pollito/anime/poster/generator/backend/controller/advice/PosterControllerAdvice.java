package dev.pollito.anime.poster.generator.backend.controller.advice;

import dev.pollito.anime.poster.generator.backend.controller.PosterController;
import dev.pollito.anime.poster.generator.backend.exception.InvalidBase64ImageException;
import dev.pollito.anime.poster.generator.backend.models.Error;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static dev.pollito.anime.poster.generator.backend.util.ControllerAdviceUtil.getBadRequestError;

@Order(1)
@RestControllerAdvice(assignableTypes = PosterController.class)
public class PosterControllerAdvice {

    @ExceptionHandler(InvalidBase64ImageException.class)
    public ResponseEntity<Error> handle(InvalidBase64ImageException e){
        return getBadRequestError(e);
    }
}
