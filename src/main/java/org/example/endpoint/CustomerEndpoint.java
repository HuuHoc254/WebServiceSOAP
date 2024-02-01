package org.example.endpoint;

import org.example.dto.request.customer.*;
import org.example.dto.request.product.UpdateProductRequest;
import org.example.dto.response.*;
import org.example.dto.response.customer.CustomerResponseList;
import org.example.dto.response.customer.CustomerResponseType;
import org.example.dto.response.customer.UpdateCustomerResponse;
import org.example.dto.response.product.ProductResponseType;
import org.example.dto.response.product.UpdateProductResponse;
import org.example.entity.AccountEntity;
import org.example.entity.CustomerEntity;
import org.example.entity.ProductEntity;
import org.example.service.AccountService;
import org.example.service.CustomerService;
import org.example.validate.customer.CustomerValidate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.Errors;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Endpoint
public class CustomerEndpoint {
    private static final String NAMESPACE_URI = "http://yournamespace.com";
    @Autowired
    private AccountService accountService;
    @Autowired
    private CustomerValidate validate;
    @Autowired
    private CustomerService customerService;
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "createCustomerRequest")
    @ResponsePayload
    @Secured({"ROLE_ADMIN", "ROLE_STAFF"})
    public StatusResponse createCustomer(@RequestPayload CreateCustomerRequest createCustomer) {
        StatusResponse response = new StatusResponse();
        try {
            // Thực hiện validation
            Errors errors = validate.validateCreateCustomer(createCustomer);

            // Nếu có lỗi validation
            if ( errors.hasErrors() ) {
                throw new Exception( errors.getFieldError().getDefaultMessage() );
            }

            Optional<AccountEntity> accountOptional = accountService.findById(createCustomer.getAccountId());

            if ( accountOptional.isEmpty() ) {
                throw new Exception( "Account ID không tồn tại!" );
            }
            AccountEntity account = accountOptional.get();

//          Chuyển đổi từ request sang một entity để lưu
            CustomerEntity customer = new CustomerEntity();
            BeanUtils.copyProperties(createCustomer,customer);
            customer.setAccount(account);
//          Thêm mới 1 khách hàng
            customer = customerService.saveCustomer(customer);

//          Chuyển đổi từ Entity sang một response
//            AccountResponseType accountResponseType = convertAccountToAccountType(account);

//          Cập nhật trạng thái
            response.setMessage("Customer create successfully");
        } catch (Exception e) {
            // Xử lý lỗi và đặt giá trị lỗi vào phản hồi
            response.setMessage("Error: " + e.getMessage());
        }

        return response;
    }
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "loadAllCustomer")
    @ResponsePayload
    @Secured("ROLE_ADMIN")
    public CustomerResponseList loadAllCustomer(@RequestPayload GetCustomersRequest request) {

        CustomerResponseList responseList = new CustomerResponseList();
        int pageSize = Optional.ofNullable(request.getPageSize()).orElse(5);
        int pageIndex = Optional.ofNullable(request.getPageIndex()).orElse(0);

        if (pageSize < 1) {
            throw new RuntimeException("PageSize phải từ 1 trở lên!");
        }

//      Lấy tổng số khách hàng
        int totalRow =(int) customerService.totalRowFindAll();

//      Nếu không có khách hàng nào thì thông báo
        if(totalRow==0){
            throw new RuntimeException("Không tìm thấy khách hàng nào nào!");
        }
//      Nếu pageIndex vượt qua số hàng tìm được thì báo lỗi
        if(totalRow <= pageIndex * pageSize){
            throw new RuntimeException("Số trang không hợp lệ!");
        }

//Lấy danh sách record theo page index
        List<CustomerEntity> customers = customerService.findAll( (pageIndex * pageSize),pageSize );

//Chuyển đổi về XML
        List<CustomerResponseType> customerResponses = customers
                .stream()
                .map(customerEntity -> {
                    CustomerResponseType response = new CustomerResponseType();
                    BeanUtils.copyProperties(customerEntity, response);
                    return response;
                }).toList();

        responseList.setCustomerResponses(customerResponses);
        return responseList;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "searchCustomerRequest")
    @ResponsePayload
    @Secured({"ROLE_ADMIN" , "ROLE_STAFF"})
    public CustomerResponseList searchCustomer(@RequestPayload SearchCustomerRequest searchCustomerRequest) {
        CustomerResponseList responseList = new CustomerResponseList();
        int pageSize = Optional.ofNullable(searchCustomerRequest.getPageSize()).orElse(5);
        int pageIndex = Optional.ofNullable(searchCustomerRequest.getPageIndex()).orElse(0);

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
        if(totalRow <= pageIndex * pageSize){
            throw new RuntimeException("Số trang không hợp lệ!");
        }

//      Lấy danh sách record theo page index
        List<CustomerEntity> customers
                = customerService.search(
                searchCustomerRequest.getCustomerName()
                ,searchCustomerRequest.getPhoneNumber()
                ,(pageIndex * pageSize)
                ,pageSize);

//Chuyển đổi về XML
        List<CustomerResponseType> customerResponses = customers
                .stream()
                .map(customerEntity -> {
                    CustomerResponseType response = new CustomerResponseType();
                    BeanUtils.copyProperties(customerEntity,response);
                    return response;
                }).toList();

        responseList.setCustomerResponses(customerResponses);
        return responseList;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "deleteCustomerRequest")
    @ResponsePayload
    @Secured({"ROLE_ADMIN", "ROLE_STAFF"})
    public StatusResponse deleteCustomer(@RequestPayload DeleteCustomerRequest deleteCustomer) {
        StatusResponse response = new StatusResponse();
        try {
            Optional<AccountEntity> accountOptional = accountService.findById(deleteCustomer.getAccountId());

            if ( accountOptional.isEmpty() ) {
                throw new Exception( "Account ID không tồn tại!" );
            }
            AccountEntity account = accountOptional.get();

            boolean check =
                    customerService.deleteCustomer(
                            deleteCustomer.getCustomerId(),account);
            if(check){
//          Cập nhật trạng thái
                response.setMessage("Customer đã xóa thành công!");
            }else {
                response.setMessage("Bạn không có quyền để xóa!");
            }
        } catch (Exception e) {
            // Xử lý lỗi và đặt giá trị lỗi vào phản hồi
            response.setMessage("Error: " + e.getMessage());
        }

        return response;
    }

    @PayloadRoot(namespace = "http://yournamespace.com", localPart = "updateCustomerRequest")
    @ResponsePayload
    @Secured({"ROLE_ADMIN" , "ROLE_STAFF"})
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

            response.setStatus("Cập nhật sản phẩm thành công!");

        } catch (Exception e) {
            // Xử lý lỗi và đặt giá trị lỗi vào phản hồi
            response.setStatus(e.getMessage());
        }
        return response;
    }
}