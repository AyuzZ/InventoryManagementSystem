package com.example.inventorymanagementsystem.repository;

import com.example.inventorymanagementsystem.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Query("SELECT p FROM Product p WHERE p.pid = ?1 AND p.name NOT LIKE '%_deleted'")
    Optional<Product> getAvailableProductById(int id);

    @Query("SELECT p FROM Product p WHERE p.name = ?1 AND p.name NOT LIKE '%_deleted'")
    Optional<Product> getProductByName(String name);
}
