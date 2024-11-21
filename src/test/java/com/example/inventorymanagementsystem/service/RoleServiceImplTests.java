package com.example.inventorymanagementsystem.service;

import com.example.inventorymanagementsystem.entity.Role;
import com.example.inventorymanagementsystem.exceptions.RoleExistsException;
import com.example.inventorymanagementsystem.exceptions.RoleNotFoundException;
import com.example.inventorymanagementsystem.repository.RoleRepository;
import com.example.inventorymanagementsystem.service.impl.RoleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
public class RoleServiceImplTests {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    private Role testRole;

    @BeforeEach
    public void setUp() {
        testRole = new Role();
        testRole.setName("ADMIN");
    }

    @Test
    public void testCreateRole(){
        Role role = new Role();
        role.setName("OWNER");
        when(roleRepository.save(role)).thenReturn(role);
        assertEquals(role, roleService.createRole(role));
    }

    @Test
    public void testCreateRole_RoleAlreadyExists() {
        when(roleRepository.findRoleByName(testRole.getName()))
                .thenReturn(Optional.of(testRole));
        assertThrows(RoleExistsException.class, () -> {
            roleService.createRole(testRole);
        });
    }

    @Test
    public void testGetRoleByName(){
        Role role = new Role();
        role.setName("OWNER");
        when(roleRepository.findRoleByName("OWNER")).thenReturn(Optional.of(role));
        assertEquals(role, roleService.getRoleByName("OWNER"));
    }

    @Test
    public void testGetRoleByName_NotFound() {
        when(roleRepository.findRoleByName("ADMIN"))
                .thenReturn(Optional.empty());
        assertThrows(RoleNotFoundException.class, () -> {
            roleService.getRoleByName("ADMIN");
        });
    }

    @Test
    public void testGetRolesTest(){
        Role role = new Role();
        role.setName("OWNER");
        Role anotherRole = new Role();
        anotherRole.setName("STAFF");
        when(roleRepository.findAll()).thenReturn(Stream
                .of(role, anotherRole)
                .collect(Collectors.toList()));
        assertEquals(2, roleService.getRoles().size());
    }

}
