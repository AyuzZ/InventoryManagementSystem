package com.example.inventorymanagementsystem.service.impl;

import com.example.inventorymanagementsystem.entity.Role;
import com.example.inventorymanagementsystem.exceptions.RoleExistsException;
import com.example.inventorymanagementsystem.exceptions.RoleNotFoundException;
import com.example.inventorymanagementsystem.repository.RoleRepository;
import com.example.inventorymanagementsystem.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Role createRole(Role role) {
        Optional<Role> optionalRole = roleRepository.findRoleByName(role.getName());
        if(optionalRole.isPresent()){
            throw new RoleExistsException("Role Name Already Exists.");
        }else {
            return roleRepository.save(role);
        }
    }

    @Override
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role getRoleByName(String name) {
        Optional<Role> optionalRole = roleRepository.findRoleByName(name);
        if(optionalRole.isPresent()){
            return optionalRole.get();
        }else {
            throw new RoleNotFoundException("Role Not Found.");
        }
    }


}
