package com.example.inventorymanagementsystem.service.impl;

import com.example.inventorymanagementsystem.entity.Order;
import com.example.inventorymanagementsystem.repository.OrderRepository;
import com.example.inventorymanagementsystem.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public Order createOrder(Order order) {

        return null;
    }

    @Override
    public List<Order> getOrders() {
        return List.of();
    }

    @Override
    public Order getOrderById(int id) {
        return null;
    }
}
