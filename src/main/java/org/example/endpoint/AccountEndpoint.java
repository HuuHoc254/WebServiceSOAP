package org.example.endpoint;

import org.example.dto.request.account.*;
import org.example.dto.response.*;
import org.example.dto.response.account.AccountResponseList;
import org.example.dto.response.account.AccountResponseType;
import org.example.dto.response.account.CreateAccountResponse;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Endpoint
public class AccountEndpoint {
    @Autowired
    private AccountService accountService;

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private AccountValidate validate;

    public AccountEndpoint() {
    }

    @PayloadRoot(namespace = "http://yournamespace.com", localPart = "loadAllAccount")
    @ResponsePayload
    @Secured("ROLE_ADMIN")
    public AccountResponseList loadAllAccount(@RequestPayload GetAccountsRequest request) {
        AccountResponseList responseList = new AccountResponseList();
        int pageSize = Optional.ofNullable(request.getPageSize()).orElse(5);
        int pageIndex = Optional.ofNullable(request.getPageIndex()).orElse(0);

        if (pageSize < 1) {
            throw new RuntimeException("PageSize phải từ 1 trở lên!");
        }

//      Lấy tổng số nhân viên
        int totalRow =(int) accountService.totalRowFindAll();

//      Nếu không có nhân viên thì thông báo
        if(totalRow==0){
            throw new RuntimeException("Không tìm thấy account nào!");
        }
//      Nếu pageIndex vượt qua số hàng tìm được thì báo lỗi
        if(totalRow <= pageIndex * pageSize){
            throw new RuntimeException("Số trang không hợp lệ!");
        }

//Lấy danh sách record theo page index
        List<AccountEntity> accounts = accountService.findAll( (pageIndex * pageSize),pageSize );

//Chuyển đổi về XML
        List<AccountResponseType> accountResponses = accounts
                .stream()
                .map(accountEntity -> {
                    AccountResponseType response = new AccountResponseType();
                    convertEntityToResponse(accountEntity, response);
                    return response;
                }).collect(Collectors.toList());

        responseList.setAccountResponses(accountResponses);

        responseList.setAccountResponses(accountResponses);
        return responseList;
    }

    @PayloadRoot(namespace = "http://yournamespace.com", localPart = "deleteAccountRequest")
    @ResponsePayload
    @Secured("ROLE_ADMIN")
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
    @Secured("ROLE_ADMIN")
    public CreateAccountResponse createAccount(@RequestPayload CreateAccountRequest request) {
        CreateAccountResponse response = new CreateAccountResponse();
        Errors errors = new BeanPropertyBindingResult(request, "request");

        try {
            // Thực hiện validation
            validate.validateCreateAccount(request, errors);

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

            AccountEntity account = accountService.saveAccount(request);

            AccountResponseType accountResponseType = new AccountResponseType();
            BeanUtils.copyProperties(account,accountResponseType);

            response.setAccountResponseType(accountResponseType);

            response.setStatus("Account create successfully");
        } catch (Exception e) {
            // Xử lý lỗi và đặt giá trị lỗi vào phản hồi
//            response.setStatus("Error: " + e.getMessage());
        }

        return response;
    }

    private void convertEntityToResponse(AccountEntity account, AccountResponseType response) {
        response.setAccountId(account.getAccountId());
        response.setAccountName(account.getAccountName());
        response.setFullName(account.getFullName());
        response.setPhoneNumber(account.getPhoneNumber());
        response.setIsOnline(account.getIsOnline());
        response.setIsDeleted(account.getIsDeleted());
        response.setVersion(account.getVersion());
    }

    @PayloadRoot(namespace = "http://yournamespace.com", localPart = "updateAccountRequest")
    @ResponsePayload
    @Secured("ROLE_ADMIN")
    public StatusResponse updateAccount(@RequestPayload UpdateAccountRequest accountUpdateRequest) {
        StatusResponse response = new StatusResponse();
        Errors errors = new BeanPropertyBindingResult(accountUpdateRequest, "accountUpdateRequest");

        try {
            // Thực hiện validation
            validate.validateUpdateAccount(accountUpdateRequest, errors);

            // Nếu có lỗi validation
            if (errors.hasErrors()) {
                throw new Exception(errors.getFieldError().getDefaultMessage());
            }
            accountUpdateRequest.setPassword(passwordEncoder.encode(accountUpdateRequest.getPassword()));
            accountService.saveAccount(accountUpdateRequest);

            response.setMessage("Account updated successfully");

        } catch (Exception e) {
            // Xử lý lỗi và đặt giá trị lỗi vào phản hồi
            response.setMessage("Error: " + e.getMessage());
        }
        return response;
    }

    @PayloadRoot(namespace = "http://yournamespace.com", localPart = "searchAccountRequest")
    @ResponsePayload
    @Secured("ROLE_ADMIN")
    public AccountResponseList searchAccount(@RequestPayload SearchAccountRequest searchAccountRequest) {
        AccountResponseList responseList = new AccountResponseList();
        int pageSize = Optional.ofNullable(searchAccountRequest.getPageSize()).orElse(5);
        int pageIndex = Optional.ofNullable(searchAccountRequest.getPageIndex()).orElse(0);

        if (pageSize < 1) {
            throw new RuntimeException("PageSize phải từ 1 trở lên!");
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
        if(totalRow <= pageIndex * pageSize){
            throw new RuntimeException("Số trang không hợp lệ!");
        }

//      Lấy danh sách record theo page index
        List<AccountEntity> accounts
                = accountService.search(
                         searchAccountRequest.getAccountName()
                        ,searchAccountRequest.getPhoneNumber()
                        ,searchAccountRequest.getFullName()
                        ,(pageIndex * pageSize)
                        ,pageSize);

//Chuyển đổi về XML
        List<AccountResponseType> accountResponses = accounts
                .stream()
                .map(accountEntity -> {
                    AccountResponseType response = new AccountResponseType();
                    convertEntityToResponse(accountEntity, response);
                    return response;
                }).collect(Collectors.toList());

        responseList.setAccountResponses(accountResponses);

        responseList.setAccountResponses(accountResponses);
        return responseList;
    }

}