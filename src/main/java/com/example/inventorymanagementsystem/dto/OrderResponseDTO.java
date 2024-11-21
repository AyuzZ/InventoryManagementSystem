package com.example.inventorymanagementsystem.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class OrderResponseDTO {
    private Integer oid;
    private Integer uid;
    private String username;
    private Date orderDate;
    private Double orderTotal;

    private List<OrderInfoDisplay> orderInfoDisplayList;
}


