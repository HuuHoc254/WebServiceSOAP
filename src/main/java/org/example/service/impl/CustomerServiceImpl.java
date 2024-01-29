package org.example.service.impl;

import org.example.entity.AccountEntity;
import org.example.entity.CustomerEntity;
import org.example.repository.CustomerRepository;
import org.example.service.AccountService;
import org.example.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public long totalRowFindAll() {
        return customerRepository.count();
    }

    @Override
    public List<CustomerEntity> findAll(int rowNumber, int pageSize) {
        return customerRepository.findAll(rowNumber, pageSize);
    }

    @Override
    public int totalRowSearch(String customerName, String phoneNumber) {
        return customerRepository.countSearch(customerName,phoneNumber);
    }

    @Override
    public List<CustomerEntity> search(String customerName, String phoneNumber, int rowNumber, int pageSize) {
        return customerRepository
                .searchCustomer(
                        customerName
                        ,phoneNumber
                        ,rowNumber
                        ,pageSize);
    }

    @Override
    public boolean deleteCustomer(Integer customerId, AccountEntity account) {
        return customerRepository.deleteCustomer(customerId,account.getAccountId()) != 0;
    }
}
