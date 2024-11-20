package com.example.inventorymanagementsystem.controller;

import com.example.inventorymanagementsystem.entity.Role;
import com.example.inventorymanagementsystem.entity.User;
import com.example.inventorymanagementsystem.exceptions.RoleNotFoundException;
import com.example.inventorymanagementsystem.exceptions.UserExistsException;
import com.example.inventorymanagementsystem.service.RoleService;
import com.example.inventorymanagementsystem.service.UserService;
import com.example.inventorymanagementsystem.service.impl.UserDetailsServiceImpl;
//import com.example.inventorymanagementsystem.utils.JwtUtil;
import com.example.inventorymanagementsystem.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/signup/")
    public ResponseEntity<?> createUser(@RequestBody User user){

        //Validation
        if (user.getUsername().length() < 4)
            return new ResponseEntity<>("Username Must be at least 4 Characters Long.", HttpStatus.BAD_REQUEST);

        if (user.getPassword().length() < 6)
            return new ResponseEntity<>("Password Must be at least 6 Characters Long.", HttpStatus.BAD_REQUEST);

        //Checking if role USER has been created or not.
        Role userRole;
        try {
            userRole = roleService.getRoleByName("STAFF");
        }catch (RoleNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        //If the user exists or not is checked in UserServiceImpl
        try{
            User createdUser = userService.createUser(user, userRole);
        }catch (UserExistsException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("User Created. Login to view your details.", HttpStatus.CREATED);
    }

    @PostMapping("/login/")
    public ResponseEntity<String> login(@RequestBody User user){
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
            String jwt = jwtUtil.generateToken(userDetails.getUsername());
            return new ResponseEntity<>(jwt, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>("Incorrect username or password.", HttpStatus.BAD_REQUEST);
        }
    }

}
