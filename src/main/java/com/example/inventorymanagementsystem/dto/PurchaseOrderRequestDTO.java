package com.example.inventorymanagementsystem.dto;

import com.example.inventorymanagementsystem.entity.Product;
import com.example.inventorymanagementsystem.entity.Vendor;
import lombok.Data;

@Data
public class PurchaseOrderRequestDTO {

    private Product product;
    private Vendor vendor;
    private Integer orderQuantity;
    private Double unitPrice;
    private String orderStatus;

}
