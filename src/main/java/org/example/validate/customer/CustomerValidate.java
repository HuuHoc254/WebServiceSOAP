package org.example.validate.customer;

import org.example.dto.request.CreateCustomerRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class CustomerValidate {
    public void validateCreateCustomer(CreateCustomerRequest customerRequest, Errors errors) {
        validatePhoneNumber(customerRequest.getPhoneNumber(),errors);
    }
    private void validatePhoneNumber(String phoneNumber, Errors errors) {

        // Kiểm tra đúng định dạng số điện thoại )
        if ( !phoneNumber.matches("^\\d{10,}$") ) {
            errors.rejectValue("phoneNumber", "phoneNumber.invalid", "Định dạng số điện thoại không hợp lệ.");
        }
    }
}
