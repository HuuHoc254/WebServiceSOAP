package org.example.service;

import org.example.dto.request.product.CreateProductRequest;
import org.example.dto.request.product.UpdateProductRequest;
import org.example.entity.ProductEntity;

import java.util.List;
import java.util.Map;

public interface ProductService {
    int totalRowSearch(String productCode, String productName, boolean isAdmin);

    List<Map<String,Object>> search(
                                    String  productCode
                                ,   String  productName
                                ,   boolean isAdmin
                                ,   int     rowNumber
                                ,   int     pageSize);

    ProductEntity createProduct(CreateProductRequest request);

    boolean existsByProductCode(String productCode);

    boolean existsByProductName(String productName);

    boolean deleteProduct(Integer productId);

    boolean existsByProductNameAndProductIdNot(String productName, Integer productId);

    boolean existsByProductCodeAndProductIdNot(String productCode, Integer productId);

    ProductEntity updateProduct(UpdateProductRequest request);

    ProductEntity findByProductCode(String productCode);

    ProductEntity findByProductName(String productName);

    String getProductCodeByProductName(String productName);

    String getProductNameByProductCode(String productCode);
}
