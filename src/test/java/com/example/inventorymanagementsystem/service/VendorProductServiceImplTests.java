package com.example.inventorymanagementsystem.service;

import com.example.inventorymanagementsystem.entity.Product;
import com.example.inventorymanagementsystem.entity.Vendor;
import com.example.inventorymanagementsystem.entity.VendorProduct;
import com.example.inventorymanagementsystem.exceptions.EmptyCSVFileException;
import com.example.inventorymanagementsystem.repository.VendorProductRepository;
import com.example.inventorymanagementsystem.service.impl.VendorProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class VendorProductServiceImplTests {

    @Mock
    private VendorProductRepository vendorProductRepository;

    @InjectMocks
    private VendorProductServiceImpl vendorProductService;

    private VendorProduct vendorProduct;
    private Vendor vendor;
    private Product product;

    @BeforeEach
    public void setUp() {
        vendor = new Vendor();
        vendor.setVid(1);

        product = new Product();
        product.setPid(1);

        vendorProduct = new VendorProduct();
        vendorProduct.setVpId(1);
        vendorProduct.setVendor(vendor);
        vendorProduct.setProduct(product);
        vendorProduct.setStockQuantity(100);
        vendorProduct.setUnitPrice(10.0);
    }

    @Test
    public void testGetVendorProductByVidAndPidAndPrice_Exists() {
        when(vendorProductRepository.getVendorProductByVidAndPidAndPrice(
                anyInt(), anyInt(), anyDouble()))
                .thenReturn(Optional.of(vendorProduct));

        VendorProduct result = vendorProductService.getVendorProductByVidAndPidAndPrice(1, 1, 10.0);

        assertNotNull(result);
        assertEquals(vendorProduct, result);
    }

    @Test
    public void testGetVendorProductByVidAndPidAndPrice_NotExists() {
        when(vendorProductRepository.getVendorProductByVidAndPidAndPrice(
                anyInt(), anyInt(), anyDouble()))
                .thenReturn(Optional.empty());

        VendorProduct result = vendorProductService.getVendorProductByVidAndPidAndPrice(1, 1, 10.0);

        assertNull(result);
    }

    @Test
    public void testSaveVendorProduct() {
        when(vendorProductRepository.save(any(VendorProduct.class)))
                .thenReturn(vendorProduct);

        VendorProduct savedProduct = vendorProductService.saveVendorProduct(vendorProduct);

        assertNotNull(savedProduct);
        assertEquals(vendorProduct, savedProduct);
        verify(vendorProductRepository, times(1)).save(vendorProduct);
    }

    @Test
    public void testImportFromCSV_Success() throws IOException {
        String csvContent = "vpId,stockQuantity,unitPrice,product.pid,vendor.vid\n" +
                "1,100,10.0,1,1\n" +
                "2,200,20.0,2,2";
        MultipartFile mockFile = new MockMultipartFile(
                "file",
                "vendorproducts.csv",
                "text/csv",
                csvContent.getBytes()
        );

        assertDoesNotThrow(() -> vendorProductService.importFromCSV(mockFile));
        verify(vendorProductRepository, times(2)).save(any(VendorProduct.class));
    }

    @Test
    public void testImportFromCSV_EmptyFile() {
        MultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.csv",
                "text/csv",
                new byte[0]
        );

        assertThrows(EmptyCSVFileException.class, () ->
                vendorProductService.importFromCSV(emptyFile)
        );
    }

    @Test
    public void testExportToCSV() {
        List<VendorProduct> vendorProductList = Arrays.asList(
                vendorProduct
        );
        when(vendorProductRepository.findAll()).thenReturn(vendorProductList);

        String filePath = vendorProductService.exportToCSV();

        assertNotNull(filePath);
        assertEquals("vendorProducts.csv", filePath);
        verify(vendorProductRepository, times(1)).findAll();
    }

    @Test
    public void testExportToCSV_EmptyList() {
        when(vendorProductRepository.findAll()).thenReturn(Arrays.asList());

        String filePath = vendorProductService.exportToCSV();

        assertNotNull(filePath);
        assertEquals("vendorProducts.csv", filePath);
    }
}

