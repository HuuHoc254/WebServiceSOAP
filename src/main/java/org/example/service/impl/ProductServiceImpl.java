package org.example.service.impl;

import org.example.dto.request.product.CreateProductRequest;
import org.example.dto.request.product.UpdateProductRequest;
import org.example.entity.ProductEntity;
import org.example.repository.ProductRepository;
import org.example.service.ProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

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
        productEntity.setInventory_quantity(0);
        productEntity.setVersion(0);
        productEntity.setIsDeleted(false);
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

    @Override
    public boolean deleteProduct(Integer productId) {
        return productRepository.deleteProduct(productId)!= 0;
    }

    @Override
    public boolean existsByProductNameAndProductIdNot(String productName, Integer productId) {
        return productRepository.existsByProductNameAndProductIdNot(productName,productId);
    }

    @Override
    public boolean existsByProductCodeAndProductIdNot(String productCode, Integer productId) {
        return productRepository.existsByProductCodeAndProductIdNot(productCode,productId);
    }

    @Override
    @Transactional
    public ProductEntity updateProduct(UpdateProductRequest request) {
        int rowUpdate = productRepository
                .updateProduct(
                        request.getProductId()
                        ,request.getProductCode()
                        ,request.getProductName()
                        ,request.getPurchasePrice()
                        ,request.getSalePrice()
                        ,request.getVersion()
                );

        if (rowUpdate==0) {
            throw new OptimisticLockingFailureException
                    ("Phiên bản không trùng khớp. Có thể đã có người cập nhật thông tin sản phẩm!.");
        }
        ProductEntity product = new ProductEntity();
        BeanUtils.copyProperties(request,product);
        product.setVersion(product.getVersion()+1);
        return product;
    }

    @Override
    public ProductEntity findByProductCode(String productCode) {
        return productRepository.findByProductCode(productCode).orElse(null);
    }

    @Override
    public ProductEntity findByProductName(String productName) {
        return productRepository.findByProductName(productName).orElse(null);
    }
}
