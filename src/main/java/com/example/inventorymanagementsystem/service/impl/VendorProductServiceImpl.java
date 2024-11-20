package com.example.inventorymanagementsystem.service.impl;

import com.example.inventorymanagementsystem.entity.User;
import com.example.inventorymanagementsystem.entity.VendorProduct;
import com.example.inventorymanagementsystem.repository.VendorProductRepository;
import com.example.inventorymanagementsystem.service.VendorProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VendorProductServiceImpl implements VendorProductService {

    @Autowired
    private VendorProductRepository vendorProductRepository;

    @Override
    public VendorProduct getVendorProductByVidAndPidAndPrice(int vid, int pid, double unitPrice) {
        Optional<VendorProduct> optionalVendorProduct = vendorProductRepository.getVendorProductByVidAndPidAndPrice(vid, pid, unitPrice);
        return optionalVendorProduct.orElse(null);
    }

    @Override
    public VendorProduct saveVendorProduct(VendorProduct vendorProduct){
        return vendorProductRepository.save(vendorProduct);
    }

}
