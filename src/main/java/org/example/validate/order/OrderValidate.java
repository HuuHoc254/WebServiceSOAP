package org.example.validate.order;

import io.micrometer.common.util.StringUtils;
import org.example.dto.request.order.SaveListOrderRequest;
import org.example.dto.request.order.SearchOrderRequest;
import org.example.entity.AccountEntity;
import org.example.entity.CustomerEntity;
import org.example.entity.ProductEntity;
import org.example.model.ErrorDTO;
import org.example.model.ListSaveOrderDTO;
import org.example.model.SaveOrderDTO;
import org.example.service.AccountService;
import org.example.service.CustomerService;
import org.example.service.OrderService;
import org.example.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.sql.Time;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
        List<SaveOrderDTO> listOrder = new ArrayList<>();
        request.getListOrder().forEach(orderRequest ->{
            SaveOrderDTO orderDTO = new SaveOrderDTO();
            List<String> error  = new ArrayList<>();
            orderDTO.setOrderId(orderRequest.getOrderId());
            orderDTO.setVersion(orderRequest.getVersion());
            orderDTO.setAccountId(account.getAccountId());
            orderDTO.setOrdinalNumber(orderRequest.getOrdinalNumber());
            ProductEntity product =
                    productService.findByProductCode(orderRequest.getProductCode());
            CustomerEntity customer =
                    converterMapToEntity(customerService.findByPhoneNumber(orderRequest.getPhoneNumberCustomer()));
            if(product.getProductId()==null){
                error.add("Mã sản phẩm không tồn tại!");
            }else{
                orderDTO.setProductId(product.getProductId());
                orderDTO.setUnitPrice(product.getSalePrice());
                orderDTO.setQuantity(orderRequest.getQuantity());
            }
            if(customer.getCustomerId()==null){
                error.add("Số điện thoại không tồn tại!");
            }else{
                orderDTO.setCustomerId(customer.getCustomerId());
                orderDTO.setPhoneNumber(customer.getPhoneNumber());
                orderDTO.setAddress(customer.getAddress());
            }
            if(orderRequest.getQuantity()<1){
                error.add("Số lượng không hợp lệ!");
            }
            if(!error.isEmpty()){
                errors.add(new ErrorDTO(orderRequest.getOrdinalNumber(),error));
            }
            listOrder.add(orderDTO);
        });

        return new ListSaveOrderDTO(errors,listOrder);
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
            if(request.getAllocation()==null && request.getOrder()==null){
                request.setAllocation(2);
                request.setOrder(1);
            }
            if(request.getOrder()==0 && request.getAllocation()==0){
                request.setAllocation(2);
                request.setOrder(1);
            }
            if(request.getOrder()==null){
                request.setOrder(0);
            }
            if(request.getAllocation()==null){
                request.setAllocation(0);
            }

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

