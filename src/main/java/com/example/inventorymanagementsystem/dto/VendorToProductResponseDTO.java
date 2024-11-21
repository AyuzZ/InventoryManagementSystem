package com.example.inventorymanagementsystem.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VendorToProductResponseDTO {
    private Integer vpId;
    private Integer stockQuantity;
    private Double unitPrice;
    private Integer productId;
    private String productName;
}
