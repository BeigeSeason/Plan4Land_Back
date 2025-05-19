package com.SpringBoot.Plan4Land.CustomException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(HttpStatus.GONE)
public class SignOutException extends ResponseStatusException {
    public SignOutException(HttpStatus status, String reason) {
        super(status, reason);
    }
}
