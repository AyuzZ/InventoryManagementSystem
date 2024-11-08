package com.example.inventorymanagementsystem.service;

import com.example.inventorymanagementsystem.entity.Role;

import java.util.List;

public interface RoleService {

    Role createRole(Role role);

    List<Role> getRoles();

    Role getRoleByName(String name);

}
