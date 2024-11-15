package com.example.inventorymanagementsystem.service;

import com.example.inventorymanagementsystem.entity.Order;
import com.example.inventorymanagementsystem.entity.OrderInfo;

import java.util.List;

public interface OrderService {

    Order createOrder(Order order, List<OrderInfo> orderInfoList);

    List<Order> getOrders();

    Order getOrderById(int id);

    List<Order> getOrderByUserId(int userId);

}
