package org.example.service.impl;

import org.example.dto.request.product.CreateProductRequest;
import org.example.entity.ProductEntity;
import org.example.repository.ProductRepository;
import org.example.service.ProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Override
    public long totalRowFindAll() {
        return productRepository.count();
    }

    @Override
    public List<ProductEntity> findAll(int rowNumber, int pageSize) {
        return productRepository.findAll(rowNumber,pageSize);
    }

    @Override
    public int totalRowSearch(String productCode, String productName) {
        return productRepository.countSearch(productCode,productName);
    }

    @Override
    public List<ProductEntity> search(String productCode, String productName, int rowNumber, int pageSize) {
        return productRepository.searchProduct(productCode,productName,rowNumber,pageSize);
    }

    @Override
    public ProductEntity createProduct(CreateProductRequest request) {
        ProductEntity productEntity = new ProductEntity();
        BeanUtils.copyProperties(request,productEntity);
        return productRepository.save(productEntity);
    }

    @Override
    public boolean existsByProductCode(String productCode) {
        return productRepository.existsByProductCode(productCode);
    }
    @Override
    public boolean existsByProductName(String productName) {
        return productRepository.existsByProductName(productName);
    }
}
