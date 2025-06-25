package com.climbx.climbx.gym.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidLocationException extends RuntimeException {

    public InvalidLocationException(Double latitude, Double longitude) {
        super(String.format(
            "Invalid location: latitude '%s' and longitude '%s' must be between -90 to 90 and -180 to 180 respectively.",
            latitude, longitude));
    }
}
