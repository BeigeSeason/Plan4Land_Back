package com.SpringBoot.Plan4Land.CustomException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class PasswordNotMatchException extends ResponseStatusException {
    public PasswordNotMatchException(HttpStatus status, String reason) {
        super(status, reason);
    }

}
