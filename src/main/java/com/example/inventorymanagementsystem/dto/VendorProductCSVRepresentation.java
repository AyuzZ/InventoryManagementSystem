package com.example.inventorymanagementsystem.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VendorProductCSVRepresentation {

    @CsvBindByName(column = "StockQuantity")
    private int stockQuantity;
    @CsvBindByName(column = "UnitPrice")
    private double unitPrice;
    @CsvBindByName(column = "Vendor ID")
    private int vid;
    @CsvBindByName(column = "Product ID")
    private int pid;
}
