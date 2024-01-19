package org.example.endpoint;

import org.example.dto.request.CreateCustomerRequest;
import org.example.dto.response.AccountResponseType;
import org.example.dto.response.StatusResponse;
import org.example.entity.AccountEntity;
import org.example.entity.CustomerEntity;
import org.example.service.AccountService;
import org.example.service.CustomerService;
import org.example.validate.account.AccountValidate;
import org.example.validate.customer.CustomerValidate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.Optional;

@Endpoint
public class CustomerEndpoint {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private CustomerValidate validate;

    @PayloadRoot(namespace = "http://yournamespace.com", localPart = "createCustomerRequest")
    @ResponsePayload
    public StatusResponse createCustomer(@RequestPayload CreateCustomerRequest customerRequest) {
        StatusResponse response = new StatusResponse();
        Errors errors = new BeanPropertyBindingResult(customerRequest, "customerRequest");

        try {
            // Thực hiện validation
            validate.validateCreateCustomer( customerRequest, errors );

            // Nếu có lỗi validation
            if ( errors.hasErrors() ) {
                throw new Exception( errors.getFieldError().getDefaultMessage() );
            }

            Optional<AccountEntity> accountOptional = accountService.findById(customerRequest.getAccountId());

            if ( accountOptional.isEmpty() ) {
                throw new Exception( "Account ID không tồn tại!" );
            }
            AccountEntity account = accountOptional.get();

//          Chuyển đổi từ request sang một entity để lưu
            CustomerEntity customer = convertCustomerRequestToEntity(customerRequest, account);

//          Thêm mới 1 khách hàng
            customer = customerService.saveCustomer(customer);

//          Chuyển đổi từ Entity sang một response
//            AccountResponseType accountResponseType = convertAccountToAccountType(account);

//          Cập nhật trạng thái
            response.setStatus("Account create successfully");
        } catch (Exception e) {
            // Xử lý lỗi và đặt giá trị lỗi vào phản hồi
            response.setStatus("Error: " + e.getMessage());
        }

        return response;
    }

    private CustomerEntity convertCustomerRequestToEntity(CreateCustomerRequest customerRequest, AccountEntity account) {
        CustomerEntity customer = new CustomerEntity();
        customer.setAccount(account);
        customer.setCustomerName(customerRequest.getCustomerName());
        customer.setAddress(customerRequest.getAddress());
        customer.setPhoneNumber(customerRequest.getPhoneNumber());
        customer.setVersion(0);
        customer.setIsDeleted(false);
        return customer;
    }
}
