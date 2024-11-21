package com.example.inventorymanagementsystem.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class PurchaseOrderResponseDTO {

    private Integer oid;
    private Date orderDate;
    private String orderStatus;
    private Double orderTotal;
    private Integer orderQuantity;
    private Double unitPrice;
    private Integer productId;
    private String productName;
    private Integer vendorId;
    private String vendorName;
}
