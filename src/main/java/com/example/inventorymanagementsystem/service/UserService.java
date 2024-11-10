package com.example.inventorymanagementsystem.service;

import com.example.inventorymanagementsystem.entity.User;

import java.util.List;

public interface UserService {

    User createUser(User user);

    User getUser(String username);

    Integer getUserId(String username);

    List<User> getUsers();

    User updateUser(User user);

    void deleteUser(String username);

}
