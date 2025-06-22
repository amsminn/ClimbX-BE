package com.climbx.climbx.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserStatNotFoundException extends RuntimeException {
    public UserStatNotFoundException(Long userId) {
        super("User stats not found for user: " + userId);
    }
}