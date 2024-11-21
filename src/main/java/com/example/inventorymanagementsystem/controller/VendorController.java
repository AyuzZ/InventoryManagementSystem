package com.example.inventorymanagementsystem.controller;

import com.example.inventorymanagementsystem.dto.VendorResponseDTO;
import com.example.inventorymanagementsystem.dto.VendorToProductResponseDTO;
import com.example.inventorymanagementsystem.entity.Vendor;
import com.example.inventorymanagementsystem.exceptions.VendorExistsException;
import com.example.inventorymanagementsystem.exceptions.VendorNotFoundException;
import com.example.inventorymanagementsystem.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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

    @PostMapping(value = "import", consumes = {"multipart/form-data"})
    public ResponseEntity<?> importVendorsFromCSV(@RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return new ResponseEntity<>("File is empty. Please upload a valid CSV file.", HttpStatus.BAD_REQUEST);
        }

        try {
            vendorService.importFromCSV(file);
            return new ResponseEntity<>("Products imported successfully.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error importing products: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/export")
    public ResponseEntity<Resource> exportVendorsToCSV() {
        try {
            String filePath = vendorService.exportToCSV();
            File file = new File(filePath);
            Resource resource = new FileSystemResource(file);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentDisposition(ContentDisposition.builder("attachment")
                    .filename("vendors.csv")
                    .build());

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(file.length())
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
