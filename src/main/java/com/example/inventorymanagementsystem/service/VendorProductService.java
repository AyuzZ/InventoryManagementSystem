package com.example.inventorymanagementsystem.service;

import com.example.inventorymanagementsystem.entity.VendorProduct;

public interface VendorProductService {

    VendorProduct getVendorProductByVidAndPidAndPrice(int vid, int pid, double unitPrice);

    VendorProduct saveVendorProduct(VendorProduct vendorProduct);
}
