package com.example.inventorymanagementsystem.dto;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class VendorResponseDTO {
    private Integer vid;
    private String name;
    private String contact;
    private List<VendorToProductResponseDTO> vendorToProductResponseDTOList;
}
