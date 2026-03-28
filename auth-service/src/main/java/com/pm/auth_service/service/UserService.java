package com.pm.auth_service.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pm.auth_service.model.User;
import com.pm.auth_service.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User createUser(String email, String password, String role) {
        if (userExists(email)) {
            throw new RuntimeException("User with email: " + email + " already exists");
        }

        log.info("Creating a new user with email: " + email);
        User newUser = new User(email, encoder.encode(password), role);
        return userRepository.save(newUser);   
    }

    public boolean userExists(String email) {
        return userRepository.existsByEmail(email);
    }
}
