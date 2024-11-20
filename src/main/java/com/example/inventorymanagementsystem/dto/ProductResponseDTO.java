package com.example.inventorymanagementsystem.dto;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProductResponseDTO {

    private Integer pid;
    private String name;
    private String description;
    private List<ProductToVendorResponseDTO> productToVendorResponseDTOList;
}
