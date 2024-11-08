package com.example.inventorymanagementsystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "roles")
public class Role {
    @Id
    private Integer rid;
    private String name;

    @ManyToMany(mappedBy = "roles")
    @JsonIgnore //to stop looped serialization
    private List<User> users = new ArrayList<>();
}
