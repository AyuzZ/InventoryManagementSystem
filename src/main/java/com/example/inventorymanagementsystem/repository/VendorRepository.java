package com.example.inventorymanagementsystem.repository;

import com.example.inventorymanagementsystem.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Integer> {

//    Optional<Vendor> findByVName(String name);

}
