package com.explosion204.wclookup.service.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class TokenUtil {
    private static final String JWT_SECRET_KEY_PROPERTY = "jwt.secret_key";
    private final Key secretKey;

    @Value("${jwt.validity_time}")
    private int jwtValidityTime; // in days

    @Value("${refresh.length}")
    private int refreshLength;

    public TokenUtil(Environment environment) {
        String rawKey = Objects.requireNonNull(environment.getProperty(JWT_SECRET_KEY_PROPERTY));
        secretKey = Keys.hmacShaKeyFor(rawKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateJwt(Map<String, Object> claims) {
        Instant expirationInstant = LocalDateTime.now(Clock.systemUTC())
                .plus(jwtValidityTime, ChronoUnit.DAYS)
                .toInstant(ZoneOffset.UTC);
        Date expirationTime = Date.from(expirationInstant);

        JwtBuilder builder = Jwts.builder()
                .setExpiration(expirationTime)
                .signWith(secretKey);
        claims.forEach(builder::claim);

        return builder.compact();
    }

    public Map<String, Object> parseToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return new HashMap<>(claims);
        } catch (JwtException e) {
            return Collections.emptyMap();
        }
    }

    public String generateRefreshToken() {
        return RandomStringUtils.random(refreshLength, true, true);
    }
}
