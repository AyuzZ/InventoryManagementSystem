package com.example.inventorymanagementsystem.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductToVendorResponseDTO {
    private Integer vpId;
    private Integer stockQuantity;
    private Double unitPrice;
    private Integer vendorId;
    private String vendorName;
}
