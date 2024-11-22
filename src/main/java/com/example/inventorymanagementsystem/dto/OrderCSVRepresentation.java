package com.example.inventorymanagementsystem.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class OrderCSVRepresentation {

    @CsvBindByName(column = "Product ID")
    private int pid;
    @CsvBindByName(column = "Vendor ID")
    private int vid;
    @CsvBindByName(column = "OrderQuantity")
    private int orderQuantity;
    @CsvBindByName(column = "UnitPrice")
    private double unitPrice;
    @CsvBindByName(column = "OrderStatus")
    private String orderStatus;
    @CsvBindByName(column = "OrderDate")
    private Date orderDate;
    @CsvBindByName(column = "OrderTotal")
    private Double orderTotal;


}
