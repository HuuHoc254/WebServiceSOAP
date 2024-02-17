package org.example.validate.allocation;

import org.example.dto.request.allocation.CreateAllocationRequest;
import org.example.entity.ProductEntity;
import org.example.model.CreateAllocationValidateDTO;
import org.example.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

@Component
public class AllocationValidate {
    @Autowired
    private ProductService productService;
    public CreateAllocationValidateDTO validateCreateOrder(CreateAllocationRequest request) {
        CreateAllocationValidateDTO dto = new CreateAllocationValidateDTO();
        Errors errors = new BeanPropertyBindingResult(request, "createAllocation");
        //      Tìm sản phẩm theo mã
        ProductEntity product = null;
        if( (request.getProductCode()!=null)
                && (!request.getProductCode().isEmpty()) )
        {
            product = productService.findByProductCode(request.getProductCode());
        }

//      Nếu không tìm thấy thì ghi nhận lỗi
        if(product==null){
            errors.rejectValue("productCode", "product.notFound", "Không tìm thấy thông tin sản phẩm!.");
        }else {
            dto.setProduct(product);
        }
        dto.setErrors(errors);
        return dto;
    }
}
