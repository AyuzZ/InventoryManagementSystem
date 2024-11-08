package com.example.inventorymanagementsystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "vendors")
public class Vendor {
    @Id
    private Integer vid;
    private String name;
    private String contact;

    @OneToMany(mappedBy = "vendor")
    @JsonIgnore
    private List<Product> products = new ArrayList<>();
}
