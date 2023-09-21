package com.kapture.security.service;

import com.kapture.security.config.JwtService;
import com.kapture.security.constant.AppConstants;
import com.kapture.security.dto.*;
import com.kapture.security.repository.UserRepository;
import com.kapture.security.user.Role;
import com.kapture.security.user.User;
import com.kapture.security.util.ResponseHandler;
import com.kapture.security.util.ValidationObject;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("userDetailService")
@RequiredArgsConstructor
public class AuthenticationServices {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Autowired
    private ModelMapper modelMapper;

    private final Logger logger = LoggerFactory.getLogger(User.class);

    public ResponseEntity<?> register(RegisterRequestDto request) {
        try {
            if (userRepository.isUserAvailable(request.getUsername())) {
                if (request.getEnable() == 0 || request.getEnable() == 1) {
                    User user = User.builder()
                            .username(request.getUsername())
                            .password(passwordEncoder.encode(request.getPassword()))
                            .emp_id(request.getEmp_id())
                            .client_id(request.getClient_id())
                            .role(Role.USER).enable(request.getEnable())
                            .lastLoginTime(new Date(System.currentTimeMillis()))
                            .build();
                    AuthenticationResponse response = getResponse(user);
                    User saveUser = userRepository.saveOrUpdateUser(user);
                    if ((response == null || saveUser == null) || response.getToken() == null || response.getToken().isEmpty()) {
                        return ResponseHandler.generateResponse("Token not found", HttpStatus.BAD_REQUEST);
                    }
                    return ResponseHandler.generateResponse("User Registered Successfully", HttpStatus.OK, response.getToken());
                } else {
                    return ResponseHandler.generateResponse("Enable is either Zero or One enter valid No", HttpStatus.BAD_REQUEST);
                }
            } else {
                return ResponseHandler.generateResponse("Already Registered", HttpStatus.OK);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseHandler.generateResponse("User not registered try again", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> authenticate(AuthenticationRequestDto request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            User user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new UsernameNotFoundException("user not found register first"));
            AuthenticationResponse response = getResponse(user);
            if (response == null || response.getToken() == null || response.getToken().isEmpty()) {
                return ResponseHandler.generateResponse("Token not found", HttpStatus.BAD_REQUEST);
            }
            user.setLastLoginTime(new Date(System.currentTimeMillis()));
            User updatedUser = userRepository.saveOrUpdateUser(user);
            return updatedUser != null ? ResponseHandler.generateResponse("User Authenticated successfully", HttpStatus.OK, response.getToken()) : ResponseHandler.generateResponse("user not found", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return ResponseHandler.generateResponse("user not found register first", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private AuthenticationResponse getResponse(User user) {
        String jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    public ResponseEntity<?> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDto> newUser = abstractUsers(users);
        try {
            if (users.isEmpty()) {
                return ResponseHandler.generateResponse("No user found", HttpStatus.OK);
            }
            return ResponseHandler.generateResponse("users fetched successfully", HttpStatus.OK, newUser);
        } catch (Exception e) {
            return ResponseHandler.generateResponse(" Some Exception Occur", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<UserDto> abstractUsers(List<User> users) {
        List<UserDto> newList = new ArrayList<>();
        for (User user : users) {
            UserDto userDto = new UserDto(user.getId(), user.getEmp_id(), user.getClient_id(), user.getUsername(), user.getLastLoginTime(), user.getLastPasswordReset(), user.getEnable());
            newList.add(userDto);
        }
        return newList;
    }

    public ResponseEntity<?> getUserById(int id) {
        try {
            User user = userRepository.findById(id);
            if (user != null) {
                UserDto userDto = new UserDto(user.getId(), user.getEmp_id(), user.getClient_id(), user.getUsername(), user.getLastLoginTime(), user.getLastPasswordReset(), user.getEnable());
                return ResponseHandler.generateResponse("User found successfully", HttpStatus.OK, userDto);
            } else {
                return ResponseHandler.generateResponse("User Not Found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return ResponseHandler.generateResponse("User Not Found", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> saveOrUpdateUser(SaveOrUpdateUserDto saveOrUpdateUserDto){
        try {
            if (saveOrUpdateUserDto != null && saveOrUpdateUserDto.getId() > 0){
                int id = saveOrUpdateUserDto.getId();
                User user = userRepository.findById(id);
                // Update fields as needed
                if (user != null) {
                    user.setClient_id(saveOrUpdateUserDto.getClientId());
                    user.setEmp_id(saveOrUpdateUserDto.getEmpId());
                    user.setUsername(saveOrUpdateUserDto.getUsername());
                    // Hash the updated password
                    String hashedPassword = passwordEncoder.encode(saveOrUpdateUserDto.getPassword());
                    user.setPassword(hashedPassword);

                    // update the lastPasswordReset only if current user give the password.
                    if (user.getPassword() != null){
                        user.setLastPasswordReset(new Date(System.currentTimeMillis()));
                    }
                    // update enable only if its value is zero or one.
                    if (saveOrUpdateUserDto.getEnable() == 0 || saveOrUpdateUserDto.getEnable() == 1)
                        user.setEnable(saveOrUpdateUserDto.getEnable());

                    User updatedUser = userRepository.saveOrUpdateUser(user);

                    return updatedUser!=null ? ResponseHandler.generateResponse("user update successfully", HttpStatus.OK, user)
                            :ResponseHandler.generateResponse("user not updated",HttpStatus.BAD_REQUEST);
                }
                else{
                      return ResponseHandler.generateResponse("Given user not present",HttpStatus.BAD_REQUEST);
                }
            }
            else if(saveOrUpdateUserDto.getId()==0){
                User user1=SaveOrUpdateUserDtoToUser(saveOrUpdateUserDto);
                User updatedUser = userRepository.saveOrUpdateUser(user1);
                return updatedUser!=null ? ResponseHandler.generateResponse("user added successfully", HttpStatus.OK, user1)
                        :ResponseHandler.generateResponse("user not updated",HttpStatus.BAD_REQUEST);
            }
            else {
                return ResponseHandler.generateResponse("enter valid user details", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return ResponseHandler.generateResponse("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public User SaveOrUpdateUserDtoToUser(SaveOrUpdateUserDto saveOrUpdateUserDto) {
        User user=new User();
        user.setClient_id(saveOrUpdateUserDto.getClientId());
        user.setEmp_id(saveOrUpdateUserDto.getEmpId());
        user.setEnable(saveOrUpdateUserDto.getEnable());
        user.setUsername(saveOrUpdateUserDto.getUsername());
        user.setLastPasswordReset(new Date());
        String hashedPassword = passwordEncoder.encode(saveOrUpdateUserDto.getPassword());
        user.setPassword(hashedPassword);
        return user;
    }

    public SaveOrUpdateUserDto userTosaveOrUpdateUserDto( User user){
        return modelMapper.map(user,SaveOrUpdateUserDto.class);
    }

    public ResponseEntity<?> addUserToKafkaServer(RegisterRequestDto registerRequestDto) {
        try {
            if (ValidationObject.validateDto(registerRequestDto)) {
                kafkaTemplate.send(AppConstants.TOPIC, registerRequestDto);
                return ResponseHandler.generateResponse(registerRequestDto.getUsername()+" send to kafka server successfully", HttpStatus.CREATED);
            } else {
                return ResponseHandler.generateResponse("Enter Valid data", HttpStatus.BAD_REQUEST);
            }
        } catch (ConstraintViolationException cv) {
            return ResponseHandler.generateResponse("Constraint violation user with given details already exist", HttpStatus.BAD_REQUEST);
        } catch (NumberFormatException e) {
            return ResponseHandler.generateResponse("Enter valid user details", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseHandler.generateResponse("Failed to create the user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
