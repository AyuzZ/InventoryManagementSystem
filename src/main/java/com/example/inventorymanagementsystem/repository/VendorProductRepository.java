package com.example.inventorymanagementsystem.repository;

import com.example.inventorymanagementsystem.entity.VendorProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VendorProductRepository extends JpaRepository<VendorProduct, Integer> {

    @Query("SELECT vp FROM VendorProduct vp WHERE vp.vendor.vid = ?1 AND vp.product.pid = ?2 AND vp.unitPrice = ?3")
    Optional<VendorProduct> getVendorProductByVidAndPidAndPrice(int vid, int pid, double unitPrice);

}
