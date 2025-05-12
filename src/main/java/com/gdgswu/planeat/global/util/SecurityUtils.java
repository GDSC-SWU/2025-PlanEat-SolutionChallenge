package com.gdgswu.planeat.global.util;

import com.gdgswu.planeat.global.exception.CustomException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import static com.gdgswu.planeat.global.exception.ErrorCode.USER_NOT_FOUND;

public class SecurityUtils {
    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }
        throw new CustomException(USER_NOT_FOUND);
    }
}
