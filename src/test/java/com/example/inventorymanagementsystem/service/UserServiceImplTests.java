package com.example.inventorymanagementsystem.service;

import com.example.inventorymanagementsystem.dto.UpdateUserDTO;
import com.example.inventorymanagementsystem.entity.Role;
import com.example.inventorymanagementsystem.entity.User;
import com.example.inventorymanagementsystem.exceptions.UserExistsException;
import com.example.inventorymanagementsystem.repository.UserRepository;
import com.example.inventorymanagementsystem.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceImplTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private Role testRole;

    @BeforeEach
    public void setUp() {
        testRole = new Role();
        testRole.setName("STAFF");

        testUser = new User();
        testUser.setUsername("testUser");
        testUser.setPassword("password");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
    }

    @Test
    public void testCreateUser_Success() {

        when(userRepository.findUserByUsername(testUser.getUsername())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User createdUser = userService.createUser(testUser, testRole);

        assertNotNull(createdUser);
        verify(userRepository).save(any(User.class));
        assertTrue(createdUser.getRoles().contains(testRole));
    }

    @Test
    public void testCreateUser_UserAlreadyExists() {

        when(userRepository.findUserByUsername(testUser.getUsername()))
                .thenReturn(Optional.of(testUser));

        assertThrows(UserExistsException.class, () -> {
            userService.createUser(testUser, testRole);
        });
    }

    @Test
    public void testGetUser_Success() {

        when(userRepository.findUserByUsername("testUser"))
                .thenReturn(Optional.of(testUser));

        User foundUser = userService.getUser("testUser");

        assertEquals(testUser, foundUser);
    }

    @Test
    public void testGetUser_NotFound() {

        when(userRepository.findUserByUsername("testUser"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userService.getUser("testUser");
        });
    }

    @Test
    public void testGetUsers() {

        List<User> users = Arrays.asList(testUser, new User());
        when(userRepository.findAll()).thenReturn(users);

        List<User> retrievedUsers = userService.getUsers();

        assertEquals(2, retrievedUsers.size());
    }

    @Test
    public void testGetUserId_Success() {

        when(userRepository.getUserId("testUser")).thenReturn(Optional.of(1));

        Integer userId = userService.getUserId("testUser");

        assertEquals(1, userId);
    }

    @Test
    public void testGetUserId_NotFound() {

        when(userRepository.getUserId("testUser")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userService.getUserId("testUser");
        });
    }

    @Test
    public void testUpdateUser_Success() {

        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setUsername("testUser");
        updateUserDTO.setFirstName("Jane");
        updateUserDTO.setLastName("Smith");

        when(userRepository.findUserByUsername("testUser"))
                .thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User updatedUser = userService.updateUser(updateUserDTO);

        assertEquals("Jane", updatedUser.getFirstName());
        assertEquals("Smith", updatedUser.getLastName());
    }

    @Test
    public void testDeleteUser() {
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User deletedUser = userService.deleteUser(testUser);

        assertTrue(deletedUser.getUsername().endsWith("_deleted"));
        assertNull(deletedUser.getPassword());
        assertNull(deletedUser.getFirstName());
        assertNull(deletedUser.getLastName());
    }
}