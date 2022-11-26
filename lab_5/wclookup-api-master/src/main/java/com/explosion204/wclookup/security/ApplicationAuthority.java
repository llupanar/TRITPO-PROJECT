package com.explosion204.wclookup.security;

import org.springframework.security.core.GrantedAuthority;

public enum ApplicationAuthority implements GrantedAuthority {
    ADMIN("ROLE_ADMIN"), USER("ROLE_USER");

    private final String authority;

    ApplicationAuthority(String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return authority;
    }
}
