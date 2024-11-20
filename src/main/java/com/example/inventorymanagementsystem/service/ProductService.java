package com.example.inventorymanagementsystem.service;

import com.example.inventorymanagementsystem.entity.Product;

import java.util.List;

public interface ProductService {

    Product createProduct(Product product);

    Product getAvailableProductById(int id);

    List<Product> getProducts();

    Product getProductById(int id);

    Product getProductByName(String name);

    Product updateProduct(Product product);

    Product deleteProduct(Product product);

}
