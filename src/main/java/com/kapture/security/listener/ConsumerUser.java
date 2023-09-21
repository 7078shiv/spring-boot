package com.kapture.security.listener;
import com.kapture.security.config.JwtService;
import com.kapture.security.constant.AppConstants;
import com.kapture.security.dto.AuthenticationResponse;
import com.kapture.security.dto.RegisterRequestDto;
import com.kapture.security.repository.UserRepository;
import com.kapture.security.user.Role;
import com.kapture.security.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
@Service
public class ConsumerUser {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;

    private final Logger logger = LoggerFactory.getLogger(User.class);
    @KafkaListener(topics = AppConstants.TOPIC,groupId = AppConstants.GROUP_ID,containerFactory = "userKafkaListenerContainerFactory")
    public void consumeJson(RegisterRequestDto registerRequestDto){
        User user = User.builder()
                .username(registerRequestDto.getUsername())
                .password(passwordEncoder.encode(registerRequestDto.getPassword()))
                .emp_id(registerRequestDto.getEmp_id())
                .client_id(registerRequestDto.getClient_id())
                .enable(registerRequestDto.getEnable())
                .lastLoginTime(new Date())
                .role(Role.USER)
                .build();
        User user1 =userRepository.saveOrUpdateUser(user);
        AuthenticationResponse response = getResponse(user);
        if ((response == null || user1==null) || response.getToken() == null || response.getToken().isEmpty()) {
            logger.error("Failed to add or update user");
        }
        logger.info("User added Successfully " + registerRequestDto);
    }
    private AuthenticationResponse getResponse(User user){
        String jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
