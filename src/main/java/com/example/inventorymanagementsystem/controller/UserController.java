package com.example.inventorymanagementsystem.controller;

import com.example.inventorymanagementsystem.dto.UpdateUserDTO;
import com.example.inventorymanagementsystem.dto.UserResponseDTO;
import com.example.inventorymanagementsystem.entity.User;
import com.example.inventorymanagementsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping()
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

    @PutMapping()
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
}
