package com.example.inventorymanagementsystem.service;

import com.example.inventorymanagementsystem.entity.Product;

import java.util.List;

public interface ProductService {

    Product createProduct(Product product);

    List<Product> getProducts();

    Product getProductById(int id);

    List<Product> getProductsByName(String keyword);

    List<Product> getProductsByPrice(Double price);

//    List<Product> getProductsByNameAndPrice(String keyword, Double price);

    Product updateProduct(Product product);

    void deleteProduct(int id);

}
