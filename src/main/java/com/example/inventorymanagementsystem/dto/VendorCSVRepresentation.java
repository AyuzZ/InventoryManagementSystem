package com.example.inventorymanagementsystem.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VendorCSVRepresentation {

    @CsvBindByName(column = "Name")
    private String name;
    @CsvBindByName(column = "Contact")
    private String contact;
}
