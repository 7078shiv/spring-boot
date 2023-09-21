package com.kapture.security.controller;

import com.kapture.security.dto.AuthenticationRequestDto;
import com.kapture.security.dto.RegisterRequestDto;
import com.kapture.security.service.AuthenticationServices;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
    private AuthenticationServices authenticationServices;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDto request){
        ResponseEntity<?> register = authenticationServices.register(request);
        return register;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequestDto request){
        ResponseEntity<?> authentication = authenticationServices.authenticate(request);
        return authentication;
    }

    @PostMapping("/add-user-to-kafka-server")
    public ResponseEntity<?> addUserToKafkaServer(@RequestBody RegisterRequestDto registerRequestDto){
        ResponseEntity<?> response = authenticationServices.addUserToKafkaServer(registerRequestDto);
        return response;
    }
}
