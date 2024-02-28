package org.example.endpoint;

import jakarta.xml.bind.ValidationException;
import org.example.dto.request.product.*;
import org.example.dto.response.ErrorTypeResponse;
import org.example.dto.response.StatusResponse;
import org.example.dto.response.product.*;
import org.example.entity.AccountEntity;
import org.example.entity.ProductEntity;
import org.example.security.UserDetailImpl;
import org.example.service.AccountService;
import org.example.service.ProductService;
import org.example.validate.product.ProductValidate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
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
public class ProductEndpoint {
    private final String NAMESPACE_URI = "http://yournamespace.com";
    private final String ADMIN = "ROLE_ADMIN";
    private final String STAFF = "ROLE_STAFF";
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductValidate validate;
    @Autowired
    private AccountService accountService;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "searchProductRequest")
    @ResponsePayload
    @Secured({ADMIN , STAFF})
    public ProductResponseList searchProduct(@RequestPayload SearchProductRequest searchProductRequest) {
        ProductResponseList responseList = new ProductResponseList();
        int pageSize = Optional.ofNullable(searchProductRequest.getPageSize()).orElse(5);
        int pageNumber = Optional.ofNullable(searchProductRequest.getPageNumber()).orElse(1);

        if (pageSize < 1) {
            throw new RuntimeException("PageSize phải từ 1 trở lên!");
        }

        if (pageNumber < 1) {
            throw new RuntimeException("PageNumber phải từ 1 trở lên!");
        }

        UserDetailImpl userDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AccountEntity account = accountService.findById(userDetail.getAccountId()).orElse(null);

//      Lấy tổng số hàng trong quá trình search
        int totalRow =  productService.totalRowSearch(
                                             searchProductRequest.getProductCode()
                                            ,searchProductRequest.getProductName()
                                            ,account.getRole().getRoleId()==1);

//      Nếu không tìm thấy thì thông báo
        if(totalRow==0){
            throw new RuntimeException("Không tìm thấy Product nào!");
        }

//      Lấy tổng số trang
        int totalPage = (int) Math.ceil( ((double) totalRow /pageSize) );

//      Nếu pageIndex vượt qua số hàng tìm được thì báo lỗi
        if(totalRow <= (pageNumber-1) * pageSize){
            throw new RuntimeException("Số trang không hợp lệ!");
        }

//      Lấy danh sách record theo page index
        List<Map<String,Object>> products
                                    = productService.search(
                                                searchProductRequest.getProductCode()
                                                ,searchProductRequest.getProductName()
                                                ,account.getRole().getRoleId()==1
                                                ,((pageNumber-1) * pageSize)
                                                ,pageSize);

//Chuyển đổi về XML
        List<ProductResponseType> productResponses = products
                .stream()
                .map(map ->{
                    return convertMapToProduct(map,(account.getRole().getRoleId()==1));
                }).toList();

        responseList.setProductResponses(productResponses);
        return responseList;
    }

    private ProductResponseType convertMapToProduct(Map<String, Object> map, boolean isAdmin) {
        ProductResponseType response = new ProductResponseType();
        response.setProductId((Integer) map.get("product_id"));
        response.setProductCode((String) map.get("product_code"));
        response.setProductName((String) map.get("product_name"));
        if(isAdmin){
            response.setPurchasePrice((Double) map.get("purchase_price"));
        }
        response.setSalePrice((Double) map.get("sale_price"));
        response.setIsDeleted((boolean) map.get("is_deleted"));
        response.setVersion((Integer) map.get("version"));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "createProductRequest")
    @ResponsePayload
    @Secured(ADMIN)
    public CreateProductResponse createProduct(@RequestPayload CreateProductRequest request) {
        CreateProductResponse response = new CreateProductResponse();
        try {
            // Thực hiện validation
            Errors errors = validate.validateCreateProduct(request);

            // Nếu có lỗi validation
            if (errors.hasErrors()) {
                List<ErrorTypeResponse> errorListResponse =
                        errors.getFieldErrors().stream().map(er ->{
                            ErrorTypeResponse errorTypeResponse = new ErrorTypeResponse();
                            errorTypeResponse.setErrorMessage(er.getDefaultMessage());
                            return errorTypeResponse;
                        }).collect(Collectors.toList());

                response.setErrorTypes(errorListResponse);
                throw new ValidationException("Lỗi Validate!");

            }

            ProductEntity product = productService.createProduct(request);

            ProductResponseType productResponseType = new ProductResponseType();
            BeanUtils.copyProperties(product,productResponseType);

            response.setProductResponseType(productResponseType);

            response.setStatus("Account create successfully");
        } catch (Exception e) {
            // Xử lý lỗi và đặt giá trị lỗi vào phản hồi
            response.setStatus("Error: " + e.getMessage());
        }

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "deleteProductRequest")
    @ResponsePayload
    @Secured({ADMIN})
    public StatusResponse deleteProduct(@RequestPayload DeleteProductRequest deleteProduct) {
        StatusResponse response = new StatusResponse();
        try {
            boolean check =
                    productService.deleteProduct(
                            deleteProduct.getProductId());
            if(check){
//          Cập nhật trạng thái
                response.setMessage("Product đã xóa thành công!");
            }else {
                response.setMessage("ProductId không tồn tại!");
            }
        } catch (Exception e) {
            // Xử lý lỗi và đặt giá trị lỗi vào phản hồi
            response.setMessage("Error: " + e.getMessage());
        }

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "updateProductRequest")
    @ResponsePayload
    @Secured(ADMIN)
    public UpdateProductResponse updateProduct(@RequestPayload UpdateProductRequest request) {
        UpdateProductResponse response = new UpdateProductResponse();

        try {
            // Thực hiện validation
            Errors errors = validate.validateUpdateProduct(request);

            // Nếu có lỗi validation
            if (errors.hasErrors()) {
                List<ErrorTypeResponse> errorListResponse =
                        errors.getFieldErrors().stream().map(er ->{
                            ErrorTypeResponse errorTypeResponse = new ErrorTypeResponse();
                            errorTypeResponse.setErrorMessage(er.getDefaultMessage());
                            return errorTypeResponse;
                        }).collect(Collectors.toList());

                response.setErrorTypes(errorListResponse);
                throw new ValidationException("Lỗi Validate!");
            }

            ProductEntity product = productService.updateProduct(request);

            ProductResponseType productResponseType = new ProductResponseType();
            BeanUtils.copyProperties(product,productResponseType);

            response.setProductResponseType(productResponseType);

            response.setStatus("Cập nhật sản phẩm thành công!");

        } catch (Exception e) {
            // Xử lý lỗi và đặt giá trị lỗi vào phản hồi
            response.setStatus(e.getMessage());
        }
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getProductCode")
    @ResponsePayload
    @Secured({ADMIN,STAFF})
    public ProductCode getProductCodeByProductName(@RequestPayload GetProductCode request) {
        ProductCode response = new ProductCode();
            String productCode = productService.getProductCodeByProductName(request.getProductName());
            response.setProductCode(productCode);
        return response;
    }
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getProductName")
    @ResponsePayload
    @Secured({ADMIN,STAFF})
    public ProductName getProductNameByProductCode(@RequestPayload GetProductName request) {
        ProductName response = new ProductName();
        String productName = productService.getProductNameByProductCode(request.getProductCode());
        response.setProductName(productName);
        return response;
    }
}
