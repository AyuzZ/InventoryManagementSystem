package com.example.inventorymanagementsystem.service;

import com.example.inventorymanagementsystem.entity.Vendor;

import java.util.List;

public interface VendorService {

    Vendor createVendor(Vendor vendor);

    List<Vendor> getVendors();

    Vendor getVendorById(int id);

//    Vendor getVendorByName(String name);

    Vendor updateVendor(Vendor vendor);

    void deleteVendor(int id);
}
