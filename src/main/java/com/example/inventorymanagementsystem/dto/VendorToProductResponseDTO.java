package com.example.inventorymanagementsystem.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.context.annotation.Bean;

@Data
@Builder
public class VendorToProductResponseDTO {
    private Integer vpId;
    private Integer stockQuantity;
    private Double unitPrice;
    private Integer productId;
    private String productName;
}
