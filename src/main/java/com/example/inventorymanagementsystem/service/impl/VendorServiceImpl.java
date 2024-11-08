package com.example.inventorymanagementsystem.service.impl;

import com.example.inventorymanagementsystem.entity.Role;
import com.example.inventorymanagementsystem.entity.Vendor;
import com.example.inventorymanagementsystem.exceptions.VendorExistsException;
import com.example.inventorymanagementsystem.exceptions.VendorNotFoundException;
import com.example.inventorymanagementsystem.repository.RoleRepository;
import com.example.inventorymanagementsystem.repository.VendorRepository;
import com.example.inventorymanagementsystem.service.RoleService;
import com.example.inventorymanagementsystem.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VendorServiceImpl implements VendorService {

    @Autowired
    private VendorRepository vendorRepository;

    @Override
    public Vendor createVendor(Vendor vendor) {
        Optional<Vendor> optionalVendor = vendorRepository.findById(vendor.getVid());
        if(optionalVendor.isPresent()){
            throw new VendorExistsException("Vendor Id Already Exists.");
        }else {
            return vendorRepository.save(vendor);
        }
    }

    @Override
    public List<Vendor> getVendors() {
        return vendorRepository.findAll();
    }

    @Override
    public Vendor getVendorById(int id) {
        Optional<Vendor> optionalVendor = vendorRepository.findById(id);
        if(optionalVendor.isPresent()){
            return optionalVendor.get();
        }else {
            throw new VendorNotFoundException("Vendor Not Found.");
        }
    }

//    @Override
//    public Vendor getVendorByName(String name) {
//        Optional<Vendor> optionalVendor = vendorRepository.findByName(name);
//        if(optionalVendor.isPresent()){
//            return optionalVendor.get();
//        }else {
//            throw new VendorNotFoundException("Vendor Not Found.");
//        }
//    }

    @Override
    public Vendor updateVendor(Vendor vendor) {
        return vendorRepository.save(vendor);
    }

    @Override
    public void deleteVendor(int id) {
        vendorRepository.deleteById(id);
    }

}
