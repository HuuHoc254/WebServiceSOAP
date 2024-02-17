package org.example.service;

import org.example.dto.request.product.CreateProductRequest;
import org.example.dto.request.product.UpdateProductRequest;
import org.example.entity.ProductEntity;

import java.util.List;

public interface ProductService {
    long totalRowFindAll();
    List<ProductEntity> findAll(int rowNumber, int pageSize);

    int totalRowSearch(String productCode, String productName);

    List<ProductEntity> search(String productCode, String productName, int rowNumber, int pageSize);

    ProductEntity createProduct(CreateProductRequest request);

    boolean existsByProductCode(String productCode);

    boolean existsByProductName(String productName);

    boolean deleteProduct(Integer productId);

    boolean existsByProductNameAndProductIdNot(String productName, Integer productId);

    boolean existsByProductCodeAndProductIdNot(String productCode, Integer productId);

    ProductEntity updateProduct(UpdateProductRequest request);

    ProductEntity findByProductCode(String productCode);

    ProductEntity findByProductName(String productName);
}
