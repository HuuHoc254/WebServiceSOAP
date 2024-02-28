package org.example.validate.account;

import org.example.dto.request.account.CreateAccountRequest;
import org.example.dto.request.account.UpdateAccountRequest;
import org.example.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

@Component
public class AccountValidate {
    @Autowired
    private AccountRepository accountRepository;
    public void validatePassword(String password, Errors errors){
        if("".equals(password)){
            errors.rejectValue("password", "" , "Password không được để trống!");
        } else if (password.length() < 8) {
            errors.rejectValue("password", "password.short", "Mật khẩu phải từ 8 kí tự trở lên.");
        } else if ( !password.matches( "^(?=.*[A-Z])(?=.*\\d).+$" ) ) {
            errors.rejectValue("password", "password.invalid", "Mật khẩu phải có ít nhất một chữ in hoa và một kí tự số.");
        }
    }

    public void validateConfirmPassword(String confirmPassword, String password,  Errors errors){
        // Kiểm tra điều kiện mật khẩu và xác nhận mật khẩu
        if (!password.equals(confirmPassword)) {
            errors.rejectValue("password", "password.mismatch", "Mật khẩu và xác nhận mật khẩu không khớp.");
        }
    }

    public void validatePhoneNumber(String phoneNumber, Errors errors){
        if("".equals(phoneNumber)){
            errors.rejectValue("phoneNumber", "" , "PhoneNumber không được để trống!");
        } else if (!phoneNumber.matches("^\\d{10}$")) {
                errors.rejectValue("phoneNumber", "phoneNumber.invalid", "Định dạng số điện thoại không hợp lệ.");
        } else if (accountRepository.existsByPhoneNumber(phoneNumber)==1) {
            errors.rejectValue("phoneNumber", "duplicate.phoneNumber", "Số điện thoại đã tồn tại cho tài khoản khác.");
        }
    }

    public void validateFullName(String fullName, Errors errors){
        if("".equals(fullName)){
            errors.rejectValue("fullName", "" , "FullName không được để trống!");
        }
    }
    public Errors validateUpdateAccount(UpdateAccountRequest request) {
        Errors errors = new BeanPropertyBindingResult(request, "accountUpdateRequest");
        validateFullName(request.getFullName(), errors);
        validatePassword(request.getPassword(),errors);
        validateConfirmPassword(request.getConfirmPassword(),request.getPassword(),errors);

        if("".equals(request.getPhoneNumber())){
            errors.rejectValue("phoneNumber", "" , "PhoneNumber không được để trống!");
        } else if (!request.getPhoneNumber().matches("^\\d{10}$")) {
            errors.rejectValue("phoneNumber", "phoneNumber.invalid", "Định dạng số điện thoại không hợp lệ.");
        }else if (accountRepository.existsByPhoneNumberAndAccountIdNot(request.getPhoneNumber(), request.getAccountId())==1) {
            errors.rejectValue("phoneNumber", "duplicate.phoneNumber", "Số điện thoại đã tồn tại cho tài khoản khác.");
        }

        if(request.getAccountName().isEmpty()){
            errors.rejectValue("accountName", "" , "Account Name không được để trống!");
        }else if (accountRepository.existsByAccountNameAndAccountIdNot(request.getAccountName(), request.getAccountId())==1) {
            errors.rejectValue("accountName", "duplicate.accountName", "Tên tài khoản đã tồn tại cho tài khoản khác.");
        }

        return errors;
    }
    public void validateAccountName(String accountName, Errors errors){
        if(accountName.isEmpty()){
            errors.rejectValue("accountName", "" , "Account Name không được để trống!");
        } else if (accountRepository.existsByAccountName(accountName)==1) {
            errors.rejectValue("accountName", "duplicate.accountName", "Tên tài khoản đã tồn tại cho tài khoản khác.");
        }
    }

    public Errors validateCreateAccount(CreateAccountRequest request){
        Errors errors = new BeanPropertyBindingResult(request, "request");
        validateAccountName(request.getAccountName(),errors);
        validatePassword(request.getPassword(), errors);
        validateConfirmPassword(request.getConfirmPassword(), request.getPassword(), errors);
        validateFullName(request.getFullName(), errors);
        validatePhoneNumber(request.getPhoneNumber(), errors);
        return errors;
    }

}
