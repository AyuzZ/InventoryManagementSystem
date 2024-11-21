package com.example.inventorymanagementsystem.service;

import com.example.inventorymanagementsystem.dto.PurchaseOrderRequestDTO;
import com.example.inventorymanagementsystem.entity.Product;
import com.example.inventorymanagementsystem.entity.PurchaseOrder;
import com.example.inventorymanagementsystem.entity.Vendor;
import com.example.inventorymanagementsystem.entity.VendorProduct;
import com.example.inventorymanagementsystem.exceptions.OrderNotFoundException;
import com.example.inventorymanagementsystem.repository.PurchaseOrderRepository;
import com.example.inventorymanagementsystem.service.impl.PurchaseOrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class PurchaseOrderServiceImplTests {

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

    @Mock
    private VendorProductService vendorProductService;

    @InjectMocks
    private PurchaseOrderServiceImpl purchaseOrderService;

    private PurchaseOrder testOrder;
    private PurchaseOrderRequestDTO testOrderRequestDTO;
    private Vendor testVendor;
    private Product testProduct;

    @BeforeEach
    public void setUp() {
        testVendor = new Vendor();
        testVendor.setVid(1);
        testVendor.setName("Test Vendor");

        testProduct = new Product();
        testProduct.setPid(1);
        testProduct.setName("Test Product");

        testOrderRequestDTO = new PurchaseOrderRequestDTO();
        testOrderRequestDTO.setOrderQuantity(10);
        testOrderRequestDTO.setUnitPrice(100.0);
        testOrderRequestDTO.setOrderStatus("pending");
        testOrderRequestDTO.setProduct(testProduct);
        testOrderRequestDTO.setVendor(testVendor);

        testOrder = new PurchaseOrder();
        testOrder.setOid(1);
        testOrder.setOrderQuantity(10);
        testOrder.setUnitPrice(100.0);
        testOrder.setOrderStatus("pending");
        testOrder.setProduct(testProduct);
        testOrder.setVendor(testVendor);
    }

    @Test
    public void testCreateOrder_NewVendorProduct() {
        when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenReturn(testOrder);
        when(vendorProductService.getVendorProductByVidAndPidAndPrice(
                testVendor.getVid(), testProduct.getPid(), testOrderRequestDTO.getUnitPrice()))
                .thenReturn(null);

        PurchaseOrder createdOrder = purchaseOrderService.createOrder(testOrderRequestDTO);

        assertNotNull(createdOrder);
        verify(purchaseOrderRepository).save(any(PurchaseOrder.class));
        verify(vendorProductService).saveVendorProduct(any(VendorProduct.class));
    }

    @Test
    public void testCreateOrder_ExistingVendorProduct_DeliveredStatus() {
        testOrderRequestDTO.setOrderStatus("delivered");
        VendorProduct existingVendorProduct = VendorProduct.builder()
                .stockQuantity(5)
                .build();

        when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenReturn(testOrder);
        when(vendorProductService.getVendorProductByVidAndPidAndPrice(
                testVendor.getVid(), testProduct.getPid(), testOrderRequestDTO.getUnitPrice()))
                .thenReturn(existingVendorProduct);

        PurchaseOrder createdOrder = purchaseOrderService.createOrder(testOrderRequestDTO);

        assertNotNull(createdOrder);
        verify(purchaseOrderRepository).save(any(PurchaseOrder.class));
        verify(vendorProductService).saveVendorProduct(existingVendorProduct);
    }

    @Test
    public void testGetOrderById_Success() {
        when(purchaseOrderRepository.findById(1))
                .thenReturn(Optional.of(testOrder));

        PurchaseOrder foundOrder = purchaseOrderService.getOrderById(1);

        assertEquals(testOrder, foundOrder);
    }

    @Test
    public void testGetOrderById_NotFound() {
        when(purchaseOrderRepository.findById(1))
                .thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> {
            purchaseOrderService.getOrderById(1);
        });
    }

    @Test
    public void testGetOrders() {
        List<PurchaseOrder> orders = Arrays.asList(testOrder, new PurchaseOrder());
        when(purchaseOrderRepository.findAll()).thenReturn(orders);

        List<PurchaseOrder> retrievedOrders = purchaseOrderService.getOrders();

        assertEquals(2, retrievedOrders.size());
    }

    @Test
    public void testUpdateOrderStatus() {
        VendorProduct existingVendorProduct = VendorProduct.builder()
                .stockQuantity(5)
                .build();

        when(vendorProductService.getVendorProductByVidAndPidAndPrice(
                testOrder.getVendor().getVid(),
                testOrder.getProduct().getPid(),
                testOrder.getUnitPrice()))
                .thenReturn(existingVendorProduct);

        purchaseOrderService.updateOrderStatus(testOrder);

        assertEquals("delivered", testOrder.getOrderStatus());
        verify(purchaseOrderRepository).save(testOrder);
        verify(vendorProductService).saveVendorProduct(existingVendorProduct);
        assertEquals(15, existingVendorProduct.getStockQuantity());
    }
}
