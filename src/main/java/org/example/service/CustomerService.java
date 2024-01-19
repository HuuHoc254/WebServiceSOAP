package org.example.service;

import org.example.dto.request.CreateCustomerRequest;
import org.example.entity.CustomerEntity;

public interface CustomerService {
    CustomerEntity saveCustomer(CustomerEntity customer);
}
