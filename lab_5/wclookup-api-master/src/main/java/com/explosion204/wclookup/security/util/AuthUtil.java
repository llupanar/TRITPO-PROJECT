package com.explosion204.wclookup.security.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {
    public boolean hasAuthority(String authority) {
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();

        return authentication != null && authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals(authority));
    }

    public long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();

        return Long.parseLong(authentication.getName());
    }
}
