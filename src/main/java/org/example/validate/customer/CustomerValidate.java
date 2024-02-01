package org.example.validate.customer;

import org.example.dto.request.customer.CreateCustomerRequest;
import org.example.dto.request.customer.UpdateCustomerRequest;
import org.example.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

@Component
public class CustomerValidate {
    @Autowired
    private CustomerService customerService;
    public Errors validateCreateCustomer(CreateCustomerRequest createCustomer) {
        Errors errors = new BeanPropertyBindingResult(createCustomer, "createCustomer");
        String phoneNumber = createCustomer.getPhoneNumber();
//      Kiểm tra đúng định dạng số điện thoại )
        if ( !phoneNumber.matches("^\\d{10}$") ) {
            errors.rejectValue("phoneNumber", "phoneNumber.invalid", "Định dạng số điện thoại không hợp lệ.");
        } else
//      Kiểm tra số điện thoại có trùng chưa
        if (customerService.existsByPhoneNumber(phoneNumber)) {
            errors.rejectValue("phoneNumber", "duplicate.phoneNumber", "Số điện thoại đã tồn tại cho tài khoản khác.");
        }
        return errors;
    }
    public Errors validateUpdateCustomer(UpdateCustomerRequest request) {
        Errors errors = new BeanPropertyBindingResult(request, "updateCustomer");
        String phoneNumber = request.getPhoneNumber();
//      Kiểm tra đúng định dạng số điện thoại )
        if ( !phoneNumber.matches("^\\d{10}$") ) {
            errors.rejectValue("phoneNumber", "phoneNumber.invalid", "Định dạng số điện thoại không hợp lệ.");
        }
//      Kiểm tra số điện thoại có trùng chưa
        else if (customerService.existsByPhoneNumberAndCustomerIdNot(phoneNumber,request.getCustomerId())) {
            errors.rejectValue("phoneNumber", "duplicate.phoneNumber", "Số điện thoại đã tồn tại cho tài khoản khác.");
        }
        return errors;
    }
}
