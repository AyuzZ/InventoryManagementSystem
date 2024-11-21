package com.example.inventorymanagementsystem.service;

import com.example.inventorymanagementsystem.entity.Product;
import com.example.inventorymanagementsystem.exceptions.ProductExistsException;
import com.example.inventorymanagementsystem.exceptions.ProductNotFoundException;
import com.example.inventorymanagementsystem.repository.ProductRepository;
import com.example.inventorymanagementsystem.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ProductServiceImplTests {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product testProduct;

    @BeforeEach
    public void setUp() {
        testProduct = new Product();
        testProduct.setPid(1);
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
    }

    @Test
    public void testCreateProduct_Success() {
        when(productRepository.getProductByName(testProduct.getName()))
                .thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        Product createdProduct = productService.createProduct(testProduct);

        assertNotNull(createdProduct);
        verify(productRepository).save(testProduct);
    }

    @Test
    public void testCreateProduct_ProductAlreadyExists() {
        when(productRepository.getProductByName(testProduct.getName()))
                .thenReturn(Optional.of(testProduct));

        assertThrows(ProductExistsException.class, () -> {
            productService.createProduct(testProduct);
        });
    }

    @Test
    public void testGetAvailableProductById_Success() {
        when(productRepository.getAvailableProductById(1))
                .thenReturn(Optional.of(testProduct));

        Product foundProduct = productService.getAvailableProductById(1);

        assertEquals(testProduct, foundProduct);
    }

    @Test
    public void testGetAvailableProductById_NotFound() {
        when(productRepository.getAvailableProductById(1))
                .thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> {
            productService.getAvailableProductById(1);
        });
    }

    @Test
    public void testGetProductById_Success() {
        when(productRepository.findById(1))
                .thenReturn(Optional.of(testProduct));

        Product foundProduct = productService.getProductById(1);

        assertEquals(testProduct, foundProduct);
    }

    @Test
    public void testGetProductById_NotFound() {
        when(productRepository.findById(1))
                .thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> {
            productService.getProductById(1);
        });
    }

    @Test
    public void testGetProductByName_Exists() {
        when(productRepository.getProductByName("Test Product"))
                .thenReturn(Optional.of(testProduct));

        Product foundProduct = productService.getProductByName("Test Product");

        assertEquals(testProduct, foundProduct);
    }

    @Test
    public void testGetProductByName_NotExists() {
        when(productRepository.getProductByName("Test Product"))
                .thenReturn(Optional.empty());

        Product foundProduct = productService.getProductByName("Test Product");

        assertNull(foundProduct);
    }

    @Test
    public void testGetProducts() {
        List<Product> products = Arrays.asList(testProduct, new Product());
        when(productRepository.findAll()).thenReturn(products);

        List<Product> retrievedProducts = productService.getProducts();

        assertEquals(2, retrievedProducts.size());
    }

    @Test
    public void testUpdateProduct() {
        when(productRepository.save(testProduct)).thenReturn(testProduct);

        Product updatedProduct = productService.updateProduct(testProduct);

        assertEquals(testProduct, updatedProduct);
        verify(productRepository).save(testProduct);
    }

    @Test
    public void testDeleteProduct() {
        when(productRepository.save(testProduct)).thenReturn(testProduct);

        Product deletedProduct = productService.deleteProduct(testProduct);

        assertTrue(deletedProduct.getName().endsWith("_deleted"));
        verify(productRepository).save(testProduct);
    }
}
