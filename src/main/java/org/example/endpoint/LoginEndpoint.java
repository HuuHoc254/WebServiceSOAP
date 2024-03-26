package org.example.endpoint;

import org.apache.coyote.BadRequestException;
import org.example.dto.request.account.LoginRequest;
//import org.example.dto.request.account.LogoutRequest;
import org.example.dto.response.StatusResponse;
import org.example.entity.AccountEntity;
import org.example.security.UserDetailImpl;
import org.example.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Endpoint
public class LoginEndpoint {
    private static final String NAMESPACE_URI = "http://yournamespace.com";
    private static final String ADMIN = "ROLE_ADMIN";
    private static final String STAFF = "ROLE_STAFF";
    private final AuthenticationManager authenticationManager;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AccountService accountService;

    @Autowired
    public LoginEndpoint(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "loginRequest")
    @ResponsePayload
    public StatusResponse login(@RequestPayload LoginRequest loginRequest) throws BadRequestException {
        String accountName = loginRequest.getAccountName();
        String password = loginRequest.getPassword();

        // Tạo đối tượng Authentication với thông tin đăng nhập
        Authentication authentication = new UsernamePasswordAuthenticationToken(accountName, password);

        try {
            // Thực hiện xác thực thông tin đăng nhập
            authenticationManager.authenticate(authentication);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = passwordEncoder.encode(accountName + password + Math.random());
            AccountEntity accountEntity = accountService.findByAccountName(accountName);
            accountEntity.setToken(token);
            accountEntity.setIsOnline(true);
            accountService.createToken(accountEntity);

            return new StatusResponse("Login successful " +
                    ": token: "+token);
        } catch (AuthenticationException e) {
            // Xử lý ngoại lệ khi đăng nhập không thành công
            throw new BadRequestException();
        }
    }
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "logoutRequest")
    @ResponsePayload
    @Secured({ADMIN, STAFF})
    public StatusResponse logout() {
        UserDetailImpl userDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            accountService.logout(userDetail.getAccountId());
            return new StatusResponse("Logout success ");
        } catch (AuthenticationException e) {
            // Xử lý ngoại lệ khi đăng nhập không thành công
            return new StatusResponse("Logout Fail ");
        }
    }
}