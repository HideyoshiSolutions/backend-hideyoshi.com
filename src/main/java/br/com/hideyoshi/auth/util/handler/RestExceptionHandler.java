package br.com.hideyoshi.auth.util.handler;

import br.com.hideyoshi.auth.util.exception.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Log4j2
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<BadRequestExceptionDetails> handleBadRequest(final BadRequestException exception) {
        return new ResponseEntity<>(
                new BadRequestExceptionDetails("Bad Request Exception, Check the Documentation",
                        HttpStatus.BAD_REQUEST.value(), exception.getMessage(),
                        exception.getClass().getName(), LocalDateTime.now()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationInvalidException.class)
    public ResponseEntity<AuthenticationInvalidExceptionDetails> handleBadRequest(final AuthenticationInvalidException exception) {
        return new ResponseEntity<>(
                new AuthenticationInvalidExceptionDetails("Authentication Failed. Check your credentials.",
                        HttpStatus.FORBIDDEN.value(), exception.getMessage(),
                        exception.getClass().getName(), LocalDateTime.now()),
                HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<AuthorizationExceptionDetails> handleBadRequest(final AuthorizationException exception) {
        return new ResponseEntity<>(
                new AuthorizationExceptionDetails("Authorization Failed. Check your permissions.",
                        HttpStatus.FORBIDDEN.value(), exception.getMessage(),
                        exception.getClass().getName(), LocalDateTime.now()),
                HttpStatus.FORBIDDEN);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            final MethodArgumentNotValidException exception, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {

        final List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        final String fields = fieldErrors.stream()
                .map(FieldError::getField)
                .collect(Collectors.joining(", "));

        final String fieldsMessage = fieldErrors.stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        return new ResponseEntity<>(
                new ValidationExceptionDetails("Bad Request Exception, Invalid Fields",
                        HttpStatus.BAD_REQUEST.value(), "Check the field(s)",
                        exception.getClass().getName(), LocalDateTime.now(),
                        fields, fieldsMessage),
                HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(final Exception exception, @Nullable final Object body, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {

        String errorMessage;
        if (Objects.nonNull(exception.getCause())) {
            errorMessage = exception.getCause().getMessage();
        } else {
            errorMessage = exception.getMessage();
        }

        final ExceptionDetails exceptionDetails = new ExceptionDetails(
                errorMessage,
                status.value(),
                exception.getMessage(),
                exception.getClass().getName(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(exceptionDetails, headers, status);
    }
}