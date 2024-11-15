package com.example.inventorymanagementsystem.repository;

import com.example.inventorymanagementsystem.entity.Order;
import com.example.inventorymanagementsystem.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Query("SELECT o FROM Order o WHERE o.user.uid = ?1")
    public List<Order> getOrderByUid(int uid);
}
