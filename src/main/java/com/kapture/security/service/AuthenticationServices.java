package com.kapture.security.service;

import com.kapture.security.config.JwtService;
import com.kapture.security.dto.AuthenticationRequest;
import com.kapture.security.dto.AuthenticationResponse;
import com.kapture.security.dto.RegisterRequest;
import com.kapture.security.repository.UserRepository;
import com.kapture.security.user.AbstractUser;
import com.kapture.security.user.Role;
import com.kapture.security.user.User;
import com.kapture.security.util.ResponseHandler;
import lombok.RequiredArgsConstructor;
import lombok.var;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("userDetailService")
@RequiredArgsConstructor
public class AuthenticationServices {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final Logger logger = LoggerFactory.getLogger(AuthenticationServices.class);

    public ResponseEntity<?> register(RegisterRequest request) {
        try {
            var user = User.builder()
                    .username(request.getUsername())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .emp_id(request.getEmp_id())
                    .client_id(request.getClient_id())
                    .role(Role.USER)
                    .enable(1)
                    .build();
            userRepository.save(user);
            AuthenticationResponse response = getResponse(user);
            if (response == null && response.getToken() == null && response.getToken().isEmpty()) {
                return ResponseHandler.generateResponse("Token not found", HttpStatus.BAD_REQUEST);
            }
            return ResponseHandler.generateResponse("User registered successfully", HttpStatus.OK, response.getToken());

        } catch (Exception e) {
            return ResponseHandler.generateResponse("User not registered try again", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> authenticate(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
            var user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("user not found register first"));
            AuthenticationResponse response = getResponse(user);
            if (response == null && response.getToken() == null && response.getToken().isEmpty()) {
                return ResponseHandler.generateResponse("Token not found", HttpStatus.BAD_REQUEST);
            }
            return ResponseHandler.generateResponse("User Authenticated successfully", HttpStatus.OK, response.getToken());
        } catch (Exception e) {
            return ResponseHandler.generateResponse("user not found register first", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private AuthenticationResponse getResponse(User user) {
        String jwtToken = jwtService.generateToken(user);
        AuthenticationResponse response = AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
        return response;
    }

    public ResponseEntity<?> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<AbstractUser> newUser = abstractUsers(users);
        try {
            if (users.size() == 0) {
                return ResponseHandler.generateResponse("No user found", HttpStatus.OK);
            }
            return ResponseHandler.generateResponse("users fetched successfully", HttpStatus.OK, newUser);
        } catch (Exception e) {
            return ResponseHandler.generateResponse(" Some Exception Occur", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<AbstractUser> abstractUsers(List<User> users) {
        List<AbstractUser> newList = new ArrayList<>();
        for (User user : users) {
            AbstractUser abstractUser = new AbstractUser(user.getId(),
                    user.getEmp_id(), user.getClient_id(),
                    user.getUsername(), user.getLastLoginTime(),
                    user.getLastPasswordReset());
            newList.add(abstractUser);
        }
        return newList;
    }

    public ResponseEntity<?> getUserById(int id) {
        try {
            if (id > 0) {
                User user = userRepository.findById(id);
                AbstractUser abstractUser = new AbstractUser(user.getId(),
                        user.getEmp_id(), user.getClient_id(),
                        user.getUsername(), user.getLastLoginTime(),
                        user.getLastPasswordReset());

                if (user != null) {
                    return ResponseHandler.generateResponse("User found successfully", HttpStatus.OK, abstractUser);
                } else {
                    return ResponseHandler.generateResponse("User Not Found", HttpStatus.NOT_FOUND);
                }
            } else {
                return ResponseHandler.generateResponse("enter valid id", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseHandler.generateResponse("User Not Found", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> updateUserById(int id, User updateUser) {
        try {
            if (id > 0) {
                User user = userRepository.findById(id);
                // Update fields as needed
                if (user != null) {
                    user.setClient_id(updateUser.getClient_id());
                    user.setEmp_id(updateUser.getEmp_id());
                    user.setUsername(updateUser.getUsername());
                    // Hash the updated password
                    String hashedPassword = passwordEncoder.encode(updateUser.getPassword());
                    user.setPassword(hashedPassword);

                    user.setLastLoginTime(updateUser.getLastLoginTime());
                    user.setLastPasswordReset(updateUser.getLastPasswordReset());
                    user.setEnable(updateUser.getEnable());
                    userRepository.save(user);
                    return ResponseHandler.generateResponse("user update successfully", HttpStatus.OK, user);
                } else {
                    return ResponseHandler.generateResponse("User not found", HttpStatus.BAD_REQUEST);
                }
            } else {
                return ResponseHandler.generateResponse("enter valid id", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return ResponseHandler.generateResponse("Some exception Occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
