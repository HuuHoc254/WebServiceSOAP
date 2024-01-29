package org.example.service;

import org.example.entity.AccountEntity;
import org.example.entity.CustomerEntity;

import java.util.List;

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
}
