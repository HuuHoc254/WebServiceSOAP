package org.example.endpoint;

import org.example.dto.request.account.*;
import org.example.dto.response.*;
import org.example.dto.response.account.AccountResponseList;
import org.example.dto.response.account.AccountResponseType;
import org.example.dto.response.account.AccountResponse;
import org.example.entity.AccountEntity;
import org.example.service.AccountService;
import org.example.validate.account.AccountValidate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BeanPropertyBindingResult;
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
public class AccountEndpoint {
    private final String ADMIN= "ROLE_ADMIN";
    private final String URL_NAMESPACE= "http://yournamespace.com";
    @Autowired
    private AccountService accountService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private AccountValidate validate;

    @PayloadRoot(namespace = URL_NAMESPACE, localPart = "deleteAccountRequest")
    @ResponsePayload
    @Secured(ADMIN)
    public StatusResponse deleteAccount(@RequestPayload DeleteAccountRequest request) {
        StatusResponse response = new StatusResponse();
        Optional<AccountEntity> optionalAccount = accountService.findById(request.getAccountId());
        try {
            if(optionalAccount.isEmpty()) throw new Exception("Account Id không tồn tại");
            AccountEntity account = optionalAccount.get();
            accountService.delete(account);
            response.setMessage("Đã xóa thành công!");
        }catch (Exception e) {
            // Xử lý lỗi và đặt giá trị lỗi vào phản hồi
            response.setMessage("Error: " + e.getMessage());
        }
        return response;
    }

    @PayloadRoot(namespace = "http://yournamespace.com", localPart = "createAccountRequest")
    @ResponsePayload
    @Secured(ADMIN)
    public AccountResponse createAccount(@RequestPayload CreateAccountRequest request) {
        AccountResponse response = new AccountResponse();

        try {
            // Thực hiện validation
            Errors errors = validate.validateCreateAccount(request);

            // Nếu có lỗi validation
            if (errors.hasErrors()) {
                List<ErrorTypeResponse> errorListResponse =
                errors.getFieldErrors().stream().map(er ->{
                    ErrorTypeResponse errorTypeResponse = new ErrorTypeResponse();
                    errorTypeResponse.setErrorMessage(er.getDefaultMessage());
                    return errorTypeResponse;
                }).collect(Collectors.toList());

                response.setErrorTypes(errorListResponse);
                throw new Exception(errors.getFieldError().getDefaultMessage());

            }

            request.setPassword(passwordEncoder.encode(request.getPassword()));

            int row = accountService.saveAccount(request);

            if(row==1){
                response.setStatus("Account create successfully");
                AccountResponseType accountResponseType = new AccountResponseType();
                BeanUtils.copyProperties(request,accountResponseType);

                response.setAccountResponseType(accountResponseType);
            }else {
                throw new Exception("Insert Fail!");
            }
        } catch (Exception e) {
            // Xử lý lỗi và đặt giá trị lỗi vào phản hồi
            response.setStatus("Error: " + e.getMessage());
        }

        return response;
    }

    private AccountResponseType convertEntityToResponse(Map<String,Object> map) {
        AccountResponseType response = new AccountResponseType();
        response.setAccountId((Integer) map.get("account_id"));
        response.setAccountName((String) map.get("account_name"));
        response.setFullName((String) map.get("full_name"));
        response.setPhoneNumber((String) map.get("phone_number"));
        response.setIsOnline((Boolean) map.get("is_online"));
        response.setIsDeleted((Boolean) map.get("is_deleted"));
        response.setVersion((Integer) map.get("version"));
        return response;
    }

    @PayloadRoot(namespace = "http://yournamespace.com", localPart = "updateAccountRequest")
    @ResponsePayload
    @Secured(ADMIN)
    public AccountResponse updateAccount(@RequestPayload UpdateAccountRequest request) {
        AccountResponse response = new AccountResponse();

        try {
            // Thực hiện validation
            Errors errors = validate.validateUpdateAccount(request);

            // Nếu có lỗi validation
            if (errors.hasErrors()) {
                List<ErrorTypeResponse> errorListResponse =
                        errors.getFieldErrors().stream().map(er ->{
                            ErrorTypeResponse errorTypeResponse = new ErrorTypeResponse();
                            errorTypeResponse.setErrorMessage(er.getDefaultMessage());
                            return errorTypeResponse;
                        }).collect(Collectors.toList());

                response.setErrorTypes(errorListResponse);
                throw new Exception("");
            }
            request.setPassword(passwordEncoder.encode(request.getPassword()));
            accountService.saveAccount(request);
            BeanUtils.copyProperties(request,response.getAccountResponseType());
            response.setStatus("SUCCESS!");

        } catch (Exception e) {
            response.setStatus("ERRORS!");
            // Xử lý lỗi và đặt giá trị lỗi vào phản hồi
        }
        return response;
    }

    @PayloadRoot(namespace = "http://yournamespace.com", localPart = "searchAccountRequest")
    @ResponsePayload
    @Secured(ADMIN)
    public AccountResponseList searchAccount(@RequestPayload SearchAccountRequest searchAccountRequest) {
        AccountResponseList responseList = new AccountResponseList();
        int pageSize = Optional.ofNullable(searchAccountRequest.getPageSize()).orElse(5);
        int pageNumber = Optional.ofNullable(searchAccountRequest.getPageNumber()).orElse(1);

        if (pageSize < 1) {
            throw new RuntimeException("PageSize phải từ 1 trở lên!");
        }
        if (pageNumber < 1) {
            throw new RuntimeException("pageNumber phải từ 1 trở lên!");
        }

//      Lấy tổng số hàng trong quá trình search
        int totalRow = accountService.totalRowSearch(
                                             searchAccountRequest.getAccountName()
                                            ,searchAccountRequest.getPhoneNumber()
                                            ,searchAccountRequest.getFullName());

//      Nếu không tìm thấy thì thông báo
        if(totalRow==0){
            throw new RuntimeException("Không tìm thấy account nào!");
        }

//      Lấy tổng số trang
        int totalPage = (int) Math.ceil( ((double) totalRow /pageSize) );

//      Nếu pageIndex vượt qua số hàng tìm được thì báo lỗi
        if(totalRow <= (pageNumber-1) * pageSize){
            throw new RuntimeException("Số trang không hợp lệ!");
        }

//      Lấy danh sách record theo page index
        List<Map<String,Object>> accounts
                = accountService.search(
                         searchAccountRequest.getAccountName()
                        ,searchAccountRequest.getPhoneNumber()
                        ,searchAccountRequest.getFullName()
                        ,((pageNumber-1) * pageSize)
                        ,pageSize);

//Chuyển đổi về XML
        List<AccountResponseType> accountResponses = accounts
                .stream()
                .map(this::convertEntityToResponse).toList();

        responseList.setAccountResponses(accountResponses);
        return responseList;
    }

}