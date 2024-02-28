package org.example.validate.order;

import io.micrometer.common.util.StringUtils;
import org.example.dto.request.order.SaveListOrderRequest;
import org.example.dto.request.order.SearchOrderRequest;
import org.example.entity.AccountEntity;
import org.example.entity.CustomerEntity;
import org.example.entity.ProductEntity;
import org.example.model.ErrorDTO;
import org.example.model.ListSaveOrderDTO;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private final String DATE_MIN = "01-01-1000";
    private final String DATE_MAX = "01-01-5000";
    public ListSaveOrderDTO validateSaveOrder(SaveListOrderRequest request,AccountEntity account) {
        List<ErrorDTO>  errors  = new ArrayList<>();
        StringBuilder   sql     = new StringBuilder();
        request.getListOrder().forEach(orderRequest ->{
            List<String> error  = new ArrayList<>();
            ProductEntity product =
                    productService.findByProductCode(orderRequest.getProductCode());
            CustomerEntity customer =
                    converterMapToEntity(customerService.findByPhoneNumber(orderRequest.getPhoneNumberCustomer()));
            if(product.getProductId()==null){
                error.add("ProductCode not found!");
            }
            if(customer.getCustomerId()==null){
                error.add("PhoneNumber of Customer not found!");
            }
            if(orderRequest.getQuantity()<1){
                error.add("Quantity invalid!");
            }
            if(!error.isEmpty()){
                errors.add(new ErrorDTO(orderRequest.getOrdinalNumber(),error));
            }else if(orderRequest.getOrderId()!=0){
                sql.append("UPDATE\n")
                   .append("        orders o\n")
                   .append(" JOIN   product    p ON p.product_id   = o.product_id\n")
                   .append(" JOIN   customer   c ON c.customer_id  = o.customer_id\n")
                   .append(" SET\n")
                   .append("    o.product_id                = ").append(product.getProductId()).append("\n")
                   .append(",   o.unit_price                = ").append(product.getSalePrice()).append("\n")
                   .append(",   o.quantity                  = ").append(orderRequest.getQuantity()).append("\n")
                   .append(",   o.customer_id               = ").append(customer.getCustomerId()).append("\n")
                   .append(",   o.address_customer          = ").append("'").append(customer.getAddress()).append("'").append("\n")
                   .append(",   o.phone_number_customer     = ").append(customer.getPhoneNumber()).append("\n")
                   .append(",   o.version                   = ").append(" o.version + 1\n")
                   .append(" WHERE\n")
                   .append("      o.order_id                = ").append(orderRequest.getOrderId()).append("\n")
                   .append(" AND  o.version                 = ").append(orderRequest.getVersion()).append("\n")
                   .append(" AND  o.order_status_id         = ").append(1).append(";\n");
            }else {
                sql.append("INSERT INTO orders(")
                   .append(" product_id")
                   .append(", unit_price")
                   .append(", quantity")
                   .append(", customer_id")
                   .append(", address_customer")
                   .append(", phone_number_customer")
                   .append(", account_id )\n")
                   .append("VALUES( ")
                   .append(product.getProductId())
                   .append(", ").append(product.getSalePrice())
                   .append(", ").append(orderRequest.getQuantity())
                   .append(", ").append(customer.getCustomerId())
                   .append(", ").append("'").append(customer.getAddress()).append("' ")
                   .append(", ").append("'").append(customer.getPhoneNumber()).append("' ")
                   .append(", ").append(account.getAccountId()).append(" );\n");
            }
        });

        return new ListSaveOrderDTO(errors,sql);
    }

    private CustomerEntity converterMapToEntity(Map<String, Object> map) {
        CustomerEntity customer = new CustomerEntity();
        customer.setCustomerId((Integer) map.get("customer_id"));
        customer.setCustomerName((String) map.get("customer_name"));
        customer.setAddress((String) map.get("address"));
        customer.setPhoneNumber((String) map.get("phone_number"));
        return customer;
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
                request.setBeginOrderDate(DATE_MIN);
                isValidDate = false;
            }
            LocalDate checkFormatBegin = LocalDate.parse(request.getBeginOrderDate(), formatter);

            if( StringUtils.isBlank(request.getEndOrderDate()) ){
                request.setEndOrderDate(DATE_MAX);
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