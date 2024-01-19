package org.example.service.impl;

import org.example.dto.request.CreateCustomerRequest;
import org.example.entity.AccountEntity;
import org.example.entity.CustomerEntity;
import org.example.repository.CustomerRepository;
import org.example.service.AccountService;
import org.example.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private AccountService accountService;
    @Override
    public CustomerEntity saveCustomer(CustomerEntity customer) {
        return customerRepository.save(customer);
    }
}
