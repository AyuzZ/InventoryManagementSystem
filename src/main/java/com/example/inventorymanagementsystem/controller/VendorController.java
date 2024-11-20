package com.example.inventorymanagementsystem.controller;

import com.example.inventorymanagementsystem.dto.ProductResponseDTO;
import com.example.inventorymanagementsystem.dto.ProductToVendorResponseDTO;
import com.example.inventorymanagementsystem.dto.VendorResponseDTO;
import com.example.inventorymanagementsystem.dto.VendorToProductResponseDTO;
import com.example.inventorymanagementsystem.entity.Product;
import com.example.inventorymanagementsystem.entity.Vendor;
import com.example.inventorymanagementsystem.entity.VendorProduct;
import com.example.inventorymanagementsystem.exceptions.VendorExistsException;
import com.example.inventorymanagementsystem.exceptions.VendorNotFoundException;
import com.example.inventorymanagementsystem.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/vendor/")
public class VendorController {

    @Autowired
    private VendorService vendorService;

    @PostMapping()
    public ResponseEntity<?> createVendor(@RequestBody Vendor vendor){

        if (vendor.getContact().length() != 10)
            return new ResponseEntity<>("Contact must contain 10 numbers.", HttpStatus.BAD_REQUEST);
        try {
            Long.valueOf(vendor.getContact());
        } catch (NumberFormatException e) {
            return new ResponseEntity<>("Contact Must only contain numbers.", HttpStatus.BAD_REQUEST);
        }

        try{
            Vendor createdVendor = vendorService.createVendor(vendor);
        } catch (VendorExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Vendor Created.", HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getVendor(@PathVariable int id){
        try {
            Vendor vendor = vendorService.getVendorById(id);

            List<VendorToProductResponseDTO> vendorToProductResponseDTOList = vendor.getVendorProductList().stream()
                    .map(vendorProduct -> VendorToProductResponseDTO.builder()
                            .vpId(vendorProduct.getVpId())
                            .stockQuantity(vendorProduct.getStockQuantity())
                            .unitPrice(vendorProduct.getUnitPrice())
                            .productId(vendorProduct.getProduct().getPid())
                            .productName(vendorProduct.getProduct().getName())
                            .build())
                    .collect(Collectors.toList());

            VendorResponseDTO vendorResponseDTO = VendorResponseDTO.builder()
                    .vid(vendor.getVid())
                    .name(vendor.getName())
                    .contact(vendor.getContact())
                    .vendorToProductResponseDTOList(vendorToProductResponseDTOList)
                    .build();

            return new ResponseEntity<>(vendorResponseDTO, HttpStatus.OK);
        } catch (VendorNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping()
    public ResponseEntity<?> getVendors(){
        try {
            List<Vendor> vendorList = vendorService.getVendors();

            List<VendorResponseDTO> vendorResponseDTOList = vendorList.stream()
                    .map(vendor -> {
                        // Transform VendorProduct list to VendorToProductResponseDTO list
                        List<VendorToProductResponseDTO> vendorToProductResponseDTOList = vendor.getVendorProductList().stream()
                                .map(vendorProduct -> VendorToProductResponseDTO.builder()
                                        .vpId(vendorProduct.getVpId())
                                        .stockQuantity(vendorProduct.getStockQuantity())
                                        .unitPrice(vendorProduct.getUnitPrice())
                                        .productId(vendorProduct.getProduct().getPid())
                                        .productName(vendorProduct.getProduct().getName())
                                        .build())
                                .collect(Collectors.toList());

                        // Build VendorResponseDTO
                        return VendorResponseDTO.builder()
                                .vid(vendor.getVid())
                                .name(vendor.getName())
                                .contact(vendor.getContact())
                                .vendorToProductResponseDTOList(vendorToProductResponseDTOList)
                                .build();
                    })
                    .collect(Collectors.toList());

            return new ResponseEntity<>(vendorResponseDTOList, HttpStatus.OK);
        } catch (VendorNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateVendor(@PathVariable int id, @RequestBody Vendor vendor){

        if (vendor.getContact().length() != 10)
            return new ResponseEntity<>("Contact must contain 10 numbers.", HttpStatus.BAD_REQUEST);
        try {
            Long.valueOf(vendor.getContact());
        } catch (NumberFormatException e) {
            return new ResponseEntity<>("Contact Must only contain numbers. " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        Vendor existingVendor;
        try{
            existingVendor = vendorService.getVendorById(id);
        } catch (VendorNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        Vendor vendorWithContact = vendorService.getVendorByContact(vendor.getContact());
        if (vendorWithContact != null)
            if (!vendorWithContact.equals(existingVendor))
                return new ResponseEntity<>("Provided contact already belongs to another vendor.", HttpStatus.BAD_REQUEST);

        existingVendor.setName(vendor.getName());
        existingVendor.setContact(vendor.getContact());

        try {
            Vendor updatedVendor = vendorService.updateVendor(existingVendor);
        } catch (Exception e){
            return new ResponseEntity<>("Vendor Details Update Failed. Because of: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Vendor Details Updated.", HttpStatus.OK);
    }

}
