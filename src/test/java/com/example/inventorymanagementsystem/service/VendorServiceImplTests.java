package com.example.inventorymanagementsystem.service;

import com.example.inventorymanagementsystem.entity.Vendor;
import com.example.inventorymanagementsystem.exceptions.VendorExistsException;
import com.example.inventorymanagementsystem.exceptions.VendorNotFoundException;
import com.example.inventorymanagementsystem.repository.VendorRepository;
import com.example.inventorymanagementsystem.service.impl.VendorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class VendorServiceImplTests {

    @Mock
    private VendorRepository vendorRepository;

    @InjectMocks
    private VendorServiceImpl vendorService;

    private Vendor vendor;

    @Captor
    private ArgumentCaptor<Vendor> vendorCaptor;

    @BeforeEach
    public void setUp() {
        vendor = new Vendor();
        vendor.setVid(1);
        vendor.setContact("contact123");
        vendor.setName("Test Vendor");
    }

    @Test
    public void createVendorTest_Success() {
        when(vendorRepository.getAvailableVendorByContact(vendor.getContact())).thenReturn(Optional.empty());
        when(vendorRepository.save(any(Vendor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Vendor createdVendor = vendorService.createVendor(vendor);

        assertNotNull(createdVendor);
        assertEquals(vendor.getContact(), createdVendor.getContact());
        assertEquals(vendor.getName(), createdVendor.getName());

        verify(vendorRepository, times(1)).getAvailableVendorByContact(vendor.getContact());
        verify(vendorRepository, times(1)).save(vendorCaptor.capture());

        Vendor savedVendor = vendorCaptor.getValue();
        assertEquals(vendor.getContact(), savedVendor.getContact());
        assertEquals(vendor.getName(), savedVendor.getName());
    }

    @Test
    public void createVendorTest_VendorExists() {
        when(vendorRepository.getAvailableVendorByContact(vendor.getContact())).thenReturn(Optional.of(vendor));

        assertThrows(VendorExistsException.class, () -> vendorService.createVendor(vendor));

        verify(vendorRepository, times(1)).getAvailableVendorByContact(vendor.getContact());
        verify(vendorRepository, never()).save(any(Vendor.class));
    }

    @Test
    public void getVendorsTest() {
        when(vendorRepository.findAll()).thenReturn(Collections.singletonList(vendor));

        List<Vendor> vendors = vendorService.getVendors();

        assertNotNull(vendors);
        assertEquals(1, vendors.size());
        assertEquals(vendor.getContact(), vendors.get(0).getContact());

        verify(vendorRepository, times(1)).findAll();
    }

    @Test
    public void getVendorByIdTest_Success() {
        when(vendorRepository.findById(vendor.getVid())).thenReturn(Optional.of(vendor));

        Vendor foundVendor = vendorService.getVendorById(vendor.getVid());

        assertNotNull(foundVendor);
        assertEquals(vendor.getContact(), foundVendor.getContact());

        verify(vendorRepository, times(1)).findById(vendor.getVid());
    }

    @Test
    public void getVendorByIdTest_NotFound() {
        when(vendorRepository.findById(vendor.getVid())).thenReturn(Optional.empty());

        assertThrows(VendorNotFoundException.class, () -> vendorService.getVendorById(vendor.getVid()));

        verify(vendorRepository, times(1)).findById(vendor.getVid());
    }

    @Test
    public void getAvailableVendorByIdTest_Success() {
        when(vendorRepository.getAvailableVendorById(vendor.getVid())).thenReturn(Optional.of(vendor));

        Vendor foundVendor = vendorService.getAvailableVendorById(vendor.getVid());

        assertNotNull(foundVendor);
        assertEquals(vendor.getContact(), foundVendor.getContact());

        verify(vendorRepository, times(1)).getAvailableVendorById(vendor.getVid());
    }

    @Test
    public void getAvailableVendorByIdTest_NotFound() {
        when(vendorRepository.getAvailableVendorById(vendor.getVid())).thenReturn(Optional.empty());

        assertThrows(VendorNotFoundException.class, () -> vendorService.getAvailableVendorById(vendor.getVid()));

        verify(vendorRepository, times(1)).getAvailableVendorById(vendor.getVid());
    }

    @Test
    public void getVendorByContactTest() {
        when(vendorRepository.getAvailableVendorByContact(vendor.getContact())).thenReturn(Optional.of(vendor));

        Vendor foundVendor = vendorService.getVendorByContact(vendor.getContact());

        assertNotNull(foundVendor);
        assertEquals(vendor.getContact(), foundVendor.getContact());

        verify(vendorRepository, times(1)).getAvailableVendorByContact(vendor.getContact());
    }

    @Test
    public void vendorExistsTest() {
        when(vendorRepository.checkVendor(vendor.getVid())).thenReturn(1);

        assertTrue(vendorService.vendorExists(vendor.getVid()));

        verify(vendorRepository, times(1)).checkVendor(vendor.getVid());
    }

    @Test
    public void updateVendorTest() {
        when(vendorRepository.save(any(Vendor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Vendor updatedVendor = vendorService.updateVendor(vendor);

        assertNotNull(updatedVendor);
        assertEquals(vendor.getContact(), updatedVendor.getContact());
        assertEquals(vendor.getName(), updatedVendor.getName());

        verify(vendorRepository, times(1)).save(vendorCaptor.capture());

        Vendor savedVendor = vendorCaptor.getValue();
        assertEquals(vendor.getContact(), savedVendor.getContact());
        assertEquals(vendor.getName(), savedVendor.getName());
    }

    @Test
    public void deleteVendorTest() {
        when(vendorRepository.save(any(Vendor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Vendor deletedVendor = vendorService.deleteVendor(vendor);

        assertNotNull(deletedVendor);
        assertNull(deletedVendor.getContact());
        assertTrue(deletedVendor.getName().endsWith("_deleted"));

        verify(vendorRepository, times(1)).save(vendorCaptor.capture());

        Vendor savedVendor = vendorCaptor.getValue();
        assertNull(savedVendor.getContact());
        assertTrue(savedVendor.getName().endsWith("_deleted"));
    }
}

