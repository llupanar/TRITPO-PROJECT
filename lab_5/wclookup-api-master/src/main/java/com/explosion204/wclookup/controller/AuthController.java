package com.explosion204.wclookup.controller;

import com.explosion204.wclookup.service.AuthService;
import com.explosion204.wclookup.service.dto.AuthDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final String GOOGLE_ACCESS_TOKEN_HEADER = "Google-Access-Token";
    private static final String REFRESH_TOKEN_HEADER = "Refresh-Token";
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthDto> authenticate(@RequestHeader(GOOGLE_ACCESS_TOKEN_HEADER) String googleAccessToken) {
        AuthDto authDto = authService.authenticate(googleAccessToken);
        return new ResponseEntity<>(authDto, OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthDto> refresh(@RequestHeader(REFRESH_TOKEN_HEADER) String refreshToken) {
        AuthDto authDto = authService.refresh(refreshToken);
        return new ResponseEntity<>(authDto, OK);
    }
}
