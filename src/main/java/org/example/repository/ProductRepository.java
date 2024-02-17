package org.example.repository;

import org.example.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Integer> {
    @Query(   "SELECT e FROM ProductEntity e"
            + " ORDER BY e.productId"
            + " LIMIT :pageSize OFFSET :rowNumber")
    List<ProductEntity> findAll(
            @Param("rowNumber") int rowNumber
            ,@Param("pageSize") int pageSize );

    @Query(   "SELECT COUNT(e)"
            + " FROM ProductEntity e"
            + " WHERE e.productCode LIKE CONCAT(:productCode, '%')"
            + " AND e.productName LIKE CONCAT(:productName, '%')")
    int countSearch(
            @Param("productCode") String productCode,
            @Param("productName") String productName
    );

    @Query(   "SELECT e FROM ProductEntity e"
            + " WHERE e.productCode LIKE CONCAT(:productCode, '%')"
            + " AND e.productName LIKE CONCAT(:productName, '%')"
            + " ORDER BY e.productName"
            + " LIMIT :pageSize OFFSET :rowNumber")
    List<ProductEntity> searchProduct(
            @Param("productCode") String productCode,
            @Param("productName") String productName,
            @Param("rowNumber") int rowNumber,
            @Param("pageSize") int pageSize
    );

    boolean existsByProductCode(String productCode);

    boolean existsByProductName(String productName);

    @Modifying
    @Query(value = "UPDATE product "
            + " SET product.is_deleted = true"
            + " WHERE product.product_id = :productId",
            nativeQuery = true)
    int deleteProduct(@Param("productId") Integer productId);

    boolean existsByProductNameAndProductIdNot(String productName, Integer productId);

    boolean existsByProductCodeAndProductIdNot(String productCode, Integer productId);

    @Modifying
    @Query(   "UPDATE ProductEntity e"
            + "  SET e.productCode = :productCode"
            + ", e.productName = :productName"
            + ", e.purchasePrice = :purchasePrice"
            + ", e.salePrice = :salePrice"
            + ", e.version = e.version + 1"
            + " WHERE e.productId = :productId AND e.version = :version")
    int updateProduct(
            @Param("productId") Integer productId,
            @Param("productCode") String productCode,
            @Param("productName") String productName,
            @Param("purchasePrice") Double purchasePrice,
            @Param("salePrice") Double salePrice,
            @Param("version") Integer version
    );

    Optional<ProductEntity> findByProductCode(String productCode);

    Optional<ProductEntity> findByProductName(String productName);
}