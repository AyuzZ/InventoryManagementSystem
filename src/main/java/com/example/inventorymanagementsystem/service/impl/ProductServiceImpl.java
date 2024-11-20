package com.example.inventorymanagementsystem.service.impl;

import com.example.inventorymanagementsystem.entity.Product;
import com.example.inventorymanagementsystem.entity.VendorProduct;
import com.example.inventorymanagementsystem.exceptions.ProductExistsException;
import com.example.inventorymanagementsystem.exceptions.ProductNotFoundException;
import com.example.inventorymanagementsystem.repository.ProductRepository;
import com.example.inventorymanagementsystem.repository.VendorProductRepository;
import com.example.inventorymanagementsystem.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

}
