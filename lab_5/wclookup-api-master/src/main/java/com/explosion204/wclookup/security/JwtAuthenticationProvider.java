package com.explosion204.wclookup.security;

import com.explosion204.wclookup.model.entity.User;
import com.explosion204.wclookup.model.repository.UserRepository;
import com.explosion204.wclookup.service.util.TokenUtil;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.explosion204.wclookup.security.ApplicationAuthority.ADMIN;
import static com.explosion204.wclookup.security.ApplicationAuthority.USER;

@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private static final String USER_ID_CLAIM = "user_id";

    private final TokenUtil tokenUtil;
    private final UserRepository userRepository;

    public JwtAuthenticationProvider(TokenUtil tokenUtil, UserRepository userRepository) {
        this.tokenUtil = tokenUtil;
        this.userRepository = userRepository;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtAuthentication jwtAuthentication = (JwtAuthentication) authentication;
        String accessToken = jwtAuthentication.getCredentials().toString();

        Map<String, Object> claims = tokenUtil.parseToken(accessToken);

        if (claims.containsKey(USER_ID_CLAIM)) {
            long userId = (Integer) claims.get(USER_ID_CLAIM);
            Optional<User> user = userRepository.findById(userId);

            if (user.isPresent()) {
                jwtAuthentication.setAuthorities(List.of(user.get().isAdmin() ? ADMIN : USER));
                jwtAuthentication.setPrincipal(user.get());
                jwtAuthentication.setAuthenticated(true);
            }
        }

        return jwtAuthentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(JwtAuthentication.class);
    }
}
