package com.example.inventorymanagementsystem.repository;

import com.example.inventorymanagementsystem.entity.Product;
import com.example.inventorymanagementsystem.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Integer> {

//    Optional<Vendor> findByVName(String name);
    @Query("SELECT v FROM Vendor v WHERE v.vid = ?1 AND v.name NOT LIKE '%_deleted'")
    Optional<Vendor> getAvailableVendorById(int id);

    @Query("SELECT v FROM Vendor v WHERE v.contact = ?1 AND v.name NOT LIKE '%_deleted'")
    Optional<Vendor> getAvailableVendorByContact(String contact);

    @Query("SELECT COUNT(v) FROM Vendor v WHERE v.vid = ?1 AND v.name NOT LIKE '%_deleted'")
    Integer checkVendor(int vid);
}
