package br.com.hideyoshi.auth.util.exception;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ValidationExceptionDetails extends ExceptionDetails {

    private final String fields;

    private final String fieldsMessage;

    public ValidationExceptionDetails(final String title, final int status,
                                      final String details, final String developerMessage,
                                      final LocalDateTime timestamp, final String fields,
                                      final String fieldsMessage) {
        super(title, status, details, developerMessage, timestamp);
        this.fields = fields;
        this.fieldsMessage = fieldsMessage;
    }

}