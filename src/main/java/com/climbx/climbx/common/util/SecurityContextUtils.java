package com.climbx.climbx.common.util;

import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityContextUtils {


    public static Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof Long) {
            return (Long) principal;
        }
        throw new IllegalStateException("principal is not of type Long.");
    }
}
