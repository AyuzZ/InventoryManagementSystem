package com.example.inventorymanagementsystem.repository;

import com.example.inventorymanagementsystem.entity.OrderInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderInfoRepository extends JpaRepository<OrderInfo, Integer> {
}
