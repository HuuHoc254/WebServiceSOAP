package org.example.service.impl;

import org.example.dto.request.customer.UpdateCustomerRequest;
import org.example.entity.AccountEntity;
import org.example.entity.CustomerEntity;
import org.example.repository.CustomerRepository;
import org.example.service.AccountService;
import org.example.service.CustomerService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
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
    @Override
    @Transactional
    public CustomerEntity updateCustomer(UpdateCustomerRequest request) {
        int rowUpdate = customerRepository
                .updateCustomer(
                         request.getCustomerId()
                        ,request.getAccountId()
                        ,request.getCustomerName()
                        ,request.getPhoneNumber()
                        ,request.getAddress()
                        ,request.getVersion()
                );

        if (rowUpdate==0) {
            throw new OptimisticLockingFailureException
                    ("Phiên bản không trùng khớp. Có thể đã có người cập nhật thông tin sản phẩm!.");
        }
        CustomerEntity customer = new CustomerEntity();
        BeanUtils.copyProperties(request,customer);
        customer.setVersion(customer.getVersion()+1);
        return customer;
    }
    @Override
    public CustomerEntity findByPhoneNumber(String phoneNumber) {
        return customerRepository.findByPhoneNumber(phoneNumber).orElse(null);
    }
    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return customerRepository.existsByPhoneNumber(phoneNumber);
    }
    @Override
    public boolean existsByPhoneNumberAndCustomerIdNot(String phoneNumber, Integer customerId) {
        return customerRepository.existsByPhoneNumberAndCustomerIdNot(phoneNumber,customerId);
    }

    @Override
    public CustomerEntity findByCustomerName(String customerName) {
        return customerRepository.findByCustomerName(customerName);
    }
}
