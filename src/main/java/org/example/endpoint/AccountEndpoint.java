package org.example.endpoint;

import org.example.dto.request.*;
import org.example.dto.response.*;
import org.example.entity.AccountEntity;
import org.example.service.AccountService;
import org.example.validate.account.AccountValidate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// AccountEndpoint.java
@Endpoint
public class AccountEndpoint {
    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountValidate validate;

    @PayloadRoot(namespace = "http://yournamespace.com", localPart = "loadAllAccount")
    @ResponsePayload
    public AccountResponseList loadAllAccount(@RequestPayload GetAccountsRequest request) {
        AccountResponseList responseList = new AccountResponseList();
        Integer pageNumber = request.getPageNumber();
        Integer pageSize = request.getPageSize();
        if ( pageSize==null ){
            pageSize = 5;
        }else if ( pageSize<1){
            throw new RuntimeException("PageSize phải từ 1 trở lên!");
        }
        if ( pageNumber==null){
            pageNumber=0;
        }

        Page<AccountEntity> accountsPage = accountService.findAllAccount(PageRequest.of(pageNumber, pageSize));

        if ( accountsPage.getTotalPages() < pageNumber ) {
            accountsPage = accountService.findAllAccount(PageRequest.of(accountsPage.getTotalPages(), pageSize));
        }

        List<AccountEntity> listAccount
                = accountsPage.getContent().stream()
                .map(this::convertPageToEntity)
                .toList();
        List<AccountResponseType> accountResponses = new ArrayList<>();

        for ( AccountEntity accountEntity : listAccount ) {
            AccountResponseType response = convertEntityToResponse(accountEntity);
            accountResponses.add(response);
        }

        responseList.setAccountResponses(accountResponses);
        return responseList;
    }

    private AccountEntity convertPageToEntity(AccountEntity account) {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setAccountId(account.getAccountId());
        accountEntity.setAccountName(account.getAccountName());
        accountEntity.setFullName(account.getFullName());
        accountEntity.setPhoneNumber(account.getPhoneNumber());
        accountEntity.setIsOnline(account.getIsOnline());
        accountEntity.setIsDeleted(account.getIsDeleted());
        accountEntity.setVersion(account.getVersion());
        return accountEntity;
    }

    @PayloadRoot(namespace = "http://yournamespace.com", localPart = "deleteAccountRequest")
    @ResponsePayload
    public StatusResponse deleteAccount(@RequestPayload DeleteAccountRequest request) {
        StatusResponse response = new StatusResponse();
        Optional<AccountEntity> optionalAccount = accountService.findById(request.getAccountId());
        try {
            if(optionalAccount.isEmpty()) throw new Exception("Account Id không tồn tại");
            AccountEntity account = optionalAccount.get();
            accountService.delete(account);
            response.setStatus("Đã xóa thành công!");
        }catch (Exception e) {
            // Xử lý lỗi và đặt giá trị lỗi vào phản hồi
            response.setStatus("Error: " + e.getMessage());
        }
        return response;
    }

    @PayloadRoot(namespace = "http://yournamespace.com", localPart = "createAccountRequest")
    @ResponsePayload
    public CreateAccountResponse createAccount(@RequestPayload CreateAccountRequest request) {
        CreateAccountResponse response = new CreateAccountResponse();
        Errors errors = new BeanPropertyBindingResult(request, "request");

        try {
            // Thực hiện validation
            validate.validateCreateAccount(request, errors);

            // Nếu có lỗi validation
            if (errors.hasErrors()) {
                throw new Exception(errors.getFieldError().getDefaultMessage());
            }

            AccountEntity account = accountService.saveAccount(request);
            AccountResponseType accountResponseType = convertEntityToResponse(account);

            response.setAccountResponseType(accountResponseType);

            response.setStatus("Account create successfully");
        } catch (Exception e) {
            // Xử lý lỗi và đặt giá trị lỗi vào phản hồi
            response.setStatus("Error: " + e.getMessage());
        }

        return response;
    }

    private AccountResponseType convertEntityToResponse(AccountEntity account) {
        AccountResponseType accountResponseType = new AccountResponseType();
        accountResponseType.setAccountId(account.getAccountId());
        accountResponseType.setAccountName(account.getAccountName());
        accountResponseType.setFullName(account.getFullName());
        accountResponseType.setPhoneNumber(account.getPhoneNumber());
        accountResponseType.setIsOnline(account.getIsOnline());
        accountResponseType.setIsDeleted(account.getIsDeleted());
        accountResponseType.setVersion(account.getVersion());
        return accountResponseType;
    }


    @PayloadRoot(namespace = "http://yournamespace.com", localPart = "updateAccountRequest")
    @ResponsePayload
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

            // Kiểm tra xem người dùng có tồn tại không
            Optional<AccountEntity> existingAccountOptional = accountService.findById(accountUpdateRequest.getAccountId());
            if (existingAccountOptional.isEmpty()) {
                throw new Exception("Tài khoản không tồn tại");
            }

            // Thực hiện cập nhật thông tin người dùng
            AccountEntity existingAccount = existingAccountOptional.get();
            existingAccount.setPhoneNumber(accountUpdateRequest.getPhoneNumber());
            existingAccount.setAccountName(accountUpdateRequest.getAccountName());
            existingAccount.setPassword(accountUpdateRequest.getPassword());
            existingAccount.setVersion(accountUpdateRequest.getVersion());


            accountService.save(existingAccount);

            response.setStatus("Account updated successfully");

        } catch (Exception e) {
            // Xử lý lỗi và đặt giá trị lỗi vào phản hồi
            response.setStatus("Error: " + e.getMessage());
        }

        return response;
    }

    @PayloadRoot(namespace = "http://yournamespace.com", localPart = "searchAccountRequest")
    @ResponsePayload
    public AccountResponseList searchAccount(@RequestPayload SearchAccountRequest searchAccountRequest) {
        AccountResponseList responseList = new AccountResponseList();
        List<AccountEntity> listAccount = accountService.searchAccount(searchAccountRequest);
        List<AccountResponseType> accountResponses = new ArrayList<>();
        for (AccountEntity accountEntity : listAccount) {
            AccountResponseType response = convertEntityToResponse(accountEntity);
            accountResponses.add(response);
        }

        responseList.setAccountResponses(accountResponses);
        return responseList;
    }

}