package com.example.inventorymanagementsystem.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductCSVRepresentation {

    @CsvBindByName(column = "Name")
    private String name;
    @CsvBindByName(column = "Description")
    private String description;
}
