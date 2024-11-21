package com.example.inventorymanagementsystem.service.impl;

import com.example.inventorymanagementsystem.entity.Product;
import com.example.inventorymanagementsystem.entity.VendorProduct;
import com.example.inventorymanagementsystem.exceptions.EmptyCSVFileException;
import com.example.inventorymanagementsystem.exceptions.ProductExistsException;
import com.example.inventorymanagementsystem.exceptions.ProductNotFoundException;
import com.example.inventorymanagementsystem.repository.ProductRepository;
import com.example.inventorymanagementsystem.repository.VendorProductRepository;
import com.example.inventorymanagementsystem.service.ProductService;
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
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private VendorProductRepository vendorProductRepository;


    @Override
    public Product createProduct(Product product) {
        Optional<Product> optionalProduct = productRepository.getProductByName(product.getName());
        if(optionalProduct.isPresent()){
            throw new ProductExistsException("Product Already Exists.");
        }else {
            return productRepository.save(product);
        }
    }

    @Override
    public Product getAvailableProductById(int id){
        Optional<Product> optionalProduct = productRepository.getAvailableProductById(id);
        if(optionalProduct.isPresent()){
            return optionalProduct.get();
        }
        throw new ProductNotFoundException("The product doesn't exist or The product id belongs to a deleted product.");
    }

    @Override
    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(int id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if(optionalProduct.isPresent()){
            return optionalProduct.get();
        }
        throw new ProductNotFoundException("Product Not Found.");
    }

    @Override
    public Product getProductByName(String name) {
        Optional<Product> optionalProduct = productRepository.getProductByName(name);
        return optionalProduct.orElse(null);
    }

    @Override
    public Product updateProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Product deleteProduct(Product product) {
        product.setName(product.getName() + "_deleted");
        return productRepository.save(product);
//        productRepository.deleteById(id);
    }


    @Override
    public void importFromCSV(MultipartFile file) {
        if (file.isEmpty()) {
            throw new EmptyCSVFileException("CSV file is empty.");
        }

        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            HeaderColumnNameMappingStrategy<Product> strategy = new HeaderColumnNameMappingStrategy<>();
            strategy.setType(Product.class);

            CsvToBean<Product> csvToBean = new CsvToBeanBuilder<Product>(reader)
                    .withMappingStrategy(strategy)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withIgnoreEmptyLine(true)
                    .build();

            List<Product> products = csvToBean.parse();

            products.forEach(product -> {
                try {
                    productRepository.save(product);
                } catch (ProductExistsException e) {
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
        List<Product> products = productRepository.findAll();
        String filePath = "products.csv";

        try(CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {

            String[] header = {"Id", "Name", "Description"};
            writer.writeNext(header);

            for(Product product : products) {
                String[] data = {String.valueOf(product.getPid()),
                        product.getName(),
                        product.getDescription()
                };
                writer.writeNext(data);
            }

        } catch (Exception ex) {
            throw new RuntimeException("Error :" + ex.getMessage(), ex);
        }
        return filePath;
    }

}
