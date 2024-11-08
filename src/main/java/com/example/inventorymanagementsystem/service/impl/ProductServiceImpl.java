package com.example.inventorymanagementsystem.service.impl;

import com.example.inventorymanagementsystem.entity.Product;
import com.example.inventorymanagementsystem.exceptions.ProductExistsException;
import com.example.inventorymanagementsystem.exceptions.ProductNotFoundException;
import com.example.inventorymanagementsystem.repository.ProductRepository;
import com.example.inventorymanagementsystem.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Product createProduct(Product product) {
        Optional<Product> optionalProduct = productRepository.findById(product.getPid());
        if(optionalProduct.isPresent()){
            throw new ProductExistsException("Product Id Already Exists.");
        }else {
            return productRepository.save(product);
        }
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
    public List<Product> getProductsByName(String name) {
        List<Product> productList = productRepository.getSearchedProductByName(name);
        if(productList != null){
            return productList;
        }
        throw new ProductNotFoundException("Product Not Found.");
    }

    @Override
    public List<Product> getProductsByPrice(Double price) {
        Double lowerLimit = price - 100;
        Double upperLimit = price + 100;
        List<Product> productList = productRepository.getSearchedProductByPrice(lowerLimit, upperLimit);
        if(productList != null){
            return productList;
        }
        throw new ProductNotFoundException("Product Not Found.");
    }

//    @Override
//    public List<Product> getProductsByNameAndPrice(String keyword, Double price) {
//        Double lowerLimit = price - 100;
//        Double upperLimit = price + 100;
//        List<Product> productList = productRepository.getSearchedProductByNameAndPrice(keyword, lowerLimit, upperLimit);
//        if(productList != null){
//            return productList;
//        }
//        throw new ProductNotFoundException("Product Not Found.");
//    }

    @Override
    public Product updateProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(int id) {
        productRepository.deleteById(id);
    }

}
