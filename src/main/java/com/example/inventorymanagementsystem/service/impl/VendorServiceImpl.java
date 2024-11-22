package com.example.inventorymanagementsystem.service.impl;

import com.example.inventorymanagementsystem.dto.VendorCSVRepresentation;
import com.example.inventorymanagementsystem.entity.Vendor;
import com.example.inventorymanagementsystem.exceptions.EmptyCSVFileException;
import com.example.inventorymanagementsystem.exceptions.VendorExistsException;
import com.example.inventorymanagementsystem.exceptions.VendorNotFoundException;
import com.example.inventorymanagementsystem.repository.VendorRepository;
import com.example.inventorymanagementsystem.service.VendorService;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Override
    public void importFromCSV(MultipartFile file) {
        if (file.isEmpty()) {
            throw new EmptyCSVFileException("CSV file is empty.");
        }

        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            HeaderColumnNameMappingStrategy<VendorCSVRepresentation> strategy = new HeaderColumnNameMappingStrategy<>();
            strategy.setType(VendorCSVRepresentation.class);

            CsvToBean<VendorCSVRepresentation> csvToBean = new CsvToBeanBuilder<VendorCSVRepresentation>(reader)
                    .withMappingStrategy(strategy)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withIgnoreEmptyLine(true)
                    .build();

            List<Vendor> vendors = csvToBean.parse()
                    .stream()
                    .map(csvLine -> Vendor.builder()
                            .name(csvLine.getName())
                            .contact(csvLine.getContact())
                            .build()
                    )
                    .collect(Collectors.toList());

            vendors.forEach(vendor -> {
                try {
                    vendorRepository.save(vendor);
                } catch (VendorExistsException e) {
                    throw new RuntimeException("Error processing CSV data: " + e.getMessage(), e);
                }
            });

        } catch (IOException e) {
            throw new RuntimeException("Error reading CSV file: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error processing CSV data: " + e.getMessage(), e);
        }
    }

    @Override
    public String exportToCSV() {
        List<Vendor> vendorList = vendorRepository.findAll();
        String filePath = "vendors.csv";

        try(CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {

            String[] header = {"Id", "Name", "Contact"};
            writer.writeNext(header);

            for(Vendor vendor : vendorList) {
                String[] data = {String.valueOf(vendor.getVid()),
                        vendor.getName(),
                        vendor.getContact()
                };
                writer.writeNext(data);
            }

        } catch (Exception ex) {
            throw new RuntimeException("Error :" + ex.getMessage(), ex);
        }
        return filePath;
    }

}
