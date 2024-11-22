package com.example.inventorymanagementsystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
