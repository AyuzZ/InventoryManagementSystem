package com.example.inventorymanagementsystem.controller;

import com.example.inventorymanagementsystem.entity.Vendor;
import com.example.inventorymanagementsystem.exceptions.VendorExistsException;
import com.example.inventorymanagementsystem.exceptions.VendorNotFoundException;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class VendorControllerTests {

    @Mock
    private VendorService vendorService;

    @InjectMocks
    private VendorController vendorController;

    private Vendor validVendor;

    @BeforeEach
    public void setUp() {
        validVendor = new Vendor();
        validVendor.setName("Test Vendor");
        validVendor.setContact("1234567890");
    }

    @Test
    public void testCreateVendor_Success() throws VendorExistsException {
        // Arrange
        when(vendorService.createVendor(validVendor)).thenReturn(validVendor);

        // Act
        ResponseEntity<?> response = vendorController.createVendor(validVendor);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Vendor Created.", response.getBody());
        verify(vendorService).createVendor(validVendor);
    }

    @Test
    public void testCreateVendor_InvalidContactLength() {
        // Arrange
        Vendor invalidVendor = new Vendor();
        invalidVendor.setContact("123");

        // Act
        ResponseEntity<?> response = vendorController.createVendor(invalidVendor);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Contact must contain 10 numbers.", response.getBody());
    }

    @Test
    public void testCreateVendor_InvalidContactFormat() {
        // Arrange
        Vendor invalidVendor = new Vendor();
        invalidVendor.setContact("123abc4567");

        // Act
        ResponseEntity<?> response = vendorController.createVendor(invalidVendor);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Contact Must only contain numbers.", response.getBody());
    }

    @Test
    public void testGetVendor_Success() throws VendorNotFoundException {
        // Arrange
        int vendorId = 1;
        when(vendorService.getVendorById(vendorId)).thenReturn(validVendor);

        // Act
        ResponseEntity<?> response = vendorController.getVendor(vendorId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testGetVendor_NotFound() throws VendorNotFoundException {
        // Arrange
        int vendorId = 1;
        when(vendorService.getVendorById(vendorId)).thenThrow(new VendorNotFoundException("Vendor not found"));

        // Act
        ResponseEntity<?> response = vendorController.getVendor(vendorId);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testGetVendors_Success() throws VendorNotFoundException {
        // Arrange
        List<Vendor> vendors = Arrays.asList(validVendor);
        when(vendorService.getVendors()).thenReturn(vendors);

        // Act
        ResponseEntity<?> response = vendorController.getVendors();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testUpdateVendor_Success() throws VendorNotFoundException {
        // Arrange
        int vendorId = 1;
        Vendor existingVendor = new Vendor();
        existingVendor.setVid(vendorId);
        existingVendor.setName("Old Name");
        existingVendor.setContact("0987654321");

        when(vendorService.getVendorById(vendorId)).thenReturn(existingVendor);
        when(vendorService.getVendorByContact(validVendor.getContact())).thenReturn(null);
        when(vendorService.updateVendor(existingVendor)).thenReturn(validVendor);

        // Act
        ResponseEntity<?> response = vendorController.updateVendor(vendorId, validVendor);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Vendor Details Updated.", response.getBody());
    }

    @Test
    public void testImportVendorsFromCSV_Success() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "vendors.csv",
                "text/csv",
                "test,data".getBytes()
        );
        doNothing().when(vendorService).importFromCSV(file);

        // Act
        ResponseEntity<?> response = vendorController.importVendorsFromCSV(file);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Products imported successfully.", response.getBody());
    }

    @Test
    public void testImportVendorsFromCSV_EmptyFile() {
        // Arrange
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "vendors.csv",
                "text/csv",
                new byte[0]
        );

        // Act
        ResponseEntity<?> response = vendorController.importVendorsFromCSV(emptyFile);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("File is empty. Please upload a valid CSV file.", response.getBody());
    }

    @Test
    public void testExportVendorsToCSV_Success() throws Exception {
        // Arrange
        String filePath = "/tmp/vendors.csv";
        when(vendorService.exportToCSV()).thenReturn(filePath);

        // Act
        ResponseEntity<org.springframework.core.io.Resource> response = vendorController.exportVendorsToCSV();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
