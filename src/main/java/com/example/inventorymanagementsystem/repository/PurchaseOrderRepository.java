package com.example.inventorymanagementsystem.repository;

import com.example.inventorymanagementsystem.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Integer> {
}
