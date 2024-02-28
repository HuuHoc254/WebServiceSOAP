package org.example.endpoint;

import org.example.dto.request.allocation.CreateAllocationRequest;
import org.example.dto.response.StatusResponse;
import org.example.model.CreateAllocationValidateDTO;
import org.example.service.AllocationService;
import org.example.validate.allocation.AllocationValidate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Endpoint
public class AllocationEndpoint {
    private static final String NAMESPACE_URI = "http://yournamespace.com";
    private static final String ADMIN = "ROLE_ADMIN";
    @Autowired
    private AllocationValidate validate;
    @Autowired
    private AllocationService allocationService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy ");

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "createAllocationRequest")
    @ResponsePayload
    @Secured({ADMIN})
    public StatusResponse createAllocation(@RequestPayload CreateAllocationRequest request) {
        StatusResponse response = new StatusResponse();

        try {
            // Thực hiện validation
            CreateAllocationValidateDTO dto = validate.validateCreateAllocation(request);

//             Nếu có lỗi validation
            if (dto.getErrors().hasErrors()) {
                throw new Exception(Objects.requireNonNull(dto.getErrors().getFieldError()).getDefaultMessage());
            }
            allocationService.allocation(dto.getProduct().getProductId(),request.getQuantity());

            response.setMessage("Phân bổ thành công!");
        } catch (Exception e) {
            // Xử lý lỗi và đặt giá trị lỗi vào phản hồi
            response.setMessage(e.getMessage());
        }

        return response;
    }
}
