package br.com.hideyoshi.auth.util.exception;

import java.time.LocalDateTime;

public class AuthorizationExceptionDetails extends ExceptionDetails {
    public AuthorizationExceptionDetails(String title, Integer status, String details, String developerMessage, LocalDateTime timestamp) {
        super(title, status, details, developerMessage, timestamp);
    }
}
