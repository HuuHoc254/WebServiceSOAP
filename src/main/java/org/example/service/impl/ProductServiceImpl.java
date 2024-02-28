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
import java.util.Map;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Override
    public int totalRowSearch(String productCode, String productName, boolean isAdmin) {
        return productRepository.countSearch(productCode,productName,isAdmin);
    }

    @Override
    public List<Map<String,Object>> search(String productCode, String productName,boolean isAdmin, int rowNumber, int pageSize) {

        return productRepository.searchProduct(productCode,productName,isAdmin,rowNumber,pageSize);
    }

    @Override
    public ProductEntity createProduct(CreateProductRequest request) {
        ProductEntity product = new ProductEntity();
        BeanUtils.copyProperties(request,product);
        boolean check = productRepository.createProduct(
                                                 request.getProductCode()
                                                ,request.getProductName()
                                                ,request.getPurchasePrice()
                                                ,request.getSalePrice()
                                                        ) ==1;
        return product;
    }

    @Override
    public boolean existsByProductCode(String productCode) {
        return productRepository.existsByProductCode(productCode)==1;
    }

    @Override
    public boolean existsByProductName(String productName) {
        return productRepository.existsByProductName(productName)==1;
    }

    @Override
    public boolean deleteProduct(Integer productId) {
        return productRepository.deleteProduct(productId)!= 0;
    }

    @Override
    public boolean existsByProductNameAndProductIdNot(String productName, Integer productId) {
        return productRepository.existsByProductNameAndProductIdNot(productName,productId)==1;
    }

    @Override
    public boolean existsByProductCodeAndProductIdNot(String productCode, Integer productId) {
        return productRepository.existsByProductCodeAndProductIdNot(productCode,productId)==1;
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
        return convertMapToProduct(productRepository.findByProductCode(productCode));
    }

    @Override
    public ProductEntity findByProductName(String productName) {
        return convertMapToProduct(productRepository.findByProductName(productName));
    }

    @Override
    public String getProductCodeByProductName(String productName) {
        return convertMapToProduct(productRepository.findByProductName(productName)).getProductCode();
    }

    @Override
    public String getProductNameByProductCode(String productCode) {
        return convertMapToProduct(productRepository.findByProductCode(productCode)).getProductName();
    }

    private ProductEntity convertMapToProduct(Map<String, Object> map) {
        ProductEntity product = new ProductEntity();
        product.setProductId((Integer) map.get("product_id"));
        product.setProductCode((String) map.get("product_code"));
        product.setProductName((String) map.get("product_name"));
        product.setSalePrice((Double) map.get("sale_price"));
        return product;
    }
}
