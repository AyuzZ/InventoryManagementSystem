package com.example.inventorymanagementsystem.controller;

import com.example.inventorymanagementsystem.dto.UserResponseDTO;
import com.example.inventorymanagementsystem.entity.Product;
import com.example.inventorymanagementsystem.entity.User;
import com.example.inventorymanagementsystem.entity.Vendor;
import com.example.inventorymanagementsystem.exceptions.ProductNotFoundException;
import com.example.inventorymanagementsystem.exceptions.VendorNotFoundException;
import com.example.inventorymanagementsystem.service.ProductService;
import com.example.inventorymanagementsystem.service.UserService;
import com.example.inventorymanagementsystem.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private VendorService vendorService;

    @Autowired
    private ProductService productService;


    @GetMapping()
    public ResponseEntity<?> getAllUsers(){

        //Getting all users
        try {
            List<User> userList = userService.getUsers();
            List<UserResponseDTO> userResponseDTOList = userList.stream()
                    .map(user -> UserResponseDTO.builder()
                            .uid(user.getUid())
                            .username(user.getUsername())
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .roles(user.getRoles())
                            .build())
                    .collect(Collectors.toList());
            return new ResponseEntity<>(userResponseDTOList, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    //Delete User
    @DeleteMapping("delete/user/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username){
        User user;
        //Checking if user exists. Retrieving it if it exists.
        try{
            user = userService.getUser(username);
        }catch (UsernameNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        //Deleting the user
        try {
            User deletedUser = userService.deleteUser(user);
            return new ResponseEntity<>("User Deleted.", HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>("User Could Not Be Deleted. Exception: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Delete Vendor
    @DeleteMapping("delete/vendor/{id}")
    public ResponseEntity<?> deleteVendor(@PathVariable int id){
        Vendor vendor;
        try{
            vendor = vendorService.getVendorById(id);
        } catch (VendorNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        // Delete Query
        try {
            vendorService.deleteVendor(vendor);
            return new ResponseEntity<>("Vendor Deleted.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Vendor Could Not Be Deleted. Exception: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Delete Product
    @DeleteMapping("delete/product/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable int id) {

        Product productDB;

        //Checking if product exists.
        try{
            productDB = productService.getProductById(id);
        }catch (ProductNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        //Deleting the user
        try {
            productService.deleteProduct(productDB);
            return new ResponseEntity<>("Product Deleted.", HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>("Product Could Not Be Deleted. Exception: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


}
