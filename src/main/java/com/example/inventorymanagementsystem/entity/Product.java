package com.example.inventorymanagementsystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "products")
public class Product {
    @Id
    private Integer pid;
    private String name;
    private String description;
    private Double unitPrice;
    private Integer stockQuantity;

    @ManyToOne
    private Vendor vendor;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<OrderInfo> orderInfoList = new ArrayList<>();
}
