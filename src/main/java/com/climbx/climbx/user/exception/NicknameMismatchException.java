package com.climbx.climbx.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NicknameMismatchException extends RuntimeException {
    public NicknameMismatchException(String pathNickname, String bodyNickname) {
        super(String.format("User newNickname '%s' does not match request body newNickname '%s'",
            pathNickname, bodyNickname));
    }
}