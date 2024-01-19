package org.example.validate.account;

import org.example.dto.request.CreateAccountRequest;
import org.example.dto.request.UpdateAccountRequest;
import org.example.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class AccountValidate {
    @Autowired
    private AccountRepository accountRepository;
    public void validateUpdateAccount(UpdateAccountRequest updateAccountRequest, Errors errors) {

        validatePhoneNumberAndPassword(
                updateAccountRequest.getPhoneNumber()
                ,updateAccountRequest.getPassword()
                ,updateAccountRequest.getConfirmPassword()
                ,errors);

        // Kiểm tra xem tên đăng nhập đã tồn tại cho người dùng khác không
        if (accountRepository.existsByAccountNameAndAccountIdNot(updateAccountRequest.getAccountName(), updateAccountRequest.getAccountId())) {
            errors.rejectValue("accountName", "duplicate.accountName", "Tên tài khoản đã tồn tại cho tài khoản khác.");
        }
        // Kiểm tra xem số điện thoại đã tồn tại cho người dùng khác không
        if (accountRepository.existsByPhoneNumberAndAccountIdNot(updateAccountRequest.getPhoneNumber(), updateAccountRequest.getAccountId())) {
            errors.rejectValue("phoneNumber", "duplicate.phoneNumber", "Số điện thoại đã tồn tại cho tài khoản khác.");
        }

    }

    public void validateCreateAccount(CreateAccountRequest createAccountRequest, Errors errors) throws Exception {
        validatePhoneNumberAndPassword(
                 createAccountRequest.getPhoneNumber()
                , createAccountRequest.getPassword()
                , createAccountRequest.getConfirmPassword()
                ,errors);

        // Kiểm tra xem tên đăng nhập đã tồn tại cho người dùng khác không
        if (accountRepository.existsByAccountName(createAccountRequest.getAccountName())) {
            errors.rejectValue("accountName", "duplicate.accountName", "Tên tài khoản đã tồn tại cho tài khoản khác.");
        }

        // Kiểm tra xem số điện thoại đã tồn tại cho người dùng khác không
        if (accountRepository.existsByPhoneNumber(createAccountRequest.getPhoneNumber())) {
            errors.rejectValue("phoneNumber", "duplicate.phoneNumber", "Số điện thoại đã tồn tại cho tài khoản khác.");
        }

    }

    private void validatePhoneNumberAndPassword(String phoneNumber, String password, String confirmPassword, Errors errors) {
        // Kiểm tra mật khẩu có ít nhất một chữ in hoa và một kí tự số
        if ( !password.matches( "^(?=.*[A-Z])(?=.*\\d).+$" ) ) {
            errors.rejectValue("password", "password.invalid", "Mật khẩu phải có ít nhất một chữ in hoa và một kí tự số.");
        }

        // Kiểm tra mật khẩu có ít nhất 8 kí tự
        if (password.length() < 8) {
            errors.rejectValue("password", "password.short", "Mật khẩu phải từ 8 kí tự trở lên.");
        }

        // Kiểm tra đúng định dạng số điện thoại )
        if (!phoneNumber.matches("^\\d{10,}$")) {
            errors.rejectValue("phoneNumber", "phoneNumber.invalid", "Định dạng số điện thoại không hợp lệ.");
        }

        // Kiểm tra điều kiện mật khẩu và xác nhận mật khẩu
        if (!password.equals(confirmPassword)) {
            errors.rejectValue("password", "password.mismatch", "Mật khẩu và xác nhận mật khẩu không khớp.");
        }
    }
}
