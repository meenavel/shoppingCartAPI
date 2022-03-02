package com.scapi.controller;

import com.scapi.DAO.CartDAO;
import com.scapi.DAO.CustomerDAO;
import com.scapi.entity.Cart;
import com.scapi.entity.Customer;
import com.scapi.model.CartAPIResponse;
import com.scapi.service.CustomerService;
import com.scapi.service.CartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("scapi/v1/customer")
@Api(value="Customer Management", protocols = "http")
public class CustomerController {

    Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    CustomerService customerService;

    @ApiOperation(value = "To add Customer Details", response = HttpStatus.class,code = 200)
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity addCustomer (@RequestBody Customer customer) throws Exception{
        return customerService.addCustomer(customer);
    }

    @ApiOperation(value = "To Get Customer Details", response = HttpStatus.class,code = 200)
    @ResponseStatus(HttpStatus.FOUND)
    @GetMapping
    public List<Customer> getCustomer(){
        return customerService.getCustomer();
    }

    @ApiOperation(value = "To Delete Customer Details By passing Customer id", response = HttpStatus.class,code = 200)
    @DeleteMapping(path = "/{id}")
    public ResponseEntity deleteCustomer(@PathVariable int id ) {

        return customerService.deleteCustomer(id);
    }
}
