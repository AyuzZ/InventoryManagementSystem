package com.example.inventorymanagementsystem.controller;

import com.example.inventorymanagementsystem.dto.*;
import com.example.inventorymanagementsystem.entity.*;
import com.example.inventorymanagementsystem.exceptions.OrderNotFoundException;
import com.example.inventorymanagementsystem.exceptions.ProductNotFoundException;
import com.example.inventorymanagementsystem.exceptions.VendorExistsException;
import com.example.inventorymanagementsystem.exceptions.VendorNotFoundException;
import com.example.inventorymanagementsystem.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order/")
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private VendorService vendorService;

    @PostMapping()
    public ResponseEntity<?> createPurchaseOrder(@RequestBody PurchaseOrderRequestDTO purchaseOrderRequestDTO){

        String orderStatus = purchaseOrderRequestDTO.getOrderStatus().toLowerCase();
        if (!(orderStatus.equals("delivered") || orderStatus.equals("pending"))) {
            return new ResponseEntity<>("order status must be either \"delivered\" or \"pending\".", HttpStatus.BAD_REQUEST);
        }
        purchaseOrderRequestDTO.setOrderStatus(orderStatus);

        try {
            purchaseOrderRequestDTO.setUnitPrice(Double.valueOf(purchaseOrderRequestDTO.getUnitPrice())); ;
        } catch (NumberFormatException e) {
            return new ResponseEntity<>("Unit Price Must Only Contain Numbers.", HttpStatus.BAD_REQUEST);
        }

        try {
            purchaseOrderRequestDTO.setOrderQuantity(Integer.valueOf(purchaseOrderRequestDTO.getOrderQuantity()));
        } catch (NumberFormatException e) {
            return new ResponseEntity<>("Order Quantity must be in integer.", HttpStatus.BAD_REQUEST);
        }

        try {
            Product orderedProductDB = productService.getAvailableProductById(purchaseOrderRequestDTO.getProduct().getPid());
            purchaseOrderRequestDTO.setProduct(orderedProductDB);
        } catch (ProductNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        try {
            Vendor supplyingVendorDB = vendorService.getAvailableVendorById(purchaseOrderRequestDTO.getVendor().getVid());
            purchaseOrderRequestDTO.setVendor(supplyingVendorDB);
        } catch (VendorNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        try {
            purchaseOrderService.createOrder(purchaseOrderRequestDTO);
        } catch (VendorNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("Order Created!", HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getPurchaseOrderById(@PathVariable int id){
        try {
            PurchaseOrder purchaseOrder = purchaseOrderService.getOrderById(id);
            PurchaseOrderResponseDTO purchaseOrderResponseDTO = PurchaseOrderResponseDTO.builder()
                    .oid(purchaseOrder.getOid())
                    .orderDate(purchaseOrder.getOrderDate())
                    .orderStatus(purchaseOrder.getOrderStatus())
                    .orderQuantity(purchaseOrder.getOrderQuantity())
                    .unitPrice(purchaseOrder.getUnitPrice())
                    .orderTotal(purchaseOrder.getOrderTotal())
                    .productId(purchaseOrder.getProduct().getPid())
                    .productName(purchaseOrder.getProduct().getName())
                    .vendorId(purchaseOrder.getVendor().getVid())
                    .vendorName(purchaseOrder.getVendor().getName())
                    .build();
            return new ResponseEntity<>(purchaseOrderResponseDTO, HttpStatus.OK);
        } catch (OrderNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping()
    public ResponseEntity<?> getPurchaseOrders(){
        try {
            List<PurchaseOrder> purchaseOrderList = purchaseOrderService.getOrders();

            List<PurchaseOrderResponseDTO> purchaseOrderResponseDTOList = purchaseOrderList.stream()
                    .map(purchaseOrder -> PurchaseOrderResponseDTO.builder()
                            .oid(purchaseOrder.getOid())
                            .orderDate(purchaseOrder.getOrderDate())
                            .orderStatus(purchaseOrder.getOrderStatus())
                            .orderQuantity(purchaseOrder.getOrderQuantity())
                            .unitPrice(purchaseOrder.getUnitPrice())
                            .orderTotal(purchaseOrder.getOrderTotal())
                            .productId(purchaseOrder.getProduct().getPid())
                            .productName(purchaseOrder.getProduct().getName())
                            .vendorId(purchaseOrder.getVendor().getVid())
                            .vendorName(purchaseOrder.getVendor().getName())
                            .build())
                    .collect(Collectors.toList());

            return new ResponseEntity<>(purchaseOrderResponseDTOList, HttpStatus.OK);
        } catch (OrderNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateOrderStatus(@PathVariable int id, @RequestBody UpdateOrderStatusDTO updateOrderStatusDTO){

         if (!updateOrderStatusDTO.isUpdateStatusToDeliver()){
            return new ResponseEntity<>("No need for changes while its not delivered.", HttpStatus.BAD_REQUEST);
        }

        PurchaseOrder purchaseOrder;
        try {
            purchaseOrder = purchaseOrderService.getOrderById(id);
        } catch (OrderNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        if (purchaseOrder.getOrderStatus().equals("delivered")){
            return new ResponseEntity<>("Order has already been delivered.", HttpStatus.BAD_REQUEST);
        }
        purchaseOrderService.updateOrderStatus(purchaseOrder);

        return new ResponseEntity<>("Order Delivered! Corresponding record in Vendor_Product Table has also been updated.", HttpStatus.CREATED);
    }

}
