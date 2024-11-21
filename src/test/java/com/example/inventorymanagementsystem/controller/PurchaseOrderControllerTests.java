package com.example.inventorymanagementsystem.controller;

import com.example.inventorymanagementsystem.dto.PurchaseOrderRequestDTO;
import com.example.inventorymanagementsystem.dto.UpdateOrderStatusDTO;
import com.example.inventorymanagementsystem.entity.Product;
import com.example.inventorymanagementsystem.entity.PurchaseOrder;
import com.example.inventorymanagementsystem.entity.Vendor;
import com.example.inventorymanagementsystem.exceptions.OrderNotFoundException;
import com.example.inventorymanagementsystem.exceptions.ProductNotFoundException;
import com.example.inventorymanagementsystem.exceptions.VendorNotFoundException;
import com.example.inventorymanagementsystem.service.ProductService;
import com.example.inventorymanagementsystem.service.PurchaseOrderService;
import com.example.inventorymanagementsystem.service.VendorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class PurchaseOrderControllerTests {

    @Mock
    private PurchaseOrderService purchaseOrderService;

    @Mock
    private ProductService productService;

    @Mock
    private VendorService vendorService;

    @InjectMocks
    private PurchaseOrderController purchaseOrderController;

    private PurchaseOrderRequestDTO validPurchaseOrderRequestDTO;
    private Product validProduct;
    private Vendor validVendor;

    @BeforeEach
    public void setUp() {
        validProduct = new Product();
        validProduct.setPid(1);
        validProduct.setName("Test Product");

        validVendor = new Vendor();
        validVendor.setVid(1);
        validVendor.setName("Test Vendor");

        validPurchaseOrderRequestDTO = new PurchaseOrderRequestDTO();
        validPurchaseOrderRequestDTO.setOrderStatus("pending");
        validPurchaseOrderRequestDTO.setUnitPrice(10.50);
        validPurchaseOrderRequestDTO.setOrderQuantity(5);
        validPurchaseOrderRequestDTO.setProduct(validProduct);
        validPurchaseOrderRequestDTO.setVendor(validVendor);
    }

    @Test
    public void testCreatePurchaseOrder_Success() throws ProductNotFoundException, VendorNotFoundException {
        // Arrange
        when(productService.getAvailableProductById(validProduct.getPid())).thenReturn(validProduct);
        when(vendorService.getAvailableVendorById(validVendor.getVid())).thenReturn(validVendor);

        PurchaseOrder createdOrder = new PurchaseOrder();
        when(purchaseOrderService.createOrder(validPurchaseOrderRequestDTO)).thenReturn(createdOrder);

        // Act
        ResponseEntity<?> response = purchaseOrderController.createPurchaseOrder(validPurchaseOrderRequestDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Order Created!", response.getBody());
    }

    @Test
    public void testCreatePurchaseOrder_InvalidOrderStatus() {
        // Arrange
        validPurchaseOrderRequestDTO.setOrderStatus("invalid");

        // Act
        ResponseEntity<?> response = purchaseOrderController.createPurchaseOrder(validPurchaseOrderRequestDTO);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("order status must be either \"delivered\" or \"pending\".", response.getBody());
    }

    @Test
    public void testCreatePurchaseOrder_InvalidUnitPrice() {
        // Arrange
        validPurchaseOrderRequestDTO.setUnitPrice(0.0);

        // Act
        ResponseEntity<?> response = purchaseOrderController.createPurchaseOrder(validPurchaseOrderRequestDTO);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Unit price and order quantity can't be 0.", response.getBody());
    }

    @Test
    public void testCreatePurchaseOrder_InvalidOrderQuantity() {
        // Arrange
        validPurchaseOrderRequestDTO.setOrderQuantity(0);

        // Act
        ResponseEntity<?> response = purchaseOrderController.createPurchaseOrder(validPurchaseOrderRequestDTO);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Unit price and order quantity can't be 0.", response.getBody());
    }

    @Test
    public void testCreatePurchaseOrder_ProductNotFound() throws ProductNotFoundException {
        // Arrange
        when(productService.getAvailableProductById(validProduct.getPid()))
                .thenThrow(new ProductNotFoundException("Product not found"));

        // Act
        ResponseEntity<?> response = purchaseOrderController.createPurchaseOrder(validPurchaseOrderRequestDTO);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testCreatePurchaseOrder_VendorNotFound() throws ProductNotFoundException, VendorNotFoundException {
        // Arrange
        when(productService.getAvailableProductById(validProduct.getPid())).thenReturn(validProduct);
        when(vendorService.getAvailableVendorById(validVendor.getVid()))
                .thenThrow(new VendorNotFoundException("Vendor not found"));

        // Act
        ResponseEntity<?> response = purchaseOrderController.createPurchaseOrder(validPurchaseOrderRequestDTO);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testGetPurchaseOrderById_Success() throws OrderNotFoundException {
        // Arrange
        int orderId = 1;
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setOid(orderId);
        purchaseOrder.setOrderStatus("pending");
        purchaseOrder.setOrderQuantity(5);
        purchaseOrder.setUnitPrice(10.50);
        purchaseOrder.setProduct(validProduct);
        purchaseOrder.setVendor(validVendor);
        purchaseOrder.setOrderDate(new Date());

        when(purchaseOrderService.getOrderById(orderId)).thenReturn(purchaseOrder);

        // Act
        ResponseEntity<?> response = purchaseOrderController.getPurchaseOrderById(orderId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testGetPurchaseOrderById_NotFound() throws OrderNotFoundException {
        // Arrange
        int orderId = 1;
        when(purchaseOrderService.getOrderById(orderId))
                .thenThrow(new OrderNotFoundException("Order not found"));

        // Act
        ResponseEntity<?> response = purchaseOrderController.getPurchaseOrderById(orderId);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testGetPurchaseOrders_Success() throws OrderNotFoundException {
        // Arrange
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setProduct(validProduct);
        purchaseOrder.setVendor(validVendor);

        List<PurchaseOrder> purchaseOrders = Arrays.asList(purchaseOrder);
        when(purchaseOrderService.getOrders()).thenReturn(purchaseOrders);

        // Act
        ResponseEntity<?> response = purchaseOrderController.getPurchaseOrders();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testUpdateOrderStatus_Success() throws OrderNotFoundException {
        // Arrange
        int orderId = 1;
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setOrderStatus("pending");

        UpdateOrderStatusDTO updateOrderStatusDTO = new UpdateOrderStatusDTO();
        updateOrderStatusDTO.setUpdateStatusToDeliver(true);

        when(purchaseOrderService.getOrderById(orderId)).thenReturn(purchaseOrder);
        doNothing().when(purchaseOrderService).updateOrderStatus(purchaseOrder);

        // Act
        ResponseEntity<?> response = purchaseOrderController.updateOrderStatus(orderId, updateOrderStatusDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Order Delivered! Corresponding record in Vendor_Product Table has also been updated.", response.getBody());
    }

    @Test
    public void testUpdateOrderStatus_AlreadyDelivered() throws OrderNotFoundException {
        // Arrange
        int orderId = 1;
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setOrderStatus("delivered");

        UpdateOrderStatusDTO updateOrderStatusDTO = new UpdateOrderStatusDTO();
        updateOrderStatusDTO.setUpdateStatusToDeliver(true);

        when(purchaseOrderService.getOrderById(orderId)).thenReturn(purchaseOrder);

        // Act
        ResponseEntity<?> response = purchaseOrderController.updateOrderStatus(orderId, updateOrderStatusDTO);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Order has already been delivered.", response.getBody());
    }

    @Test
    public void testUpdateOrderStatus_NoDeliveryUpdate() {
        // Arrange
        int orderId = 1;
        UpdateOrderStatusDTO updateOrderStatusDTO = new UpdateOrderStatusDTO();
        updateOrderStatusDTO.setUpdateStatusToDeliver(false);

        // Act
        ResponseEntity<?> response = purchaseOrderController.updateOrderStatus(orderId, updateOrderStatusDTO);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("No need for changes while its not delivered.", response.getBody());
    }

    @Test
    public void testImportOrdersFromCSV_Success() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "orders.csv",
                "text/csv",
                "test,data".getBytes()
        );
        doNothing().when(purchaseOrderService).importFromCSV(file);

        // Act
        ResponseEntity<?> response = purchaseOrderController.importOrdersFromCSV(file);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Products imported successfully.", response.getBody());
    }

    @Test
    public void testImportOrdersFromCSV_EmptyFile() {
        // Arrange
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "orders.csv",
                "text/csv",
                new byte[0]
        );

        // Act
        ResponseEntity<?> response = purchaseOrderController.importOrdersFromCSV(emptyFile);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("File is empty. Please upload a valid CSV file.", response.getBody());
    }

    @Test
    public void testExportOrdersToCSV_Success() throws Exception {
        // Arrange
        String filePath = "/tmp/orders.csv";
        when(purchaseOrderService.exportToCSV()).thenReturn(filePath);

        // Act
        ResponseEntity<org.springframework.core.io.Resource> response = purchaseOrderController.exportOrdersToCSV();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}

