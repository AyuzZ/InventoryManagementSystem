package com.example.inventorymanagementsystem.controller;

import com.example.inventorymanagementsystem.entity.Product;
import com.example.inventorymanagementsystem.entity.Role;
import com.example.inventorymanagementsystem.entity.Vendor;
import com.example.inventorymanagementsystem.exceptions.ProductExistsException;
import com.example.inventorymanagementsystem.exceptions.ProductNotFoundException;
import com.example.inventorymanagementsystem.exceptions.RoleNotFoundException;
import com.example.inventorymanagementsystem.exceptions.VendorNotFoundException;
import com.example.inventorymanagementsystem.service.ProductService;
import com.example.inventorymanagementsystem.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products/")
public class ProductController {
    
    @Autowired
    private ProductService productService;

    @Autowired
    private VendorService vendorService;

//    Create Product
    @PostMapping()
    public ResponseEntity<?> createProduct(@RequestBody Product product){

        // Check if the vendor exists, if it exists, set it as the vendor of the product else throw exception.
        Vendor existingVendor;
        try {
            existingVendor = vendorService.getVendorById(product.getVendor().getVid());
        }catch (VendorNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        //Adding vendor to product
        product.setVendor(existingVendor);

        try {
            Product createdProduct = productService.createProduct(product);
        } catch (ProductExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Product Created.", HttpStatus.CREATED);
    }


//    Get Product Endpoints
    @GetMapping()
    public ResponseEntity<?> getProducts(){
        try{
            List<Product> productList = productService.getProducts();
            return new ResponseEntity<>(productList, HttpStatus.OK);
        } catch (ProductNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getProductById(@PathVariable int id){
        try{
            Product product = productService.getProductById(id);
            return new ResponseEntity<>(product, HttpStatus.OK);
        }catch (ProductNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

//    @GetMapping("{productName}")
//    public ResponseEntity<?> getProductsByName(@PathVariable String productName){
//        try {
//            List<Product> productList = productService.getProductsByName(productName);
//            return new ResponseEntity<>(productList, HttpStatus.OK);
//        } catch (ProductNotFoundException e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
//        }
//    }
//
//    @GetMapping("{price}")
//    public ResponseEntity<?> getProductsByPrice(@PathVariable Double price){
//        try {
//            List<Product> productList = productService.getProductsByPrice(price);
//            return new ResponseEntity<>(productList, HttpStatus.OK);
//        } catch (ProductNotFoundException e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
//        }
//    }

//    @GetMapping("{productName&price}")
//    public ResponseEntity<?> getProductsByName(@PathVariable String productName, @PathVariable Double price){
//        List<Product> productList = productService.getProductByNameAndPrice(productName, price);
//        return new ResponseEntity<>(productList, HttpStatus.OK);
//    }


// update product
    @PutMapping("{id}")
    public ResponseEntity<?> updateProduct(@PathVariable int id, @RequestBody Product product){
        Product existingProduct;
        try {
            existingProduct = productService.getProductById(id);
        } catch (ProductNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setUnitPrice(product.getUnitPrice());
        existingProduct.setStockQuantity(product.getStockQuantity());
        //Setting vendor
        // Check if the vendor exists, if it exists, set it as the vendor of the product else throw exception.
        Vendor existingVendor;
        try {
            existingVendor = vendorService.getVendorById(product.getVendor().getVid());
        }catch (VendorNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        existingProduct.setVendor(existingVendor);

        try {
            Product updatedProduct = productService.updateProduct(existingProduct);
        } catch (Exception e) {
            return new ResponseEntity<>("Product Details Update Failed. Because of: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Product Details Updated.", HttpStatus.OK);
    }

//    delete product
    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable int id) {

        Product product;

        //Checking if product exists.
        try{
            product = productService.getProductById(id);
        }catch (ProductNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        //Clearing product from the arrayList products.
        Vendor vendor = product.getVendor();
        vendor.getProducts().clear();

        //Deleting the user
        try {
            productService.deleteProduct(id);
            return new ResponseEntity<>("Product Deleted.", HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>("Product Could Not Be Deleted. Exception: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
