package com.example.inventorymanagementsystem.service;

import com.example.inventorymanagementsystem.entity.Order;

import java.util.List;

public interface OrderService {

    Order createOrder(Order order);

    List<Order> getOrders();

    Order getOrderById(int id);

}
