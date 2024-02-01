package org.example.validate.order;

import io.micrometer.common.util.StringUtils;
import org.example.dto.request.order.CreateOrderRequest;
import org.example.dto.request.order.SearchOrderRequest;
import org.example.entity.AccountEntity;
import org.example.entity.CustomerEntity;
import org.example.entity.ProductEntity;
import org.example.model.CreateOrderValidateDTO;
import org.example.service.AccountService;
import org.example.service.CustomerService;
import org.example.service.OrderService;
import org.example.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class OrderValidate {
    @Autowired
    private OrderService orderService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private ProductService productService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private final String DEFAULT_BEGIN_DATE = "30-08-1945";
    private final String DEFAULT_END_DATE = "30-12-2100";
    public CreateOrderValidateDTO validateCreateOrder(CreateOrderRequest request) {
        CreateOrderValidateDTO validateDTO = new CreateOrderValidateDTO();
        Errors errors = new BeanPropertyBindingResult(request, "createOrder");

//      Tìm sản phẩm theo mã
        ProductEntity product = null;
        if( (request.getProductCode()!=null)
                && (!request.getProductCode().isEmpty()) )
        {
            product = productService.findByProductCode(request.getProductCode());
        } else if( (request.getProductName()!=null)
                && !request.getProductName().isEmpty())
        {
            product = productService.findByProductName(request.getProductName());
        }

//      Nếu không tìm thấy thì ghi nhận lỗi
        if(product==null){
            errors.rejectValue("productCode", "product.notFound", "Không tìm thấy thông tin sản phẩm!.");
        }else {
            validateDTO.setProduct(product);
        }

//      Tìm khách hàng theo số điện thoại
        CustomerEntity customer = null;
        if( (request.getPhoneNumberCustomer()!=null)
                && (!request.getPhoneNumberCustomer().isEmpty()) )
        {
            customer = customerService.findByPhoneNumber(request.getPhoneNumberCustomer());
        }
        else if( (request.getCustomerName()!=null)
                && (!request.getCustomerName().isEmpty()) )
        {
            customer = customerService.findByCustomerName(request.getCustomerName());
        }

//      Nếu không tìm thấy thì ghi nhận lỗi
        if (customer==null){
            errors.rejectValue("customerName", "customer.notFound", "Không tìm thấy thông tin khách hàng!.");
        } else {
            validateDTO.setCustomer(customer);
        }

        validateDTO.setErrors(errors);
        return validateDTO;
    }

    public Errors validateSearch(SearchOrderRequest request, AccountEntity account) {
        Errors errors = new BeanPropertyBindingResult(request, "searchOrder");
        if(account.getRole().getRoleId()==2){
            request.setAccountName("");
            request.setStaffFullName("");
        }
        boolean isValidDate = true;
        try {
            if( StringUtils.isBlank(request.getBeginOrderDate()) ){
                request.setBeginOrderDate(DEFAULT_BEGIN_DATE);
                isValidDate = false;
            }
            LocalDate checkFormatBegin = LocalDate.parse(request.getBeginOrderDate(), formatter);

            if( StringUtils.isBlank(request.getEndOrderDate()) ){
                request.setEndOrderDate(DEFAULT_END_DATE);
                isValidDate = false;
            }
            LocalDate checkFormatEnd = LocalDate.parse(request.getEndOrderDate(), formatter);

            if(checkFormatBegin.isAfter(LocalDate.now())){
                errors.rejectValue("beginOrderDate","beginOrderDate.range", "Ngày bắt đầu không đươc lớn hơn ngày hiện tại!");
            }else if (isValidDate && checkFormatEnd.isBefore(checkFormatBegin)) {
                    errors.rejectValue("endOrderDate","date.range", "Ngày kết thúc không được trước ngày bắt đầu");
            }

        }catch (Exception e){
            errors.rejectValue("beginOrderDate","beginOrderDate.format","Sai định dạng ngày tháng!");
        }
        return errors;
    }
}