package org.example.endpoint;

import org.example.dto.request.order.CreateOrderRequest;
import org.example.dto.request.order.GetOrdersRequest;
import org.example.dto.request.order.SearchOrderRequest;
import org.example.dto.response.ErrorTypeResponse;
import org.example.dto.response.order.CreateOrderResponse;
import org.example.dto.response.order.OrderResponseList;
import org.example.dto.response.order.OrderResponseType;
import org.example.entity.AccountEntity;
import org.example.entity.OrderEntity;
import org.example.model.CreateOrderValidateDTO;
import org.example.security.UserDetailImpl;
import org.example.service.AccountService;
import org.example.service.OrderService;
import org.example.validate.order.OrderValidate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Endpoint
public class OrderEndpoint {
    private static final String NAMESPACE_URI = "http://yournamespace.com";

    @Autowired
    private OrderService orderService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private OrderValidate validate;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy ");

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "createOrderRequest")
    @ResponsePayload
    @Secured({"ROLE_ADMIN","ROLE_STAFF"})
    public CreateOrderResponse createOrder(@RequestPayload CreateOrderRequest request) {
        CreateOrderResponse response = new CreateOrderResponse();

        try {
            // Thực hiện validation
            CreateOrderValidateDTO dto = validate.validateCreateOrder(request);

//             Nếu có lỗi validation
            if (dto.getErrors().hasErrors()) {
                List<ErrorTypeResponse> errorListResponse =
                        dto.getErrors().getFieldErrors().stream().map(er ->{
                            ErrorTypeResponse errorTypeResponse = new ErrorTypeResponse();
                            errorTypeResponse.setErrorMessage(er.getDefaultMessage());
                            return errorTypeResponse;
                        }).collect(Collectors.toList());
                response.setErrorTypes(errorListResponse);
                throw new Exception("Có lỗi validate!");
            }

            OrderEntity order = orderService.createOrder(dto.getCustomer(),dto.getProduct(),request.getQuantity());

            OrderResponseType orderResponseType = convertOrderToResponse(order);

            response.setOrderResponseType(orderResponseType);

            response.setStatus("Tạo đơn hàng thành công!");
        } catch (Exception e) {
            // Xử lý lỗi và đặt giá trị lỗi vào phản hồi
            response.setStatus("Error: " + e.getMessage());
        }

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "loadAllOrder")
    @ResponsePayload
    @Secured({"ROLE_ADMIN","ROLE_STAFF"})
    public OrderResponseList loadAllOrder(@RequestPayload GetOrdersRequest request) {
        OrderResponseList responseList = new OrderResponseList();
        int pageSize = Optional.ofNullable(request.getPageSize()).orElse(5);
        int pageIndex = Optional.ofNullable(request.getPageIndex()).orElse(0);

        if (pageSize < 1) {
            throw new RuntimeException("PageSize phải từ 1 trở lên!");
        }

        UserDetailImpl userDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AccountEntity account = accountService.findById(userDetail.getAccountId()).orElse(null);
//      Lấy tổng số sản phẩm
        int totalRecord = orderService.totalRecordFindAll(account);

//      Nếu không có sản phẩm thì thông báo
        if(totalRecord==0){
            throw new RuntimeException("Không tìm thấy product nào!");
        }
//      Nếu pageIndex vượt qua số hàng tìm được thì báo lỗi
        if(totalRecord <= pageIndex * pageSize){
            throw new RuntimeException("Số trang không hợp lệ!");
        }

//      Lấy danh sách record theo page index
        List<OrderEntity> orders = orderService.findAll( account,(pageIndex * pageSize),pageSize );

//      Chuyển đổi về XML
        List<OrderResponseType> orderResponses = orders
                .stream()
                .map(this::convertOrderToResponse).toList();
        responseList.setOrderResponses(orderResponses);
        return responseList;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "searchOrderRequest")
    @ResponsePayload
    @Secured({"ROLE_ADMIN" , "ROLE_STAFF"})
    public OrderResponseList searchOrder(@RequestPayload SearchOrderRequest request) {
        OrderResponseList responseList = new OrderResponseList();

        try {
            int pageSize = Optional.ofNullable(request.getPageSize()).orElse(5);
            int pageIndex = Optional.ofNullable(request.getPageIndex()).orElse(0);

            if (pageSize < 1) {
                throw new RuntimeException("PageSize phải từ 1 trở lên!");
            }

            UserDetailImpl userDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            AccountEntity account = accountService.findById(userDetail.getAccountId()).orElse(null);

            Errors errors = validate.validateSearch(request,account);
            if (errors.hasErrors()) {
                List<ErrorTypeResponse> errorListResponse =
                        errors.getFieldErrors().stream().map(er ->{
                            ErrorTypeResponse errorTypeResponse = new ErrorTypeResponse();
                            errorTypeResponse.setErrorMessage(er.getDefaultMessage());
                            return errorTypeResponse;
                        }).toList();
                responseList.setErrorTypes(errorListResponse);
                throw new Exception("Có lỗi validate!");
            }

//      Lấy tổng số hàng trong quá trình search
            int totalRow =
                    orderService.totalRecordSearch(request,account);

//      Nếu không tìm thấy thì thông báo
            if(totalRow==0){
                throw new RuntimeException("Không tìm thấy đơn hàng nào!");
            }

//      Lấy tổng số trang
            int totalPage = (int) Math.ceil( ((double) totalRow /pageSize) );

//      Nếu pageIndex vượt qua số hàng tìm được thì báo lỗi
            if(totalRow <= pageIndex * pageSize){
                throw new RuntimeException("Số trang không hợp lệ!");
            }

//      Lấy danh sách record theo page index
            List<OrderEntity> orders
                    = orderService.search(
                    request
                    ,account
                    ,(pageIndex * pageSize)
                    ,pageSize);

//Chuyển đổi về XML
            List<OrderResponseType> orderResponse = orders
                    .stream()
                    .map(this::convertOrderToResponse).toList();

            responseList.setOrderResponses(orderResponse);
        } catch (Exception e){
            responseList.setMessage(e.getMessage());
        }

        return responseList;
    }
    private OrderResponseType convertOrderToResponse(OrderEntity order) {
        OrderResponseType response = new OrderResponseType();
        response.setOrderId(order.getOrderId());
        response.setOrderDateTime(order.getOrderDate().format(formatter));
        response.setProductCode(order.getProduct().getProductCode());
        response.setProductName(order.getProduct().getProductName());
        response.setUnitPrice(order.getUnitPrice());
        response.setQuantity(order.getQuantity());
        response.setCustomerName(order.getCustomer().getCustomerName());
        response.setCustomerPhoneNumber(order.getPhoneNumberCustomer());
        response.setAddress(order.getAddressCustomer());
        response.setOrderStatusName(order.getOrderStatus().getOrderStatusName());
        if(order.getAllocationDate()!=null){
            response.setAllocationDateTime(order.getAllocationDate().format(formatter));
        }
        response.setAccountName(order.getAccount().getAccountName());
        response.setStaffFullName(order.getAccount().getFullName());
        response.setVersion(order.getVersion());
        response.setIsDeleted(order.getIsDeleted());
        return response;
    }
}