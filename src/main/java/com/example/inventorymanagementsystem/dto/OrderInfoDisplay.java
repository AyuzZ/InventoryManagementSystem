package com.example.inventorymanagementsystem.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderInfoDisplay {
    private Integer pid;
    private String pName;
    private Double unitPrice;
    private Integer orderQuantity;
    private Double lineTotal;
}
