package com.education.student.service;

import com.education.student.dto.UserDto;
import com.education.student.dto.UserResponseDto;
import com.education.student.model.User;
import com.education.student.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.education.student.model.Role.ADMIN;
import static com.education.student.model.Role.TEACHER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId("USER0001");
        user.setUsername("testuser");
        user.setPassword("password");
        user.setRole(ADMIN);
    }

    @Test
    void testAuthenticate_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        User result = userService.authenticate("testuser", "password");
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void testAuthenticate_Failure() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        assertThrows(RuntimeException.class, () -> userService.authenticate("testuser", "wrongpass"));
    }

    @Test
    void testAuthenticate_UserNotFound() {
        when(userRepository.findByUsername("nouser")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.authenticate("nouser", "password"));
    }

    @Test
    void testCreateUser_Success() {
        UserDto dto = new UserDto();
        dto.setUsername("newuser");
        dto.setPassword("newpass");
        dto.setRole(TEACHER);

        when(userRepository.findAll()).thenReturn(Collections.emptyList());
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDto response = userService.createUser(dto);
        assertNotNull(response);
        assertEquals("newuser", response.getUsername());
        assertEquals(TEACHER, response.getRole());
        assertTrue(response.getId().startsWith("USER"));
    }

    @Test
    void testGetAllUsersList_ReturnsList() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        List<User> users = userService.getAllUsersList();
        assertEquals(1, users.size());
        assertEquals("testuser", users.get(0).getUsername());
    }
}