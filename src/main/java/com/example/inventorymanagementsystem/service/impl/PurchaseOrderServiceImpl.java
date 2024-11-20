package com.example.inventorymanagementsystem.service.impl;

import com.example.inventorymanagementsystem.dto.PurchaseOrderRequestDTO;
import com.example.inventorymanagementsystem.dto.UpdateOrderStatusDTO;
import com.example.inventorymanagementsystem.entity.PurchaseOrder;
import com.example.inventorymanagementsystem.entity.VendorProduct;
import com.example.inventorymanagementsystem.exceptions.OrderNotFoundException;
import com.example.inventorymanagementsystem.repository.PurchaseOrderRepository;
import com.example.inventorymanagementsystem.service.PurchaseOrderService;
import com.example.inventorymanagementsystem.service.VendorProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private VendorProductService vendorProductService;

    @Override
    @Transactional
    public PurchaseOrder createOrder(PurchaseOrderRequestDTO purchaseOrderRequestDTO) {

        //Purchase Order
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setOrderDate(new Date());
        purchaseOrder.setOrderQuantity(purchaseOrderRequestDTO.getOrderQuantity());
        purchaseOrder.setUnitPrice(purchaseOrderRequestDTO.getUnitPrice());
        purchaseOrder.setOrderStatus(purchaseOrderRequestDTO.getOrderStatus());
        purchaseOrder.setProduct(purchaseOrderRequestDTO.getProduct());
        purchaseOrder.setVendor(purchaseOrderRequestDTO.getVendor());
        Double orderTotal = purchaseOrderRequestDTO.getOrderQuantity() * purchaseOrderRequestDTO.getUnitPrice();
        purchaseOrder.setOrderTotal(orderTotal);

        //Saving purchase order
        PurchaseOrder createdPurchaseOrder = purchaseOrderRepository.save(purchaseOrder);

        //Saving vendor product or updating its stock qty.
        VendorProduct existingVendorProduct = vendorProductService.getVendorProductByVidAndPidAndPrice(
                purchaseOrder.getVendor().getVid(), purchaseOrder.getProduct().getPid(), purchaseOrder.getUnitPrice());
        //if the record doesn't exist (record with the given vendor, product and exact price) we create a new record.
        if (existingVendorProduct == null){
            VendorProduct newVendorProduct = VendorProduct.builder()
                    .product(purchaseOrder.getProduct())
                    .vendor(purchaseOrder.getVendor())
                    .stockQuantity(0)
                    .unitPrice(purchaseOrderRequestDTO.getUnitPrice())
                    .build();
            if (purchaseOrder.getOrderStatus().equals("delivered"))
                newVendorProduct.setStockQuantity(purchaseOrder.getOrderQuantity());
            vendorProductService.saveVendorProduct(newVendorProduct);
        } else if (purchaseOrder.getOrderStatus().equals("delivered")) {
            existingVendorProduct.setStockQuantity(existingVendorProduct.getStockQuantity() + purchaseOrder.getOrderQuantity());
            vendorProductService.saveVendorProduct(existingVendorProduct);
        }
        return createdPurchaseOrder;
    }

//    @Override
//    public List<PurchaseOrder> getOrders() {
//        return purchaseOrderRepository.findAll();
//    }

    @Override
    public PurchaseOrder getOrderById(int id) {
        Optional<PurchaseOrder> optionalOrder = purchaseOrderRepository.findById(id);
        if(optionalOrder.isPresent()){
            return optionalOrder.get();
        }
        throw new OrderNotFoundException("Order Not Found.");
    }

    @Override
    public List<PurchaseOrder> getOrders() {
        List<PurchaseOrder> orderList = purchaseOrderRepository.findAll();
        return orderList;
    }

    @Override
    @Transactional
    public void updateOrderStatus(PurchaseOrder purchaseOrder){

        purchaseOrder.setOrderStatus("delivered");
        purchaseOrderRepository.save(purchaseOrder);

        // updating its stock qty.
        VendorProduct existingVendorProduct = vendorProductService.getVendorProductByVidAndPidAndPrice(
                purchaseOrder.getVendor().getVid(), purchaseOrder.getProduct().getPid(), purchaseOrder.getUnitPrice());

        existingVendorProduct.setStockQuantity
                (existingVendorProduct.getStockQuantity() + purchaseOrder.getOrderQuantity());

        vendorProductService.saveVendorProduct(existingVendorProduct);
    }

}
