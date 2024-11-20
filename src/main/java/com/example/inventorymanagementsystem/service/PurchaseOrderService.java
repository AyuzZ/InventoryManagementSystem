package com.example.inventorymanagementsystem.service;

import com.example.inventorymanagementsystem.dto.PurchaseOrderRequestDTO;
import com.example.inventorymanagementsystem.dto.UpdateOrderStatusDTO;
import com.example.inventorymanagementsystem.entity.PurchaseOrder;

import java.util.List;

public interface PurchaseOrderService {

    PurchaseOrder createOrder(PurchaseOrderRequestDTO purchaseOrderRequestDTO);

    PurchaseOrder getOrderById(int id);

    List<PurchaseOrder> getOrders();

    void updateOrderStatus(PurchaseOrder purchaseOrder);

}
