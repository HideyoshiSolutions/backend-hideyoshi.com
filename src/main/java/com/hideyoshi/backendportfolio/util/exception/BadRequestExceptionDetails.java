package com.hideyoshi.backendportfolio.util.exception;

import java.time.LocalDateTime;

public class BadRequestExceptionDetails extends ExceptionDetails {

    public BadRequestExceptionDetails(final String title, final Integer status,
                                      final String details, final String developerMessage,
                                      final LocalDateTime timestamp) {
        super(title, status, details, developerMessage, timestamp);
    }

}