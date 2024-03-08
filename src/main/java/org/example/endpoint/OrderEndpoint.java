package org.example.endpoint;

import org.example.dto.request.order.DeleteOrderRequest;
import org.example.dto.request.order.SaveListOrderRequest;
import org.example.dto.request.order.SearchOrderRequest;
import org.example.dto.request.report.ReportCustomerZeroOrderRequest;
import org.example.dto.request.report.ReportProductBestSellerRequest;
import org.example.dto.request.report.ReportProductZeroOrderRequest;
import org.example.dto.response.ErrorTypeResponse;
import org.example.dto.response.StatusResponse;
import org.example.dto.response.customer.CustomerResponseList;
import org.example.dto.response.customer.CustomerResponseType;
import org.example.dto.response.order.ListSaveOrderResponse;
import org.example.dto.response.order.OrderResponseList;
import org.example.dto.response.order.OrderResponseType;
import org.example.dto.response.order.SaveOrderResponse;
import org.example.dto.response.report.ProductReportResponse;
import org.example.dto.response.report.ProductsReportResponse;
import org.example.entity.AccountEntity;
import org.example.model.ListSaveOrderDTO;
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

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Endpoint
public class OrderEndpoint {
    private static final String NAMESPACE_URI = "http://yournamespace.com";
    private static final String ADMIN = "ROLE_ADMIN";
    private static final String STAFF = "ROLE_STAFF";
    @Autowired
    private OrderService orderService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private OrderValidate validate;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy ");

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "saveListOrderRequest")
    @ResponsePayload
    @Secured({ADMIN,STAFF})
    public ListSaveOrderResponse saveOrder(@RequestPayload SaveListOrderRequest request) {
        ListSaveOrderResponse response = new ListSaveOrderResponse();
        UserDetailImpl userDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AccountEntity account = accountService.findById(userDetail.getAccountId()).orElse(null);

        try {
            // Thực hiện validation
            ListSaveOrderDTO dto = validate.validateSaveOrder(request, account);

            if(!dto.getListOrder().isEmpty()){
                List<SaveOrderResponse> list =
                dto.getListOrder().stream().map(er ->{
                    SaveOrderResponse saveOrderResponse = new SaveOrderResponse();
                    saveOrderResponse.setOrdinalNumber("Đơn hàng thứ "+ er.getOrdinalNumber());
                    saveOrderResponse.setErrors(er.getErrorTypes());
                    return saveOrderResponse;
                }).toList();
                response.setSaveOrderResponses(list);
                throw new Exception("Có lỗi validate!");
            }

            orderService.saveOrder(dto.getSql());

            response.setStatus("Tạo đơn hàng thành công!");
        } catch (Exception e) {
            // Xử lý lỗi và đặt giá trị lỗi vào phản hồi
            response.setStatus("Error: " + e.getMessage());
        }

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "searchOrderRequest")
    @ResponsePayload
    @Secured({ADMIN, STAFF})
    public OrderResponseList searchOrder(@RequestPayload SearchOrderRequest request) {
        OrderResponseList responseList = new OrderResponseList();

        try {
            int pageSize = Optional.ofNullable(request.getPageSize()).orElse(5);
            int pageNumber = Optional.ofNullable(request.getPageIndex()).orElse(1);

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

//      Nếu pageNumber vượt qua số hàng tìm được thì báo lỗi
            if(totalRow <= (pageNumber -1) * pageSize){
                throw new RuntimeException("Số trang không hợp lệ!");
            }

//      Lấy danh sách record theo page index
            List<Map<String, Object>> orders
                    = orderService.search(
                    request
                    ,account
                    ,( (pageNumber-1) * pageSize)
                    ,pageSize);
//
//Chuyển đổi về XML
            List<OrderResponseType> orderResponse = orders
                    .stream()
                    .map(order-> convertOrderToResponse(order,account.getRole().getRoleName())).toList();

            responseList.setOrderResponses(orderResponse);
        } catch (Exception e){
            responseList.setMessage(e.getMessage());
        }

        return responseList;
    }
    private OrderResponseType convertOrderToResponse(Map<String,Object> map, String role) {
        OrderResponseType response = new OrderResponseType();
        Timestamp orderDate = (Timestamp) map.get("orderDate");
        // Chuyển đổi các giá trị từ Map sang OrderResponseDTO
        response.setOrderDateTime(orderDate.toLocalDateTime().format(formatter));
        response.setProductCode((String) map.get("productCode"));
        response.setProductName((String) map.get("productName"));
        response.setUnitPrice((Double) map.get("unitPrice"));
        response.setQuantity((Integer) map.get("quantity"));
        response.setCustomerName((String) map.get("customerName"));
        response.setCustomerPhoneNumber((String) map.get("phoneNumber"));
        if(map.get("allocationDate") !=null) {
            Timestamp allocation = (Timestamp) map.get("allocationDate");
            response.setAllocationDateTime(allocation.toLocalDateTime().format(formatter));
        }
        response.setAddress((String) map.get("address"));
        response.setOrderStatusName((String) map.get("orderStatusName"));
        if(role.equals(ADMIN)){
            response.setAccountName((String) map.get("accountName"));
            response.setStaffFullName((String) map.get("staffName"));
        }
        // Trả về đối tượng OrderResponseDTO đã chuyển đổi
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "deleteOrderRequest")
    @ResponsePayload
    @Secured({ADMIN, STAFF})
    public StatusResponse deleteOrder(@RequestPayload DeleteOrderRequest request) {
        StatusResponse response = new StatusResponse();
        try {
            int result =
                    orderService.deleteOrder(
                            request.getOrderId());
            if(result>0){
//          Cập nhật trạng thái
                response.setMessage("Order đã xóa thành công!");
            }else {
                response.setMessage("OrderId không tồn tại!");
            }
        } catch (Exception e) {
            // Xử lý lỗi và đặt giá trị lỗi vào phản hồi
            response.setMessage("Error: " + e.getMessage());
        }

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "reportCustomerZeroOrderRequest")
    @ResponsePayload
    @Secured({ADMIN})
    public CustomerResponseList findCustomerWithoutOrders(@RequestPayload ReportCustomerZeroOrderRequest request) {
        CustomerResponseList responseList = new CustomerResponseList();
        int pageSize = Optional.ofNullable(request.getPageSize()).orElse(5);
        int pageNumber = Optional.ofNullable(request.getPageNumber()).orElse(1);

        if (pageSize < 1) {
            throw new RuntimeException("PageSize phải từ 1 trở lên!");
        }
        if (pageNumber < 1) {
            throw new RuntimeException("PageNumber phải từ 1 trở lên!");
        }

        if (request.getStartDate()==null || request.getStartDate().isEmpty()){
            request.setStartDate("2000-01-01");
        }

        if (request.getEndDate()==null || request.getEndDate().isEmpty()){
            request.setEndDate("3000-01-01");
        }

//      Lấy tổng số khách hàng chưa mua hàng
        int totalRecord = orderService
                            .totalFindCustomerZeroOrder(
                                     request.getStartDate()
                                    ,request.getEndDate() );

//      Nếu tất cả khách hàng đều mua rồi
        if(totalRecord==0){
            throw new RuntimeException("Tất cả khách hàng đều đã mua sản phẩm!");
        }
//      Nếu pageNumber vượt qua số hàng tìm được thì báo lỗi
        if( totalRecord <= (pageNumber-1) * pageSize ){
            throw new RuntimeException("Số trang không hợp lệ!");
        }

//      Lấy danh sách record theo page index
        List<Map<String,Object>> customerMap =
                orderService.findCustomerZeroOrder(
                                             request.getStartDate()
                                            ,request.getEndDate()
                                            ,((pageNumber-1) * pageSize)
                                            ,pageSize );

//      Chuyển đổi về XML
        List<CustomerResponseType>
                customerResponses = customerMap
                                            .stream()
                                            .map(this::convertCustomerToResponse)
                                            .toList();
        responseList.setCustomerResponses(customerResponses);
        return responseList;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "reportProductBestSellerRequest")
    @ResponsePayload
    @Secured({ADMIN})
    public ProductsReportResponse findProductBestSeller(@RequestPayload ReportProductBestSellerRequest request) {
        ProductsReportResponse responseList = new ProductsReportResponse();
        int pageSize = Optional.ofNullable(request.getPageSize()).orElse(5);
        int pageNumber = Optional.ofNullable(request.getPageNumber()).orElse(1);

        if (pageSize < 1) {
            throw new RuntimeException("PageSize phải từ 1 trở lên!");
        }
        if (pageNumber < 1) {
            throw new RuntimeException("PageNumber phải từ 1 trở lên!");
        }

        if (request.getStartDate()==null || request.getStartDate().isEmpty()){
            request.setStartDate("2000-01-01");
        }

        if (request.getEndDate()==null || request.getEndDate().isEmpty()){
            request.setEndDate("3000-01-01");
        }

//      Lấy tổng số khách hàng chưa mua hàng
        int totalRecord = orderService
                .totalFindProductBestSeller(
                        request.getStartDate()
                        ,request.getEndDate() );

//      Nếu tất cả khách hàng đều mua rồi
        if(totalRecord==0){
            throw new RuntimeException("Không có sản phẩm nào được bán trong khoản thời gian đó!");
        }
//      Nếu pageNumber vượt qua số hàng tìm được thì báo lỗi
        if( totalRecord <= (pageNumber-1) * pageSize ){
            throw new RuntimeException("Số trang không hợp lệ!");
        }

//      Lấy danh sách record theo page index
        List<Map<String,Object>> productMap =
                orderService.findProductBestSeller(
                        request.getStartDate()
                        ,request.getEndDate()
                        ,((pageNumber-1) * pageSize)
                        ,pageSize );

//      Chuyển đổi về XML
        List<ProductReportResponse>
                productResponses = productMap
                .stream()
                .map(this::convertProductToResponse)
                .toList();
        responseList.setProductsBestSeller(productResponses);
        return responseList;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "reportProductZeroOrderRequest")
    @ResponsePayload
    @Secured({ADMIN})
    public ProductsReportResponse findProductWithoutOrders(@RequestPayload ReportProductZeroOrderRequest request) {
        ProductsReportResponse responseList = new ProductsReportResponse();
        int pageSize = Optional.ofNullable(request.getPageSize()).orElse(5);
        int pageNumber = Optional.ofNullable(request.getPageNumber()).orElse(1);

        if (pageSize < 1) {
            throw new RuntimeException("PageSize phải từ 1 trở lên!");
        }
        if (pageNumber < 1) {
            throw new RuntimeException("PageNumber phải từ 1 trở lên!");
        }

        if (request.getStartDate()==null || request.getStartDate().isEmpty()){
            request.setStartDate("2000-01-01");
        }

        if (request.getEndDate()==null || request.getEndDate().isEmpty()){
            request.setEndDate("3000-01-01");
        }

//      Lấy tổng số khách hàng chưa mua hàng
        int totalRecord = orderService
                .totalFindProductZeroOrder(
                        request.getStartDate()
                        ,request.getEndDate() );

//      Nếu tất cả khách hàng đều mua rồi
        if(totalRecord==0){
            throw new RuntimeException("Tất cả khách hàng đều đã mua sản phẩm!");
        }
//      Nếu pageNumber vượt qua số hàng tìm được thì báo lỗi
        if( totalRecord <= (pageNumber-1) * pageSize ){
            throw new RuntimeException("Số trang không hợp lệ!");
        }

//      Lấy danh sách record theo page index
        List<Map<String,Object>> productMap =
                orderService.findProductZeroOrder(
                        request.getStartDate()
                        ,request.getEndDate()
                        ,((pageNumber-1) * pageSize)
                        ,pageSize );

//      Chuyển đổi về XML
        List<ProductReportResponse>
                productResponses = productMap
                .stream()
                .map(this::convertProductToResponse)
                .toList();
        responseList.setProductsBestSeller(productResponses);
        return responseList;
    }

    private ProductReportResponse convertProductToResponse(Map<String, Object> map) {
        ProductReportResponse response = new ProductReportResponse();
        response.setProductId((Integer) map.get("product_id"));
        response.setProductCode((String) map.get("product_code"));
        response.setProductName((String) map.get("product_name"));
        if(map.get("quantity") != null){
            response.setQuantitySale(((BigDecimal) map.get("quantity")).intValue());
        }
        return response;
    }

    private CustomerResponseType convertCustomerToResponse(Map<String,Object> map) {
        CustomerResponseType response = new CustomerResponseType();
        response.setCustomerId((Integer) map.get("customer_id"));
        response.setCustomerName((String) map.get("customer_name"));
        response.setPhoneNumber((String) map.get("phone_number"));
        response.setAddress((String) map.get("address"));
        return response;
    }
}