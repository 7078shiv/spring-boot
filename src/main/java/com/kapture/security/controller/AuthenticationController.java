package com.kapture.security.controller;

import com.kapture.security.dto.AuthenticationRequest;
import com.kapture.security.dto.AuthenticationResponse;
import com.kapture.security.dto.RegisterRequest;
import com.kapture.security.service.AuthenticationServices;
import com.kapture.security.util.ResponseHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    @Autowired
    private  AuthenticationServices authenticationServices;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        ResponseEntity<?> register = authenticationServices.register(request);
        return register;
    }


    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest request) {
        ResponseEntity<?> authentication = authenticationServices.authenticate(request);
        return authentication;
    }

}
