package com.hideyoshi.auth.util.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class AuthenticationInvalidException extends RuntimeException {

    public AuthenticationInvalidException(String message) {
        super(message);
    }

}
