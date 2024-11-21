package com.example.inventorymanagementsystem.controller;

import org.springframework.boot.test.context.SpringBootTest;
import com.example.inventorymanagementsystem.entity.Role;
import com.example.inventorymanagementsystem.exceptions.RoleExistsException;
import com.example.inventorymanagementsystem.exceptions.RoleNotFoundException;
import com.example.inventorymanagementsystem.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class RoleControllerTests {

    @Mock
    private RoleService roleService;

    @InjectMocks
    private RoleController roleController;

    private Role role;

    @BeforeEach
    public void setUp() {
        role = new Role();
        role.setName("ROLE_USER");
    }

    @Test
    public void testCreateRole_Success() throws RoleExistsException {
        when(roleService.createRole(any(Role.class))).thenReturn(role);

        ResponseEntity<?> response = roleController.createRole(role);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Role Created.", response.getBody());
        verify(roleService, times(1)).createRole(role);
    }

    @Test
    public void testCreateRole_RoleExists() throws RoleExistsException {
        when(roleService.createRole(any(Role.class)))
                .thenThrow(new RoleExistsException("Role already exists"));

        ResponseEntity<?> response = roleController.createRole(role);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Role already exists", response.getBody());
    }

    @Test
    public void testGetAllRoles_Success() throws RoleNotFoundException {
        Role roleOwner = new Role();
        roleOwner.setName("OWNER");
        Role roleStaff = new Role();
        roleStaff.setName("STAFF");

        List<Role> rolesList = Arrays.asList(
                roleOwner,
                roleStaff
        );
        when(roleService.getRoles()).thenReturn(rolesList);

        ResponseEntity<?> response = roleController.getAllRoles();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof List);
        assertEquals(rolesList, response.getBody());
    }

    @Test
    public void testGetAllRoles_NoRolesFound() throws RoleNotFoundException {
        when(roleService.getRoles())
                .thenThrow(new RoleNotFoundException("No roles found"));

        ResponseEntity<?> response = roleController.getAllRoles();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("No roles found", response.getBody());
    }
}
