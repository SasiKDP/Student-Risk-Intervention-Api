package com.education.student.service;

import com.education.student.dto.UserDto;
import com.education.student.dto.UserResponseDto;
import com.education.student.exceptions.ResourceNotFoundException;
import com.education.student.model.Role;
import com.education.student.model.User;
import com.education.student.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User authenticate(String username, String password) {
        log.info("Authenticating user with username: {}", username);
        return userRepository.findByUsername(username)
                .filter(user -> user.getPassword().equals(password))
                .orElseThrow(() -> {
                    log.error("Authentication failed for username: {}", username);
                    return new RuntimeException("Invalid username or password");
                });
    }

    public UserResponseDto createUser(UserDto dto) {
        log.info("Registering new user with username: {}", dto.getUsername());

        // Just check null, no need to check empty string because it's enum
        if (dto.getRole() == null) {
            log.error("Role is missing for user: {}", dto.getUsername());
            throw new ResourceNotFoundException("Role is required");
        }

        User user = new User();
        user.setId(generateUserId());
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setRole(dto.getRole());  // Directly assign enum from dto

        userRepository.save(user);
        log.info("User created successfully with ID: {}", user.getId());

        return new UserResponseDto(user.getId(), user.getUsername(), user.getRole());
    }


    private String generateUserId() {
        int count = userRepository.findAll().size() + 1;
        return String.format("USER%04d", count);
    }


    public List<User> getAllUsersList() {
        log.info("Fetching all users from repository.");
        return userRepository.findAll();
    }
}
