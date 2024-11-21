package com.example.inventorymanagementsystem.controller;

import org.springframework.boot.test.context.SpringBootTest;
import com.example.inventorymanagementsystem.entity.Product;
import com.example.inventorymanagementsystem.exceptions.ProductExistsException;
import com.example.inventorymanagementsystem.exceptions.ProductNotFoundException;
import com.example.inventorymanagementsystem.service.ProductService;
import com.example.inventorymanagementsystem.service.VendorProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.Mockito.*;

@SpringBootTest
public class ProductControllerTests {

    @Mock
    private ProductService productService;

    @Mock
    private VendorProductService vendorProductService;

    @InjectMocks
    private ProductController productController;

    private Product validProduct;

    @BeforeEach
    public void setUp() {
        validProduct = new Product();
        validProduct.setName("Test Product");
        validProduct.setDescription("Test Description");
    }

    @Test
    public void testCreateProduct_Success() throws ProductExistsException {
        // Arrange
        when(productService.createProduct(validProduct)).thenReturn(validProduct);

        // Act
        ResponseEntity<?> response = productController.createProduct(validProduct);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Product Created.", response.getBody());
        verify(productService).createProduct(validProduct);
    }

    @Test
    public void testCreateProduct_AlreadyExists() throws ProductExistsException {
        // Arrange
        when(productService.createProduct(validProduct)).thenThrow(new ProductExistsException("Product already exists"));

        // Act
        ResponseEntity<?> response = productController.createProduct(validProduct);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testGetProductById_Success() throws ProductNotFoundException {
        // Arrange
        int productId = 1;
        when(productService.getProductById(productId)).thenReturn(validProduct);

        // Act
        ResponseEntity<?> response = productController.getProductById(productId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testGetProductById_NotFound() throws ProductNotFoundException {
        // Arrange
        int productId = 1;
        when(productService.getProductById(productId)).thenThrow(new ProductNotFoundException("Product not found"));

        // Act
        ResponseEntity<?> response = productController.getProductById(productId);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testGetProducts_Success() throws ProductNotFoundException {
        // Arrange
        List<Product> products = Arrays.asList(validProduct);
        when(productService.getProducts()).thenReturn(products);

        // Act
        ResponseEntity<?> response = productController.getProducts();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testUpdateProduct_Success() throws ProductNotFoundException {
        // Arrange
        int productId = 1;
        Product existingProduct = new Product();
        existingProduct.setPid(productId);
        existingProduct.setName("Old Product");
        existingProduct.setDescription("Old Description");

        when(productService.getProductById(productId)).thenReturn(existingProduct);
        when(productService.getProductByName(validProduct.getName())).thenReturn(null);
        when(productService.updateProduct(existingProduct)).thenReturn(validProduct);

        // Act
        ResponseEntity<?> response = productController.updateProduct(productId, validProduct);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product Details Updated.", response.getBody());
    }

    @Test
    public void testUpdateProduct_DuplicateName() throws ProductNotFoundException {
        // Arrange
        int productId = 1;
        Product existingProduct = new Product();
        existingProduct.setPid(productId);
        existingProduct.setName("Old Product");
        existingProduct.setDescription("Old Description");

        Product duplicateProduct = new Product();
        duplicateProduct.setName("Duplicate Product");
        duplicateProduct.setPid(2);

        when(productService.getProductById(productId)).thenReturn(existingProduct);
        when(productService.getProductByName(validProduct.getName())).thenReturn(duplicateProduct);

        // Act
        ResponseEntity<?> response = productController.updateProduct(productId, validProduct);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Another product with that name already exists. Try a different name.", response.getBody());
    }

    @Test
    public void testImportProductsFromCSV_Success() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "products.csv",
                "text/csv",
                "test,data".getBytes()
        );
        doNothing().when(productService).importFromCSV(file);

        // Act
        ResponseEntity<?> response = productController.importProductsFromCSV(file);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Products imported successfully.", response.getBody());
    }

    @Test
    public void testImportProductsFromCSV_EmptyFile() {
        // Arrange
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "products.csv",
                "text/csv",
                new byte[0]
        );

        // Act
        ResponseEntity<?> response = productController.importProductsFromCSV(emptyFile);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("File is empty. Please upload a valid CSV file.", response.getBody());
    }

    @Test
    public void testExportProductsToCSV_Success() throws Exception {
        // Arrange
        String filePath = "/tmp/products.csv";
        when(productService.exportToCSV()).thenReturn(filePath);

        // Act
        ResponseEntity<org.springframework.core.io.Resource> response = productController.exportProductsToCSV();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testImportVendorProductsFromCSV_Success() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "vendor_products.csv",
                "text/csv",
                "test,data".getBytes()
        );
        doNothing().when(vendorProductService).importFromCSV(file);

        // Act
        ResponseEntity<?> response = productController.importVendorProductsFromCSV(file);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Products imported successfully.", response.getBody());
    }

    @Test
    public void testExportVendorProductsToCSV_Success() throws Exception {
        // Arrange
        String filePath = "/tmp/vendor_products.csv";
        when(vendorProductService.exportToCSV()).thenReturn(filePath);

        // Act
        ResponseEntity<org.springframework.core.io.Resource> response = productController.exportVendorProductsToCSV();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
