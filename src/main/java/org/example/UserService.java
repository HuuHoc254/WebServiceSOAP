package org.example;

import jakarta.jws.WebService;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

// UserService.java
@Endpoint
@WebService
public class UserService {
    @PayloadRoot(namespace = "http://yournamespace.com", localPart = "UserRequest")
    @ResponsePayload
    public UserResponse loginUser(@RequestPayload UserRequest request) {
        UserResponse response = new UserResponse();

        // Kiểm tra thông tin đăng nhập, ví dụ đơn giản kiểm tra username và password
        if ("admin".equals(request.getUsername()) && "password".equals(request.getPassword())) {
            response.setStatus("SUCCESS");
        } else {
            response.setStatus("FAIL");
        }

        return response;
    }
}
