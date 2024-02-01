package org.example.service;

import org.example.dto.request.customer.UpdateCustomerRequest;
import org.example.entity.AccountEntity;
import org.example.entity.CustomerEntity;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    CustomerEntity saveCustomer(CustomerEntity customer);

    long totalRowFindAll();

    List<CustomerEntity> findAll(int rowNumber, int pageSize);

    int totalRowSearch(String customerName, String phoneNumber);

    List<CustomerEntity> search( String customerName
                              , String phoneNumber
                              , int rowNumber
                              , int pageSize);

    boolean deleteCustomer(Integer customerId, AccountEntity account);

    CustomerEntity updateCustomer(UpdateCustomerRequest request);

    CustomerEntity findByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumberAndCustomerIdNot(String phoneNumber, Integer customerId);

    CustomerEntity findByCustomerName(String customerName);
}
