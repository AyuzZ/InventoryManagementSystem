package com.example.inventorymanagementsystem.service;

import com.example.inventorymanagementsystem.entity.VendorProduct;
import org.springframework.web.multipart.MultipartFile;

public interface VendorProductService {

    VendorProduct getVendorProductByVidAndPidAndPrice(int vid, int pid, double unitPrice);

    VendorProduct saveVendorProduct(VendorProduct vendorProduct);

    void importFromCSV(MultipartFile file);

    String exportToCSV();
}
