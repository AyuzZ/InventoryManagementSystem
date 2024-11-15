package com.example.inventorymanagementsystem.controller;

import com.example.inventorymanagementsystem.dto.OrderInfoDisplay;
import com.example.inventorymanagementsystem.dto.OrderRequestDTO;
import com.example.inventorymanagementsystem.dto.OrderResponseDTO;
import com.example.inventorymanagementsystem.entity.Order;
import com.example.inventorymanagementsystem.entity.OrderInfo;
import com.example.inventorymanagementsystem.entity.Product;
import com.example.inventorymanagementsystem.entity.User;
import com.example.inventorymanagementsystem.exceptions.OrderNotFoundException;
import com.example.inventorymanagementsystem.exceptions.ProductNotFoundException;
import com.example.inventorymanagementsystem.service.OrderService;
import com.example.inventorymanagementsystem.service.ProductService;
import com.example.inventorymanagementsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/order/")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @PostMapping()
    public ResponseEntity<?> createOrder(@RequestBody List<OrderRequestDTO> orderRequests){

        Order order = new Order();
        order.setOrderDate(new Date());
        order.setOrderTotal(0.0);

        List<OrderInfo> orderInfoList = new ArrayList<>();

        for (OrderRequestDTO orderRequestDTO : orderRequests){
            //Check product id and retrieving its data if it exists. else throw exception
            Product orderedProduct;
            try {
                orderedProduct = productService.getProductById(orderRequestDTO.getPid());
            } catch (ProductNotFoundException e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }

            //Check if enough quantities are available, if not, throw an exception
            if(orderRequestDTO.getOrderQuantity() > orderedProduct.getStockQuantity()){
                return new ResponseEntity<>("Not enough stock available.", HttpStatus.BAD_REQUEST);
            }

            Double lineTotal = orderRequestDTO.getOrderQuantity() * orderedProduct.getUnitPrice();

            //create corresponding records in order info table
            OrderInfo orderInfo = new OrderInfo();
            orderInfo.setOrderQuantity(orderRequestDTO.getOrderQuantity());
            orderInfo.setLineTotal(lineTotal);
            orderInfo.setOrder(order);
            orderInfo.setProduct(orderedProduct);

            //after everything goes correctly
            //deduct orderqty from stock qty
            orderedProduct.setStockQuantity(orderedProduct.getStockQuantity() - orderRequestDTO.getOrderQuantity());
            //update details on product DB
            try {
                productService.updateProduct(orderedProduct);
            } catch (Exception e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }

            //            orderInfoList.add(orderInfo);
            order.getOrderInfoList().add(orderInfo);
            order.setOrderTotal(order.getOrderTotal() + lineTotal);

        }

        //Getting username of logged-in user from security context holder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        //Getting user id
        Integer userId = userService.getUserId(username);
        User user = new User();
        user.setUid(userId);
        user.setUsername(username);

        order.setUser(user);

        //saving order and order info records in the db
        try {
            orderService.createOrder(order, order.getOrderInfoList());
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("Order Created!", HttpStatus.CREATED);
    }

    //For admin - view all orders
    @GetMapping()
    public ResponseEntity<?> getOrders(){

        //to display
        //order record
        //with corresponding record/s from orderinfo
        //product name from products table

        return null;
    }

    //For admin (for now) - for users - only allow access to order id belonging to that user
    @GetMapping("{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable int orderId){
        Order order;
        try {
            order = orderService.getOrderById(orderId);
        }catch (OrderNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        OrderResponseDTO orderResponseDTO = OrderResponseDTO.builder()
                .oid(order.getOid())
                .uid(order.getUser().getUid())
                .username(order.getUser().getUsername())
                .orderDate(order.getOrderDate())
                .orderTotal(order.getOrderTotal())
                .build();

        List<OrderInfoDisplay> orderInfoDisplayList = new ArrayList<>();
        for (OrderInfo orderInfo : order.getOrderInfoList()){
            OrderInfoDisplay orderInfoDisplay = OrderInfoDisplay.builder()
                    .pid(orderInfo.getProduct().getPid())
                    .pName(orderInfo.getProduct().getName())
                    .unitPrice(orderInfo.getProduct().getUnitPrice())
                    .orderQuantity(orderInfo.getOrderQuantity())
                    .lineTotal(orderInfo.getLineTotal())
                    .build();
            orderInfoDisplayList.add(orderInfoDisplay);
        }

        orderResponseDTO.setOrderInfoDisplayList(orderInfoDisplayList);

        return new ResponseEntity<>(orderResponseDTO, HttpStatus.OK);
    }

    //For admins only
    @GetMapping("{userId}")
    public ResponseEntity<?> getOrderByUserId(@PathVariable int userId){

        List<Order> orderList;
        try {
            orderList = orderService.getOrderByUserId(userId);
        }catch (OrderNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        List<OrderResponseDTO> orderResponseDTOList = new ArrayList<>();
        for (Order order : orderList){
            OrderResponseDTO orderResponseDTO = OrderResponseDTO.builder()
                    .oid(order.getOid())
                    .uid(order.getUser().getUid())
                    .username(order.getUser().getUsername())
                    .orderDate(order.getOrderDate())
                    .orderTotal(order.getOrderTotal())
                    .build();

            List<OrderInfoDisplay> orderInfoDisplayList = new ArrayList<>();
            for (OrderInfo orderInfo : order.getOrderInfoList()){
                OrderInfoDisplay orderInfoDisplay = OrderInfoDisplay.builder()
                        .pid(orderInfo.getProduct().getPid())
                        .pName(orderInfo.getProduct().getName())
                        .unitPrice(orderInfo.getProduct().getUnitPrice())
                        .orderQuantity(orderInfo.getOrderQuantity())
                        .lineTotal(orderInfo.getLineTotal())
                        .build();
                orderInfoDisplayList.add(orderInfoDisplay);
            }

            orderResponseDTO.setOrderInfoDisplayList(orderInfoDisplayList);
            orderResponseDTOList.add(orderResponseDTO);
        }
        return new ResponseEntity<>(orderResponseDTOList, HttpStatus.OK);
    }

    //to view your own orders
    @GetMapping("")
    public ResponseEntity<?> getOrderForLoggedInUser(){

        //Getting username of logged-in user from security context holder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        //Getting user id
        Integer userId = userService.getUserId(username);

        List<Order> orderList;
        try {
            orderList = orderService.getOrderByUserId(userId);
        }catch (OrderNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        List<OrderResponseDTO> orderResponseDTOList = new ArrayList<>();
        for (Order order : orderList){
            OrderResponseDTO orderResponseDTO = OrderResponseDTO.builder()
                    .oid(order.getOid())
                    .uid(order.getUser().getUid())
                    .username(order.getUser().getUsername())
                    .orderDate(order.getOrderDate())
                    .orderTotal(order.getOrderTotal())
                    .build();

            List<OrderInfoDisplay> orderInfoDisplayList = new ArrayList<>();
            for (OrderInfo orderInfo : order.getOrderInfoList()){
                OrderInfoDisplay orderInfoDisplay = OrderInfoDisplay.builder()
                        .pid(orderInfo.getProduct().getPid())
                        .pName(orderInfo.getProduct().getName())
                        .unitPrice(orderInfo.getProduct().getUnitPrice())
                        .orderQuantity(orderInfo.getOrderQuantity())
                        .lineTotal(orderInfo.getLineTotal())
                        .build();
                orderInfoDisplayList.add(orderInfoDisplay);
            }

            orderResponseDTO.setOrderInfoDisplayList(orderInfoDisplayList);
            orderResponseDTOList.add(orderResponseDTO);
        }
        return new ResponseEntity<>(orderResponseDTOList, HttpStatus.OK);
    }

}
