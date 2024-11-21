package com.example.inventorymanagementsystem.service;

import com.example.inventorymanagementsystem.entity.Vendor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VendorService {

    Vendor createVendor(Vendor vendor);

    List<Vendor> getVendors();

    Vendor getVendorById(int id);

    Vendor getAvailableVendorById(int id);

    Vendor getVendorByContact(String contact);

    boolean vendorExists(int id);

    Vendor updateVendor(Vendor vendor);

    Vendor deleteVendor(Vendor vendor);

    void importFromCSV(MultipartFile file);

    String exportToCSV();
}
