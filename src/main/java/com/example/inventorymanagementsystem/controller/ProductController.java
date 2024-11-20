package com.example.inventorymanagementsystem.controller;

import com.example.inventorymanagementsystem.dto.ProductResponseDTO;
import com.example.inventorymanagementsystem.dto.ProductToVendorResponseDTO;
import com.example.inventorymanagementsystem.entity.Product;
import com.example.inventorymanagementsystem.entity.VendorProduct;
import com.example.inventorymanagementsystem.exceptions.ProductExistsException;
import com.example.inventorymanagementsystem.exceptions.ProductNotFoundException;
import com.example.inventorymanagementsystem.service.ProductService;
import com.example.inventorymanagementsystem.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/product/")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private VendorService vendorService;

//    Create Product
    @PostMapping()
    public ResponseEntity<?> createProduct(@RequestBody Product product){
        try {
            Product createdProduct = productService.createProduct(product);
        } catch (ProductExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Product Created.", HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getProductById(@PathVariable int id){
        try{
            Product product = productService.getProductById(id);

            List<ProductToVendorResponseDTO> productToVendorResponseDTOList = product.getVendorProductList().stream()
                    .map(vendorProduct -> ProductToVendorResponseDTO.builder()
                            .vpId(vendorProduct.getVpId())
                            .stockQuantity(vendorProduct.getStockQuantity())
                            .unitPrice(vendorProduct.getUnitPrice())
                            .vendorId(vendorProduct.getVendor().getVid())
                            .vendorName(vendorProduct.getVendor().getName())
                            .build())
                    .collect(Collectors.toList());

            ProductResponseDTO productResponseDTO = ProductResponseDTO.builder()
                    .pid(product.getPid())
                    .name(product.getName())
                    .description(product.getDescription())
                    .productToVendorResponseDTOList(productToVendorResponseDTOList)
                    .build();

            return new ResponseEntity<>(productResponseDTO, HttpStatus.OK);
        }catch (ProductNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping()
    public ResponseEntity<?> getProducts(){
        try{
            List<Product> productList = productService.getProducts();

            List<ProductResponseDTO> productResponseDTOList = productList.stream()
                    .map(product -> {
                        // Map VendorProduct list to ProductToVendorResponseDTO list using streams
                        List<ProductToVendorResponseDTO> productToVendorResponseDTOList = product.getVendorProductList().stream()
                                .map(vendorProduct -> ProductToVendorResponseDTO.builder()
                                        .vpId(vendorProduct.getVpId())
                                        .stockQuantity(vendorProduct.getStockQuantity())
                                        .unitPrice(vendorProduct.getUnitPrice())
                                        .vendorId(vendorProduct.getVendor().getVid())
                                        .vendorName(vendorProduct.getVendor().getName())
                                        .build())
                                .collect(Collectors.toList());

                        // Build ProductResponseDTO
                        return ProductResponseDTO.builder()
                                .pid(product.getPid())
                                .name(product.getName())
                                .description(product.getDescription())
                                .productToVendorResponseDTOList(productToVendorResponseDTOList)
                                .build();
                    })
                    .collect(Collectors.toList());
            return new ResponseEntity<>(productResponseDTOList, HttpStatus.OK);
        }catch (ProductNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

// update product
    @PutMapping("{id}")
    public ResponseEntity<?> updateProduct(@PathVariable int id, @RequestBody Product product){
        Product existingProduct;
        try {
            existingProduct = productService.getProductById(id);
        } catch (ProductNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        Product productWithName = productService.getProductByName(product.getName());
        if (productWithName != null)
            if (!productWithName.equals(existingProduct))
            return new ResponseEntity<>("Another product with that name already exists. Try a different name.", HttpStatus.BAD_REQUEST);

        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());

        try {
            Product updatedProduct = productService.updateProduct(existingProduct);
        } catch (Exception e) {
            return new ResponseEntity<>("Product Details Update Failed. Because of: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Product Details Updated.", HttpStatus.OK);
    }
}
