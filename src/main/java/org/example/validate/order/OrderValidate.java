package org.example.validate.order;

import io.micrometer.common.util.StringUtils;
import org.example.dto.request.order.CreateOrderRequest;
import org.example.dto.request.order.SearchOrderRequest;
import org.example.dto.request.order.UpdateOrderRequest;
import org.example.entity.AccountEntity;
import org.example.entity.CustomerEntity;
import org.example.entity.ProductEntity;
import org.example.model.CreateOrderValidateDTO;
import org.example.model.UpdateOrderValidateDTO;
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

    public CreateOrderValidateDTO validateCreateOrder(CreateOrderRequest request) {
        CreateOrderValidateDTO validateDTO = new CreateOrderValidateDTO();
        Errors errors = new BeanPropertyBindingResult(request, "createOrder");

//      Tìm sản phẩm theo mã
        ProductEntity product = null;
        if( (request.getProductCode()!=null)
                && (!request.getProductCode().isEmpty()) )
        {
            product = productService.findByProductCode(request.getProductCode());
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
                request.setBeginOrderDate(LocalDate.MIN.format(formatter));
                isValidDate = false;
            }
            LocalDate checkFormatBegin = LocalDate.parse(request.getBeginOrderDate(), formatter);

            if( StringUtils.isBlank(request.getEndOrderDate()) ){
                request.setEndOrderDate(LocalDate.MAX.format(formatter));
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

    public UpdateOrderValidateDTO validateUpdateOrder(UpdateOrderRequest request) {
        UpdateOrderValidateDTO updateOrderValidate = new UpdateOrderValidateDTO();
        Errors errors = new BeanPropertyBindingResult(request, "updateOrder");
        ProductEntity product = null;
        if(request.getProductCode()!=null ){
            product = productService.findByProductCode(request.getProductCode());
        }
        if(product==null){
            errors.rejectValue("productCode",
                    "productCode.notFound",
                    "Không tìm thấy mã sản phẩm!");
        }else {
            updateOrderValidate.setProduct(product);
        }

        CustomerEntity customer = null;
        if(request.getPhoneNumber()!=null){
            customer = customerService.findByPhoneNumber(request.getPhoneNumber());
        }
        if(customer==null){
            errors.rejectValue("phoneNumber",
                    "phoneNumber.notFound",
                    "Không tìm thấy số điện thoại của khách hàng!");
        }else {
            updateOrderValidate.setCustomer(customer);
        }

        if(request.getQuantity()<1){
            errors.rejectValue("quantity",
                    "invalid.quantity",
                    "Số lượng phải lớn hơn hoặc bằng 1!");
        }

        updateOrderValidate.setErrors(errors);
        return updateOrderValidate;
    }
}