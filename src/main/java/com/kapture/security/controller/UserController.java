package com.kapture.security.controller;
import com.kapture.security.dto.SaveOrUpdateUserDto;
import com.kapture.security.service.AuthenticationServices;
import com.kapture.security.user.User;
import com.kapture.security.util.ResponseHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
    @Autowired
    private AuthenticationServices authenticationServices;

    // GET: Retrieve all user details
    @GetMapping("/get-all-users-details")
    public ResponseEntity<?> getAllUserDetails(){
        ResponseEntity<?> users = authenticationServices.getAllUsers();
        return users;
    }

    @GetMapping("get-users-by-id/{id}")
    public ResponseEntity<?> getUserById(@PathVariable int id){
        ResponseEntity<?> user = authenticationServices.getUserById(id);
        return user;
    }

    // PUT: Add or update user by ID
    @PutMapping("/add-or-update-users")
    public ResponseEntity<?> saveOrUpdateUser(@RequestBody SaveOrUpdateUserDto saveOrUpdateUserDto){
         ResponseEntity<?> response = authenticationServices.saveOrUpdateUser(saveOrUpdateUserDto);
        return response;
    }
}
