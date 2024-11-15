package com.example.inventorymanagementsystem.service.impl;

import com.example.inventorymanagementsystem.entity.Order;
import com.example.inventorymanagementsystem.entity.OrderInfo;
import com.example.inventorymanagementsystem.exceptions.OrderNotFoundException;
import com.example.inventorymanagementsystem.repository.OrderInfoRepository;
import com.example.inventorymanagementsystem.repository.OrderRepository;
import com.example.inventorymanagementsystem.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderInfoRepository orderInfoRepository;

    @Override
    @Transactional
    public Order createOrder(Order order, List<OrderInfo> orderInfoList) {
        Order createdOrder = orderRepository.save(order);
        for (OrderInfo orderInfo : orderInfoList){
            orderInfo.setOrder(createdOrder);
            orderInfoRepository.save(orderInfo);
        }
        return createdOrder;
    }

    @Override
    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Order getOrderById(int id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if(optionalOrder.isPresent()){
            return optionalOrder.get();
        }
        throw new OrderNotFoundException("Order Not Found.");
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrderByUserId(int userId) {
        List<Order> orderList = orderRepository.getOrderByUid(userId);
        if (orderList != null){
            return orderList;
        }
        throw new OrderNotFoundException("User hasn't created any orders.");
    }

}
