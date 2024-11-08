package com.example.inventorymanagementsystem.controller;

import com.example.inventorymanagementsystem.entity.Product;
import com.example.inventorymanagementsystem.entity.Vendor;
import com.example.inventorymanagementsystem.exceptions.VendorExistsException;
import com.example.inventorymanagementsystem.exceptions.VendorNotFoundException;
import com.example.inventorymanagementsystem.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vendor/")
public class VendorController {

    @Autowired
    private VendorService vendorService;

    @PostMapping()
    public ResponseEntity<?> createVendor(@RequestBody Vendor vendor){
        try{
            Vendor createdVendor = vendorService.createVendor(vendor);
        } catch (VendorExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Vendor Created.", HttpStatus.CREATED);
    }

    @GetMapping()
    public ResponseEntity<?> getAllVendors(){
        try {
            List<Vendor> vendorList = vendorService.getVendors();
            return new ResponseEntity<>(vendorList, HttpStatus.OK);
        } catch (VendorNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateVendor(@PathVariable int id, @RequestBody Vendor vendor){
        Vendor existingVendor;
        try{
            existingVendor = vendorService.getVendorById(id);
        } catch (VendorNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        existingVendor.setName(vendor.getName());
        existingVendor.setContact(vendor.getContact());

        try {
            Vendor updatedVendor = vendorService.updateVendor(existingVendor);
        } catch (Exception e){
            return new ResponseEntity<>("Vendor Details Update Failed. Because of: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Vendor Details Updated.", HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteVendor(@PathVariable int id){
        Vendor vendor;
        try{
            vendor = vendorService.getVendorById(id);
        } catch (VendorNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        // Removing from products.
        List<Product> products = vendor.getProducts();
        for (Product product : products){
            product.setVendor(null);
        }

        // Delete Query
        try {
            vendorService.deleteVendor(id);
            return new ResponseEntity<>("Vendor Deleted.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Vendor Could Not Be Deleted. Exception: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
