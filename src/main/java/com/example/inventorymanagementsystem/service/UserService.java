package com.example.inventorymanagementsystem.service;

import com.example.inventorymanagementsystem.dto.UpdateUserDTO;
import com.example.inventorymanagementsystem.dto.UserResponseDTO;
import com.example.inventorymanagementsystem.entity.Role;
import com.example.inventorymanagementsystem.entity.User;

import java.util.List;

public interface UserService {

    User createUser(User user, Role userRole);

    User getUser(String username);

    Integer getUserId(String username);

    List<User> getUsers();

    User updateUser(UpdateUserDTO updateUserDTO);

    User deleteUser(User user);

}
