package org.example.validate.product;

import org.example.dto.request.product.CreateProductRequest;
import org.example.dto.request.product.UpdateProductRequest;
import org.example.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
@Component
public class ProductValidate {
    @Autowired
    private ProductService productService;
    public Errors validateCreateProduct(CreateProductRequest createProductRequest) {
        Errors errors = new BeanPropertyBindingResult(createProductRequest, "createProduct");
        validateProductCode(createProductRequest.getProductCode(),errors);
        validateProductName(createProductRequest.getProductName(),errors);
        return errors;
    }

    private void validateProductName(String productName, Errors errors) {
        boolean check = productService.existsByProductName(productName);
        if ( check ) {
            errors.rejectValue("productName", "duplicate.productName", "Tên sản phẩm đã tồn tại!.");
        }
    }

    private void validateProductCode(String productCode, Errors errors) {
        boolean check = productService.existsByProductCode(productCode);
        if ( check ) {
            errors.rejectValue("productCode", "duplicate.productCode", "Mã sản phẩm đã tồn tại!.");
        }
    }

    public Errors validateUpdateProduct(UpdateProductRequest request) {
        Errors errors = new BeanPropertyBindingResult(request, "updateProduct");
        boolean check = productService.
                existsByProductNameAndProductIdNot(request.getProductName(),request.getProductId());
        if ( check ) {
            errors.rejectValue("productName", "duplicate.productName", "Tên sản phẩm đã tồn tại!.");
        }

        check = productService.
                existsByProductCodeAndProductIdNot(request.getProductCode(),request.getProductId());
        if ( check ) {
            errors.rejectValue("productCode", "duplicate.productCode", "Mã sản phẩm đã tồn tại!.");
        }
        return errors;
    }
}
