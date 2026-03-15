package com.bank.corebanking.controller;

import com.bank.corebanking.entity.Customer;
import com.bank.corebanking.service.CustomerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @PostMapping
    public Customer createCustomer(@RequestBody Customer customer){
        return service.createCustomer(customer);
    }

    @GetMapping
    public List<Customer> getAll(){
        return service.getAllCustomers();
    }
}