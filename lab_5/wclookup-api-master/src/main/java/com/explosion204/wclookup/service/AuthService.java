package com.explosion204.wclookup.service;

import com.explosion204.wclookup.model.entity.User;
import com.explosion204.wclookup.model.repository.UserRepository;
import com.explosion204.wclookup.service.dto.AuthDto;
import com.explosion204.wclookup.service.util.TokenUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.time.ZoneOffset.UTC;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private static final Pattern emailPattern = Pattern.compile("(.+)@.+");
    private static final String USER_ID_CLAIM = "user_id";

    @Value("${refresh.validity_time}")
    private int refreshValidityTime;

    private final UserRepository userRepository;
    private final TokenUtil tokenUtil;

    public AuthService(UserRepository userRepository, TokenUtil tokenUtil) {
        this.userRepository = userRepository;
        this.tokenUtil = tokenUtil;
    }

    public AuthDto authenticate(String googleAccessToken) {
        GoogleIdToken googleIdToken = parseGoogleAccessToken(googleAccessToken)
            .orElseThrow(() -> new BadCredentialsException(StringUtils.EMPTY));
        GoogleIdToken.Payload payload = googleIdToken.getPayload();
        String googleId = payload.getSubject();
        String email = payload.getEmail();
        User user = userRepository.findByGoogleId(googleId)
                .orElseGet(() -> createUser(googleId, email));

        return buildAuthDto(user);
    }

    public AuthDto refresh(String refreshToken) {
        User user = userRepository.findByRefreshToken(DigestUtils.sha256Hex(refreshToken))
                .orElseThrow(() -> new BadCredentialsException(StringUtils.EMPTY));
        LocalDateTime expirationTime = user.getRefreshTokenExpiration();

        if (expirationTime.isAfter(expirationTime.plusDays(refreshValidityTime))) {
            throw new CredentialsExpiredException(StringUtils.EMPTY);
        }

        return buildAuthDto(user);
    }

    private AuthDto buildAuthDto(User user) {
        String accessToken = tokenUtil.generateJwt(
                Map.of(USER_ID_CLAIM, user.getId())
        );

        String refreshToken = tokenUtil.generateRefreshToken();
        LocalDateTime expirationTime = LocalDateTime.now(UTC).plus(refreshValidityTime, ChronoUnit.DAYS);
        user.setRefreshToken(DigestUtils.sha256Hex(refreshToken));
        user.setRefreshTokenExpiration(expirationTime);
        userRepository.save(user);

        AuthDto authDto = new AuthDto();
        authDto.setAccessToken(accessToken);
        authDto.setRefreshToken(refreshToken);

        return authDto;
    }

    private Optional<GoogleIdToken> parseGoogleAccessToken(String accessToken) {
        HttpTransport httpTransport = new NetHttpTransport();
        JsonFactory jsonFactory = new GsonFactory();
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(httpTransport, jsonFactory)
                .build();
        GoogleIdToken idToken = null;

        try {
            idToken = verifier.verify(accessToken);
        } catch (GeneralSecurityException | IOException | IllegalArgumentException e) {
            logger.error("Unable to parse idToken", e);
        }

        return Optional.ofNullable(idToken);
    }

    private User createUser(String googleId, String email) {
        User newUser = new User();

        newUser.setGoogleId(googleId);
        newUser.setNickname(createNickname(email));
        newUser.setAdmin(false);

        return userRepository.save(newUser);
    }

    private String createNickname(String email) {
        Matcher matcher = emailPattern.matcher(email);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return StringUtils.EMPTY;
    }
}
