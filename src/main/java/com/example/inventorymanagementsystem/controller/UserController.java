package com.example.inventorymanagementsystem.controller;

import com.example.inventorymanagementsystem.dto.UpdateUserDTO;
import com.example.inventorymanagementsystem.dto.UserResponseDTO;
import com.example.inventorymanagementsystem.exceptions.RoleNotFoundException;
import com.example.inventorymanagementsystem.exceptions.UserExistsException;
import com.example.inventorymanagementsystem.entity.Role;
import com.example.inventorymanagementsystem.entity.User;
import com.example.inventorymanagementsystem.service.RoleService;
import com.example.inventorymanagementsystem.service.UserService;
import com.example.inventorymanagementsystem.service.impl.UserDetailsServiceImpl;
import com.example.inventorymanagementsystem.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController {

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

    //No Auth Required

//    @GetMapping({"", "/"})
//    public ResponseEntity<?> getHome(){
//        List<String> endpoints = new ArrayList<>();
//        endpoints.add("Available Endpoints:");
//        endpoints.add("/createUser/ - POST");
//        endpoints.add("/login/ - POST");
//        endpoints.add("/user/ - GET, PUT");
//        endpoints.add("/admin/ - GET");
//        endpoints.add("/delete/{username} - DELETE");
//        endpoints.add("/role/ - GET, PUT");
//        return new ResponseEntity<>(endpoints, HttpStatus.OK);
//    }

    @PostMapping("/signup/")
    public ResponseEntity<?> createUser(@RequestBody User user){

        //Checking if role USER has been created or not.
        Role userRole;
        try {
            userRole = roleService.getRoleByName("USER");
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


    //User and Admin has Access to the following endpoints.

    @GetMapping("/user/")
    public ResponseEntity<?> getUser(){
        //Getting username of logged-in user from security context holder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        //Do we need to catch username not found exception here!?
        //since we are getting username from the Logged-in User, the user/name always exists.
        try{
            //Getting the user from DB
            User user = userService.getUser(username);
            UserResponseDTO userResponseDTO = UserResponseDTO.builder()
                    .uid(user.getUid())
                    .username(user.getUsername())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .roles(user.getRoles())
                    .build();
            return new ResponseEntity<>(userResponseDTO, HttpStatus.OK);
        }catch (UsernameNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/user/")
    public ResponseEntity<?> updateUser(@RequestBody UpdateUserDTO updateUserDTO){
        //Getting username of logged-in user from security context holder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        //Setting the username in the DTO
        updateUserDTO.setUsername(username);

        //Calling update method
        try{
            User updatedUser = userService.updateUser(updateUserDTO);
        }catch (Exception e){
            return new ResponseEntity<>("User Update Failed. Because of: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("User Updated.", HttpStatus.OK);
    }


    //Only Admin has access to the following endpoints.

    @GetMapping({"/admin", "/admin/"})
    public ResponseEntity<?> getAllUsers(){
        try {
            List<User> userList = userService.getUsers();
            List<UserResponseDTO> userResponseDTOList = new ArrayList<>();
            for (User user : userList){
                UserResponseDTO userResponseDTO = UserResponseDTO.builder()
                        .uid(user.getUid())
                        .username(user.getUsername())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .roles(user.getRoles())
                        .build();
                userResponseDTOList.add(userResponseDTO);
            }
            return new ResponseEntity<>(userResponseDTOList, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

// Dont delete user as order placed by the users will also have to be deleted -

    @DeleteMapping("/delete/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username){
        User user;
        //Checking if user exists. Retrieving it if it exists.
        try{
            user = userService.getUser(username);
        }catch (UsernameNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        //Clearing user from the user_roles table too.
//        List<Role> roles = user.getRoles();
//        for (Role role : roles) {
//            role.getUsers().clear();
//        }

        //Deleting the user
        try {
            User deletedUser = userService.deleteUser(user);
            return new ResponseEntity<>("User Deleted.", HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>("User Could Not Be Deleted. Exception: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
