package com.kapture.security.controller;

import com.kapture.security.dto.AuthenticationRequest;
import com.kapture.security.dto.AuthenticationResponse;
import com.kapture.security.dto.RegisterRequest;
import com.kapture.security.service.AuthenticationServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationServices authenticationServices;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {

        AuthenticationResponse register = authenticationServices.register(request);
        if (register != null && register.getToken() != null && !register.getToken().isEmpty()) {
            return ResponseEntity.ok("User Registered Success fully");
        }
        return ResponseEntity.badRequest().body("user not registered");
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        AuthenticationResponse authenticate = authenticationServices.authenticate(request);
        return ResponseEntity.ok(authenticate);
    }
}
