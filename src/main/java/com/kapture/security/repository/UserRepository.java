package com.kapture.security.repository;

import com.kapture.security.user.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findByUsername(String username);
    User findById(int id);

    List<User> findAll();

    User saveOrUpdateUser(User user);

    boolean isUserAvailable(String username);
}
