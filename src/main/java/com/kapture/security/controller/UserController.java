package com.kapture.security.controller;
import com.kapture.security.service.AuthenticationServices;
import com.kapture.security.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user-controller")

public class UserController {
    private final AuthenticationServices authenticationServices;

    // GET: Retrieve all user details
    @GetMapping("/get-all-users-details")
    public ResponseEntity<List<User>> getAllUserDetails(){
        List<User> users = authenticationServices.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("get-users-by-id/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id){
        try {
            User user = authenticationServices.getUserById(id);
            if (user != null) {
                return new ResponseEntity<>(user, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {

            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // PUT: Update a user by ID
    @PutMapping("/update-user-details/{id}")
    public ResponseEntity<User> updateUserById(@PathVariable int id, @RequestBody User updateUser){
        try {
            User user = authenticationServices.updateUserById(id, updateUser);
            if (user != null) {
                return new ResponseEntity<>(user, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            // Log the exception
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
