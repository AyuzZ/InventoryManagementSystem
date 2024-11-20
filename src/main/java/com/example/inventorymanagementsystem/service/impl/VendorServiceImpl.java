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
        Optional<Vendor> optionalVendor = vendorRepository.getAvailableVendorByContact(vendor.getContact());
        if(optionalVendor.isPresent()){
            throw new VendorExistsException("Vendor Already Exists.");
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

    @Override
    public Vendor getAvailableVendorById(int id){
        Optional<Vendor> optionalVendor = vendorRepository.getAvailableVendorById(id);
        if(optionalVendor.isPresent()){
            return optionalVendor.get();
        }else {
            throw new VendorNotFoundException("The Vendor doesn't exist or The Vendor ID belongs to a Deleted Vendor.");
        }
    }

    @Override
    public Vendor getVendorByContact(String contact){
        Optional<Vendor> optionalVendor = vendorRepository.getAvailableVendorByContact(contact);
        return  optionalVendor.orElse(null);
    }

    @Override
    public boolean vendorExists(int id) {
        return vendorRepository.checkVendor(id) == 1;
    }

    @Override
    public Vendor updateVendor(Vendor vendor) {
        return vendorRepository.save(vendor);
    }

    @Override
    public Vendor deleteVendor(Vendor vendor) {
        vendor.setContact(null);
        vendor.setName(vendor.getName() + "_deleted");
        return vendorRepository.save(vendor);
    }

}
