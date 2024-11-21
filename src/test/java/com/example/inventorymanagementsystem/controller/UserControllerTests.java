package com.example.inventorymanagementsystem.controller;

import org.springframework.boot.test.context.SpringBootTest;
import com.example.inventorymanagementsystem.dto.UpdateUserDTO;
import com.example.inventorymanagementsystem.dto.UserResponseDTO;
import com.example.inventorymanagementsystem.entity.Role;
import com.example.inventorymanagementsystem.entity.User;
import com.example.inventorymanagementsystem.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserControllerTests {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User user;
    private Role role;

    @BeforeEach
    public void setUp() {

        role = new Role();
        role.setName("OWNER");

        List<Role> roleList = new ArrayList<>();
        roleList.add(role);
        
        user = new User();
        user.setUsername("testUser");
        user.setUid(1);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setRoles(roleList);

        // Setup authentication context
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getUsername(), "password"
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    public void testGetUser_Success() {
        when(userService.getUser("testUser")).thenReturn(user);

        ResponseEntity<?> response = userController.getUser();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof UserResponseDTO);
        UserResponseDTO userResponseDTO = (UserResponseDTO) response.getBody();
        assertEquals(user.getUsername(), userResponseDTO.getUsername());
    }

    @Test
    public void testGetUser_UserNotFound() {
        when(userService.getUser("testUser")).thenThrow(new UsernameNotFoundException("User not found"));

        ResponseEntity<?> response = userController.getUser();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User not found", response.getBody());
    }

    @Test
    public void testUpdateUser_Success() {
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setFirstName("Updated");
        updateUserDTO.setLastName("Name");

        when(userService.updateUser(any(UpdateUserDTO.class))).thenReturn(user);

        ResponseEntity<?> response = userController.updateUser(updateUserDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User Updated.", response.getBody());

        // Verify username was set from security context
        verify(userService).updateUser(argThat(dto ->
                dto.getUsername().equals("testUser")
        ));
    }

    @Test
    public void testUpdateUser_Failure() {
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();

        when(userService.updateUser(any(UpdateUserDTO.class)))
                .thenThrow(new RuntimeException("Update failed"));

        ResponseEntity<?> response = userController.updateUser(updateUserDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("User Update Failed"));
    }
    
}
