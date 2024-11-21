package com.example.inventorymanagementsystem.controller;

import com.example.inventorymanagementsystem.entity.Role;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.inventorymanagementsystem.dto.UserResponseDTO;
import com.example.inventorymanagementsystem.entity.Product;
import com.example.inventorymanagementsystem.entity.User;
import com.example.inventorymanagementsystem.entity.Vendor;
import com.example.inventorymanagementsystem.exceptions.ProductNotFoundException;
import com.example.inventorymanagementsystem.exceptions.VendorNotFoundException;
import com.example.inventorymanagementsystem.service.ProductService;
import com.example.inventorymanagementsystem.service.UserService;
import com.example.inventorymanagementsystem.service.VendorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AdminControllerTests {

    @Mock
    private UserService userService;

    @Mock
    private VendorService vendorService;

    @Mock
    private ProductService productService;

    @InjectMocks
    private AdminController adminController;

    private User user;
    private Vendor vendor;
    private Product product;
    private Role role1;
    private Role role2;

    @BeforeEach
    public void setUp() {
        role1 = new Role();
        role1.setName("OWNER");

        role2 = new Role();
        role2.setName("STAFF");

        List<Role> roleList = new ArrayList<>();
        roleList.add(role1);
        roleList.add(role2);

        user = new User();
        user.setUsername("testUser");
        user.setUid(1);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setRoles(roleList);

        vendor = new Vendor();
        vendor.setVid(1);
        vendor.setName("Test Vendor");

        product = new Product();
        product.setPid(1);
        product.setName("Test Product");
    }

    @Test
    public void testGetAllUsers_Success() {
        List<User> mockUsers = Arrays.asList(user);
        when(userService.getUsers()).thenReturn(mockUsers);

        ResponseEntity<?> response = adminController.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof List);
        List<UserResponseDTO> userResponseList = (List<UserResponseDTO>) response.getBody();
        assertFalse(userResponseList.isEmpty());
        assertEquals(user.getUsername(), userResponseList.get(0).getUsername());
    }

    @Test
    public void testGetAllUsers_Exception() {
        when(userService.getUsers()).thenThrow(new RuntimeException("Error fetching users"));

        ResponseEntity<?> response = adminController.getAllUsers();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error fetching users", response.getBody());
    }

    @Test
    public void testDeleteUser_Success() {
        when(userService.getUser("testUser")).thenReturn(user);
        when(userService.deleteUser(user)).thenReturn(user);

        ResponseEntity<?> response = adminController.deleteUser("testUser");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User Deleted.", response.getBody());
    }

    @Test
    public void testDeleteUser_UserNotFound() {
        when(userService.getUser("testUser")).thenThrow(new UsernameNotFoundException("User not found"));

        ResponseEntity<?> response = adminController.deleteUser("testUser");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User not found", response.getBody());
    }

    @Test
    public void testDeleteVendor_Success() {
        when(vendorService.getVendorById(1)).thenReturn(vendor);
        when(vendorService.deleteVendor(vendor)).thenReturn(vendor);

        ResponseEntity<?> response = adminController.deleteVendor(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Vendor Deleted.", response.getBody());
    }

    @Test
    public void testDeleteVendor_NotFound() {
        when(vendorService.getVendorById(1)).thenThrow(new VendorNotFoundException("Vendor not found"));

        ResponseEntity<?> response = adminController.deleteVendor(1);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Vendor not found", response.getBody());
    }

    @Test
    public void testDeleteProduct_Success() {
        when(productService.getProductById(1)).thenReturn(product);
        when(productService.deleteProduct(product)).thenReturn(product);

        ResponseEntity<?> response = adminController.deleteProduct(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product Deleted.", response.getBody());
    }

    @Test
    public void testDeleteProduct_NotFound() {
        when(productService.getProductById(1)).thenThrow(new ProductNotFoundException("Product not found"));

        ResponseEntity<?> response = adminController.deleteProduct(1);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Product not found", response.getBody());
    }
}
