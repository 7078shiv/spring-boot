package com.kapture.security.service;

import com.kapture.security.config.JwtService;
import com.kapture.security.dto.AuthenticationRequest;
import com.kapture.security.dto.AuthenticationResponse;
import com.kapture.security.dto.RegisterRequest;
import com.kapture.security.repository.UserRepository;
import com.kapture.security.user.Role;
import com.kapture.security.user.User;
import lombok.RequiredArgsConstructor;
import lombok.var;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("userDetailService")
@RequiredArgsConstructor
public class AuthenticationServices {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final Logger logger = LoggerFactory.getLogger(AuthenticationServices.class);

    public AuthenticationResponse register(RegisterRequest request){
        var user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .emp_id(request.getEmp_id())
                .client_id(request.getClient_id())
                .role(Role.USER)
                .enable(1)
                .build();
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("user not found register first"));
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(int id) {
        User user = userRepository.findById(id);
        return user;
    }

    public User updateUserById(int id, User existingUser) {
        try {
            User user = getUserById(id);
                // Update fields as needed
                user.setClient_id(existingUser.getClient_id());
                user.setEmp_id(existingUser.getEmp_id());
                user.setUsername(existingUser.getUsername());

                // Hash the updated password
                String hashedPassword = passwordEncoder.encode(user.getPassword());
                user.setPassword(hashedPassword);

                user.setLastLoginTime(existingUser.getLastLoginTime());
                user.setLastPasswordReset(existingUser.getLastPasswordReset());
                user.setEnable(existingUser.getEnable());

                return userRepository.save(existingUser);

        } catch (Exception e) {
            logger.error("Error while updating login credential by ID: " + id, e);
            throw e;
        }
    }
}
