package com.example.inventorymanagementsystem.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "purchaseOrders")
public class PurchaseOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer oid;
    private Date orderDate;
    private Double orderTotal;
    private Integer orderQuantity;
    private String orderStatus;
    private Double unitPrice;

    @ManyToOne(fetch = FetchType.LAZY)
//    @JsonBackReference // Prevent infinite serialization loop with Products
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
//    @JsonBackReference // Prevent infinite serialization loop with Vendors
    private Vendor vendor;

}
