package com.climbx.climbx.common.util;

import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityContextUtils {


    public static Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
