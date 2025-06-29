package com.climbx.climbx.gym.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class GymNotFoundException extends RuntimeException {
    
    public GymNotFoundException(Long gymId) {
        super("Gym not found with id: " + gymId);
    }
} 