package com.example.inventorymanagementsystem.controller;

import com.example.inventorymanagementsystem.entity.Role;
import com.example.inventorymanagementsystem.entity.User;
import com.example.inventorymanagementsystem.exceptions.RoleNotFoundException;
import com.example.inventorymanagementsystem.exceptions.UserExistsException;
import com.example.inventorymanagementsystem.service.RoleService;
import com.example.inventorymanagementsystem.service.UserService;
import com.example.inventorymanagementsystem.service.impl.UserDetailsServiceImpl;
import com.example.inventorymanagementsystem.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AuthControllerTests {

    @Mock
    private UserService userService;

    @Mock
    private RoleService roleService;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    private User user;
    private Role testRole;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setUsername("username");
        user.setPassword("password");

        testRole = new Role();
        testRole.setName("STAFF");
    }

    @Test
    public void testCreateUser_Success() throws UserExistsException {
        user.setUsername("validUser");
        user.setPassword("validPassword");

        when(roleService.getRoleByName("STAFF")).thenReturn(testRole);
        when(userService.createUser(any(User.class), any(Role.class))).thenReturn(user);

        ResponseEntity<?> response = authController.createUser(user);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("User Created. Login to view your details.", response.getBody());
    }

    @Test
    public void testCreateUser_ShortUsername() {
        user.setUsername("u");

        ResponseEntity<?> response = authController.createUser(user);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Username Must be at least 4 Characters Long.", response.getBody());
    }

    @Test
    public void testCreateUser_ShortPassword() {
        user.setUsername("username");
        user.setPassword("pw");

        ResponseEntity<?> response = authController.createUser(user);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Password Must be at least 6 Characters Long.", response.getBody());
    }

    @Test
    public void testCreateUser_RoleNotFound() throws RoleNotFoundException {

        when(roleService.getRoleByName("STAFF")).thenThrow(new RoleNotFoundException("Role not found"));

        ResponseEntity<?> response = authController.createUser(user);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Role not found", response.getBody());
    }

    @Test
    public void testCreateUser_UserExists() throws UserExistsException {
 
        when(roleService.getRoleByName("STAFF")).thenReturn(testRole);
        when(userService.createUser(any(User.class), any(Role.class)))
                .thenThrow(new UserExistsException("User already exists"));

        ResponseEntity<?> response = authController.createUser(user);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User already exists", response.getBody());
    }

    @Test
    public void testLogin_Success() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(user.getUsername());
        when(userDetailsService.loadUserByUsername(user.getUsername())).thenReturn(userDetails);
        when(jwtUtil.generateToken(user.getUsername())).thenReturn("mock-jwt-token");

        ResponseEntity<String> response = authController.login(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("mock-jwt-token", response.getBody());
        verify(authenticationManager).authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        );
    }

    @Test
    public void testLogin_Failure() {
        doThrow(new RuntimeException("Authentication failed"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        ResponseEntity<String> response = authController.login(user);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Incorrect username or password.", response.getBody());
    }
}
