package com.example.inventorymanagementsystem.service.impl;

import com.example.inventorymanagementsystem.entity.VendorProduct;
import com.example.inventorymanagementsystem.exceptions.EmptyCSVFileException;
import com.example.inventorymanagementsystem.exceptions.VendorProductExistsException;
import com.example.inventorymanagementsystem.repository.VendorProductRepository;
import com.example.inventorymanagementsystem.service.VendorProductService;
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

    @Override
    public void importFromCSV(MultipartFile file) {
        if (file.isEmpty()) {
            throw new EmptyCSVFileException("CSV file is empty.");
        }

        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            HeaderColumnNameMappingStrategy<VendorProduct> strategy = new HeaderColumnNameMappingStrategy<>();
            strategy.setType(VendorProduct.class);

            CsvToBean<VendorProduct> csvToBean = new CsvToBeanBuilder<VendorProduct>(reader)
                    .withMappingStrategy(strategy)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withIgnoreEmptyLine(true)
                    .build();

            List<VendorProduct> vendorProductList = csvToBean.parse();

            vendorProductList.forEach(vendorProduct -> {
                try {
                    vendorProductRepository.save(vendorProduct);
                } catch (VendorProductExistsException e) {
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
        List<VendorProduct> vendorProductList = vendorProductRepository.findAll();
        String filePath = "vendorProducts.csv";

        try(CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {

            String[] header = {"Id", "StockQuantity", "UnitPrice", "Vendor ID", "Product ID"};
            writer.writeNext(header);

            for(VendorProduct vendorProduct : vendorProductList) {
                String[] data = { String.valueOf(vendorProduct.getVpId()),
                        String.valueOf(vendorProduct.getStockQuantity()),
                        String.valueOf(vendorProduct.getUnitPrice()),
                        String.valueOf(vendorProduct.getProduct().getPid()),
                        String.valueOf(vendorProduct.getVendor().getVid())
                };
                writer.writeNext(data);
            }

        } catch (Exception ex) {
            throw new RuntimeException("Error :" + ex.getMessage(), ex);
        }
        return filePath;
    }

}
