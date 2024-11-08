package com.example.inventorymanagementsystem.repository;

import com.example.inventorymanagementsystem.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Query("SELECT p FROM Product p WHERE p.name LIKE %?1%")
    public List<Product> getSearchedProductByName(String keyword);

    @Query("SELECT p FROM Product p WHERE p.name BETWEEN ?1 AND ?2")
    public List<Product> getSearchedProductByPrice(Double lowerLimit, Double upperLimit);

    @Query("SELECT p FROM Product p WHERE p.name LIKE %?1% AND p.unitPrice BETWEEN ?2 AND ?3")
    public List<Product> getSearchedProductByNameAndPrice(String keyword, Double lowerLimit, Double upperLimit);
}
