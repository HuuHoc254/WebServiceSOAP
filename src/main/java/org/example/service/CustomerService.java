package org.example.service;

import org.example.dto.request.customer.UpdateCustomerRequest;
import org.example.entity.AccountEntity;
import org.example.entity.CustomerEntity;

import java.util.List;
import java.util.Map;

public interface CustomerService {
    CustomerEntity saveCustomer(CustomerEntity customer);

    int totalRowSearch(String customerName, String phoneNumber);

    List<Map<String,Object>> search(String customerName
                              , String phoneNumber
                              , int rowNumber
                              , int pageSize);

    int deleteCustomer(Integer customerId, AccountEntity account);

    CustomerEntity updateCustomer(UpdateCustomerRequest request);

    Map<String,Object> findByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumberAndCustomerIdNot(String phoneNumber, Integer customerId);

    Map<String,Object> findByCustomerName(String customerName);

}
