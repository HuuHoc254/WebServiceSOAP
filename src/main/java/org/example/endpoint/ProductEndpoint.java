package org.example.endpoint;

import jakarta.xml.bind.ValidationException;
import org.example.dto.request.product.*;
import org.example.dto.response.ErrorTypeResponse;
import org.example.dto.response.StatusResponse;
import org.example.dto.response.product.CreateProductResponse;
import org.example.dto.response.product.ProductResponseList;
import org.example.dto.response.product.ProductResponseType;
import org.example.dto.response.product.UpdateProductResponse;
import org.example.entity.ProductEntity;
import org.example.service.ProductService;
import org.example.validate.product.ProductValidate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.Errors;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Endpoint
public class ProductEndpoint {
    private static final String NAMESPACE_URI = "http://yournamespace.com";
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductValidate validate;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "loadAllProduct")
    @ResponsePayload
    @Secured({"ROLE_ADMIN","ROLE_STAFF"})
    public ProductResponseList loadAllProduct(@RequestPayload GetProductsRequest request) {
            ProductResponseList responseList = new ProductResponseList();
        int pageSize = Optional.ofNullable(request.getPageSize()).orElse(5);
        int pageIndex = Optional.ofNullable(request.getPageIndex()).orElse(0);

        if (pageSize < 1) {
            throw new RuntimeException("PageSize phải từ 1 trở lên!");
        }

//      Lấy tổng số sản phẩm
        int totalRow =(int) productService.totalRowFindAll();

//      Nếu không có sản phẩm thì thông báo
        if(totalRow==0){
            throw new RuntimeException("Không tìm thấy product nào!");
        }
//      Nếu pageIndex vượt qua số hàng tìm được thì báo lỗi
        if(totalRow <= pageIndex * pageSize){
            throw new RuntimeException("Số trang không hợp lệ!");
        }

//Lấy danh sách record theo page index
        List<ProductEntity> products = productService.findAll( (pageIndex * pageSize),pageSize );

//Chuyển đổi về XML
        List<ProductResponseType> productResponses = products
                .stream()
                .map(productEntity -> {
                    ProductResponseType response = new ProductResponseType();
                    BeanUtils.copyProperties(productEntity,response);
                    return response;
                }).toList();
        responseList.setProductResponses(productResponses);
        return responseList;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "searchProductRequest")
    @ResponsePayload
    @Secured({"ROLE_ADMIN" , "ROLE_STAFF"})
    public ProductResponseList searchProduct(@RequestPayload SearchProductRequest searchProductRequest) {
        ProductResponseList responseList = new ProductResponseList();
        int pageSize = Optional.ofNullable(searchProductRequest.getPageSize()).orElse(5);
        int pageIndex = Optional.ofNullable(searchProductRequest.getPageIndex()).orElse(0);

        if (pageSize < 1) {
            throw new RuntimeException("PageSize phải từ 1 trở lên!");
        }

//      Lấy tổng số hàng trong quá trình search
        int totalRow =  productService.totalRowSearch(
                searchProductRequest.getProductCode()
                ,searchProductRequest.getProductName());

//      Nếu không tìm thấy thì thông báo
        if(totalRow==0){
            throw new RuntimeException("Không tìm thấy Product nào!");
        }

//      Lấy tổng số trang
        int totalPage = (int) Math.ceil( ((double) totalRow /pageSize) );

//      Nếu pageIndex vượt qua số hàng tìm được thì báo lỗi
        if(totalRow <= pageIndex * pageSize){
            throw new RuntimeException("Số trang không hợp lệ!");
        }

//      Lấy danh sách record theo page index
        List<ProductEntity> products
                = productService.search(
                searchProductRequest.getProductCode()
                ,searchProductRequest.getProductName()
                ,(pageIndex * pageSize)
                ,pageSize);

//Chuyển đổi về XML
        List<ProductResponseType> productResponses = products
                .stream()
                .map(productEntity -> {
                    ProductResponseType response = new ProductResponseType();
                    BeanUtils.copyProperties(productEntity,response);
                    return response;
                }).toList();

        responseList.setProductResponses(productResponses);
        return responseList;
    }

    @PayloadRoot(namespace = "http://yournamespace.com", localPart = "createProductRequest")
    @ResponsePayload
    @Secured("ROLE_ADMIN")
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
    @Secured({"ROLE_ADMIN"})
    public StatusResponse deleteCustomer(@RequestPayload DeleteProductRequest deleteProduct) {
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

    @PayloadRoot(namespace = "http://yournamespace.com", localPart = "updateProductRequest")
    @ResponsePayload
    @Secured("ROLE_ADMIN")
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
}
