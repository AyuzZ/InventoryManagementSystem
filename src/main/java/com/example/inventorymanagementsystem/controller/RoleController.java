package com.example.inventorymanagementsystem.controller;

import com.example.inventorymanagementsystem.exceptions.RoleExistsException;
import com.example.inventorymanagementsystem.entity.Role;
import com.example.inventorymanagementsystem.exceptions.RoleNotFoundException;
import com.example.inventorymanagementsystem.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/role/")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping
    public ResponseEntity<?> createRole(@RequestBody Role role){
        try {
            Role createdRole = roleService.createRole(role);
        } catch (RoleExistsException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Role Created.", HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<?> getAllRoles(){
        try {
            List<Role> roleList = roleService.getRoles();
            return new ResponseEntity<>(roleList, HttpStatus.OK);
        } catch (RoleNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
