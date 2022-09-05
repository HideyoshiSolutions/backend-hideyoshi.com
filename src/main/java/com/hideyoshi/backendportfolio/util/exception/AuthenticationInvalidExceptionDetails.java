package com.hideyoshi.backendportfolio.util.exception;

import java.time.LocalDateTime;

public class AuthenticationInvalidExceptionDetails extends ExceptionDetails{

    public AuthenticationInvalidExceptionDetails(
            String title,
            Integer status,
            String details,
            String developerMessage,
            LocalDateTime timestamp) {
        super(title, status, details, developerMessage, timestamp);
    }
}
