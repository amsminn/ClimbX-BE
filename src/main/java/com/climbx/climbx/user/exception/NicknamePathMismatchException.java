package com.climbx.climbx.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class NicknamePathMismatchException extends RuntimeException {
    public NicknamePathMismatchException(String pathNickname, String userNickname) {
        super(String.format("Path nickname '%s' does not match your current nickname '%s'",
            pathNickname, userNickname));
    }
}