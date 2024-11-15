package com.example.inventorymanagementsystem.dto;

import com.example.inventorymanagementsystem.entity.Order;
import com.example.inventorymanagementsystem.entity.Role;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
public class UserResponseDTO {
    private Integer uid;
    private String username;
    private String firstName;
    private String lastName;
    private List<Role> roles = new ArrayList<Role>();
}
