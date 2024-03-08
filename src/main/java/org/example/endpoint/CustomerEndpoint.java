package org.example.endpoint;

import org.example.dto.request.customer.*;
import org.example.dto.request.product.GetProductName;
import org.example.dto.response.ErrorTypeResponse;
import org.example.dto.response.StatusResponse;
import org.example.dto.response.customer.*;
import org.example.dto.response.product.ProductName;
import org.example.entity.AccountEntity;
import org.example.entity.CustomerEntity;
import org.example.security.UserDetailImpl;
import org.example.service.AccountService;
import org.example.service.CustomerService;
import org.example.validate.customer.CustomerValidate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Endpoint
public class CustomerEndpoint {
    private static final String NAMESPACE_URI = "http://yournamespace.com";
    private final String ADMIN = "ROLE_ADMIN";
    private final String STAFF = "ROLE_STAFF";
    @Autowired
    private AccountService accountService;
    @Autowired
    private CustomerValidate validate;
    @Autowired
    private CustomerService customerService;
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "createCustomerRequest")
    @ResponsePayload
    @Secured({ADMIN, STAFF})
    public StatusResponse createCustomer(@RequestPayload CreateCustomerRequest createCustomer) {
        StatusResponse response = new StatusResponse();
        try {
            // Thực hiện validation
            Errors errors = validate.validateCreateCustomer(createCustomer);

            // Nếu có lỗi validation
            if ( errors.hasErrors() ) {
                throw new Exception( errors.getFieldError().getDefaultMessage() );
            }
            UserDetailImpl userDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            AccountEntity account = accountService.findById(userDetail.getAccountId()).orElse(null);

//          Chuyển đổi từ request sang một entity để lưu
            CustomerEntity customer = new CustomerEntity();
            BeanUtils.copyProperties(createCustomer,customer);
            customer.setAccount(account);
//          Thêm mới 1 khách hàng
            customer = customerService.saveCustomer(customer);

//          Cập nhật trạng thái
            response.setMessage("Customer create successfully");
        } catch (Exception e) {
            // Xử lý lỗi và đặt giá trị lỗi vào phản hồi
            response.setMessage("Error: " + e.getMessage());
        }

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "searchCustomerRequest")
    @ResponsePayload
    @Secured({ADMIN , STAFF})
    public CustomerResponseList searchCustomer(@RequestPayload SearchCustomerRequest searchCustomerRequest) {
        CustomerResponseList responseList = new CustomerResponseList();
        int pageSize = Optional.ofNullable(searchCustomerRequest.getPageSize()).orElse(5);
        int pageNumber = Optional.ofNullable(searchCustomerRequest.getPageNumber()).orElse(1);

        if (pageSize < 1) {
            throw new RuntimeException("PageSize phải từ 1 trở lên!");
        }

//      Lấy tổng số hàng trong quá trình search
        int totalRow =  customerService.totalRowSearch(
                searchCustomerRequest.getCustomerName()
                ,searchCustomerRequest.getPhoneNumber());

//      Nếu không tìm thấy thì thông báo
        if(totalRow==0){
            throw new RuntimeException("Không tìm thấy Customer nào!");
        }

//      Lấy tổng số trang
        int totalPage = (int) Math.ceil( ((double) totalRow /pageSize) );

//      Nếu pageIndex vượt qua số hàng tìm được thì báo lỗi
        if(totalRow <= (pageNumber-1) * pageSize){
            throw new RuntimeException("Số trang không hợp lệ!");
        }

//      Lấy danh sách record theo page index
        List<Map<String,Object>> customers
                = customerService.search(
                searchCustomerRequest.getCustomerName()
                ,searchCustomerRequest.getPhoneNumber()
                ,((pageNumber-1) * pageSize)
                ,pageSize);

//Chuyển đổi về XML
        List<CustomerResponseType> customerResponses = customers
                .stream()
                .map(this::converterMapToResponse).toList();

        responseList.setCustomerResponses(customerResponses);
        return responseList;
    }

    private CustomerResponseType converterMapToResponse(Map<String, Object> map) {
        CustomerResponseType response = new CustomerResponseType();
        response.setCustomerId((Integer) map.get("customer_id"));
        response.setCustomerName((String) map.get("customer_name"));
        response.setAddress((String) map.get("address"));
        response.setPhoneNumber((String) map.get("phone_number"));
        response.setAccountName((String) map.get("account_name"));
        response.setVersion((Integer) map.get("version"));
        response.setIsDeleted((Boolean) map.get("is_deleted"));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "deleteCustomerRequest")
    @ResponsePayload
    @Secured({ADMIN, STAFF})
    public StatusResponse deleteCustomer(@RequestPayload DeleteCustomerRequest deleteCustomer) {
        StatusResponse response = new StatusResponse();
        try {
            UserDetailImpl userDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            AccountEntity account = accountService.findById(userDetail.getAccountId()).orElse(null);

            boolean check =
                    customerService.deleteCustomer(
                            deleteCustomer.getCustomerId(),account) !=0;
            if(check){
//          Cập nhật trạng thái
                response.setMessage("Customer đã xóa thành công!");
            }else {
                throw new Exception("Bạn không có quyền để xóa!");
            }
        } catch (Exception e) {
            // Xử lý lỗi và đặt giá trị lỗi vào phản hồi
            response.setMessage("Error: " + e.getMessage());
        }

        return response;
    }

    @PayloadRoot(namespace = "http://yournamespace.com", localPart = "updateCustomerRequest")
    @ResponsePayload
    @Secured({ADMIN , STAFF})
    public UpdateCustomerResponse updateCustomer(@RequestPayload UpdateCustomerRequest request) {
        UpdateCustomerResponse response = new UpdateCustomerResponse();

        try {
            // Thực hiện validation
            Errors errors = validate.validateUpdateCustomer(request);

            // Nếu có lỗi validation
            if (errors.hasErrors()) {
                List<ErrorTypeResponse> errorListResponse =
                        errors.getFieldErrors().stream().map(er ->{
                            ErrorTypeResponse errorTypeResponse = new ErrorTypeResponse();
                            errorTypeResponse.setErrorMessage(er.getDefaultMessage());
                            return errorTypeResponse;
                        }).collect(Collectors.toList());
                response.setErrorTypes(errorListResponse);
            }

            CustomerEntity customer = customerService.updateCustomer(request);

            CustomerResponseType customerResponseType = new CustomerResponseType();
            BeanUtils.copyProperties(customer,customerResponseType);

            response.setCustomerResponseType(customerResponseType);

            response.setMessage("Cập nhật sản phẩm thành công!");

        } catch (Exception e) {
            // Xử lý lỗi và đặt giá trị lỗi vào phản hồi
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getPhoneNumber")
    @ResponsePayload
    @Secured({ADMIN,STAFF})
    public PhoneNumber getPhoneNumberByCustomerName(@RequestPayload GetPhoneNumber request) {
        PhoneNumber response = new PhoneNumber();
        Map<String,Object> map = customerService.findByCustomerName(request.getCustomerName());
        response.setPhoneNumber((String) map.get("phone_number"));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getCustomerName")
    @ResponsePayload
    @Secured({ADMIN,STAFF})
    public CustomerName getCustomerNameByPhoneNumber(@RequestPayload GetCustomerName request) {
        CustomerName response = new CustomerName();
        Map<String,Object> map = customerService.findByPhoneNumber(request.getPhoneNumber());
        response.setCustomerName((String) map.get("customer_name"));
        return response;
    }
}