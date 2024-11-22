package com.example.inventorymanagementsystem.service.impl;

import com.example.inventorymanagementsystem.dto.OrderCSVRepresentation;
import com.example.inventorymanagementsystem.dto.PurchaseOrderRequestDTO;
import com.example.inventorymanagementsystem.entity.Product;
import com.example.inventorymanagementsystem.entity.PurchaseOrder;
import com.example.inventorymanagementsystem.entity.Vendor;
import com.example.inventorymanagementsystem.entity.VendorProduct;
import com.example.inventorymanagementsystem.exceptions.EmptyCSVFileException;
import com.example.inventorymanagementsystem.exceptions.OrderNotFoundException;
import com.example.inventorymanagementsystem.exceptions.PurchaseOrderExistsException;
import com.example.inventorymanagementsystem.repository.PurchaseOrderRepository;
import com.example.inventorymanagementsystem.service.PurchaseOrderService;
import com.example.inventorymanagementsystem.service.VendorProductService;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

//    @Override
//    public void importFromCSV(MultipartFile file) {
//        if (file.isEmpty()) {
//            throw new EmptyCSVFileException("CSV file is empty.");
//        }
//
//        try (Reader reader = new BufferedReader(
//                new InputStreamReader(file.getInputStream()))) {
//
//            HeaderColumnNameMappingStrategy<OrderCSVRepresentation> strategy =
//                    new HeaderColumnNameMappingStrategy<>();
//            strategy.setType(OrderCSVRepresentation.class);
//
//            CsvToBean<OrderCSVRepresentation> csvToBean = new CsvToBeanBuilder<OrderCSVRepresentation>(reader)
//                    .withMappingStrategy(strategy)
//                    .withIgnoreLeadingWhiteSpace(true)
//                    .withIgnoreEmptyLine(true)
//                    .build();
//
//            List<PurchaseOrder> purchaseOrders = csvToBean.parse()
//                    .stream()
//                    .map(csvLine -> PurchaseOrder.builder()
//                                .product(Product.builder().pid(csvLine.getPid()).build())
//                                .vendor(Vendor.builder().vid(csvLine.getVid()).build())
//                                .orderDate(csvLine.getOrderDate())
//                                .orderQuantity(csvLine.getOrderQuantity())
//                                .orderStatus(csvLine.getOrderStatus())
//                                .orderTotal(csvLine.getOrderTotal())
//                                .unitPrice(csvLine.getUnitPrice())
//                                .build()
//                    )
//                    .collect(Collectors.toList());
//
//            purchaseOrders.forEach(purchaseOrder -> {
//                try {
//                    purchaseOrderRepository.save(purchaseOrder);
//                } catch (PurchaseOrderExistsException e) {
//                    throw new RuntimeException("Error processing CSV data: " + e.getMessage(), e);
//                }
//            });
//
//        } catch (IOException e) {
//            throw new RuntimeException("Error reading CSV file: " + e.getMessage(), e);
//        } catch (RuntimeException e) {
//            throw new RuntimeException("Error processing CSV data: " + e.getMessage(), e);
//        }
//    }

    @Override
    public String exportToCSV() {
        List<PurchaseOrder> purchaseOrderList = purchaseOrderRepository.findAll();
        String filePath = "purchaseOrders.csv";

        try(CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {

            String[] header = {"Id", "OrderDate", "OrderTotal", "OrderQuantity",
                    "OrderStatus", "UnitPrice", "Product ID", "Vendor ID"};
            writer.writeNext(header);

            for(PurchaseOrder purchaseOrder : purchaseOrderList) {
                String[] data = { String.valueOf(purchaseOrder.getOid()),
                        String.valueOf(purchaseOrder.getOrderDate()),
                        String.valueOf(purchaseOrder.getOrderTotal()),
                        String.valueOf(purchaseOrder.getOrderQuantity()),
                        String.valueOf(purchaseOrder.getOrderStatus()),
                        String.valueOf(purchaseOrder.getUnitPrice()),
                        String.valueOf(purchaseOrder.getProduct().getPid()),
                        String.valueOf(purchaseOrder.getVendor().getVid())
                };
                writer.writeNext(data);
            }

        } catch (Exception ex) {
            throw new RuntimeException("Error :" + ex.getMessage(), ex);
        }
        return filePath;
    }

}
