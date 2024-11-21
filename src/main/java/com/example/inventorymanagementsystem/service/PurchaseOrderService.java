package com.example.inventorymanagementsystem.service;

import com.example.inventorymanagementsystem.dto.PurchaseOrderRequestDTO;
import com.example.inventorymanagementsystem.entity.PurchaseOrder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PurchaseOrderService {

    PurchaseOrder createOrder(PurchaseOrderRequestDTO purchaseOrderRequestDTO);

    PurchaseOrder getOrderById(int id);

    List<PurchaseOrder> getOrders();

    void updateOrderStatus(PurchaseOrder purchaseOrder);

    void importFromCSV(MultipartFile file);

    String exportToCSV();

}
