package org.example.repository;

import org.example.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Integer> {
    @Query( value =   " SELECT"
                    + "     COUNT(*)"
                    + " FROM"
                    + "     product"
                    + " WHERE"
                    + "     product_code    LIKE CONCAT(:productCode, '%')"
                    + " AND product_name    LIKE CONCAT(:productName, '%')"
                    + " AND ("
                    + "         :isAdmin    = TRUE"
                    + "     OR  is_deleted  = FALSE"
                    +       ")"
            ,nativeQuery = true)
    int countSearch(
                     @Param("productCode")  String  productCode
                    ,@Param("productName")  String  productName
                    ,@Param("isAdmin")      boolean isAdmin
    );

    @Query( value =   " SELECT"
                    + "      product_id"
                    + ",     product_code"
                    + ",     product_name"
                    + ",     purchase_price"
                    + ",     sale_price"
                    + ",     inventory_quantity"
                    + ",     version"
                    + ",     is_deleted"
                    + " FROM"
                    + "      product"
                    + " WHERE"
                    + "      product_code LIKE CONCAT(:productCode, '%')"
                    + " AND  product_name LIKE CONCAT(:productName, '%')"
                    + " AND  ("
                    + "         :isAdmin    = TRUE"
                    + " OR      is_deleted  = FALSE"
                    +           ")"
                    + " ORDER BY CASE WHEN is_deleted = TRUE THEN 1 ELSE 0 END, product_name"
                    + " LIMIT :pageSize OFFSET :rowNumber"
            ,nativeQuery = true)
    List<Map<String,Object>> searchProduct(
                     @Param("productCode") String productCode
                    ,@Param("productName") String productName
                    ,@Param("isAdmin") boolean isAdmin
                    ,@Param("rowNumber") int rowNumber
                    ,@Param("pageSize") int pageSize
    );

    @Query(value =    " SELECT"
                    + "     COUNT(*)"
                    + " FROM"
                    + "     product"
                    + " WHERE"
                    + "     product_code = :productCode"
            ,nativeQuery = true)
    int existsByProductCode(@Param("productCode") String productCode);

    @Query(value =    " SELECT"
                    + "     COUNT(*)"
                    + " FROM"
                    + "     product"
                    + " WHERE"
                    + "     product_name = :productName"
            ,nativeQuery = true)
    int existsByProductName(@Param("productName") String productName);

    @Modifying
    @Query(value =    " UPDATE"
                    + "     product "
                    + " SET"
                    + "     product.is_deleted = true"
                    + " WHERE"
                    + "     product.product_id = :productId",
            nativeQuery = true)
    int deleteProduct(@Param("productId") Integer productId);

    @Query(value =    " SELECT"
                    + "     COUNT(*)"
                    + " FROM"
                    + "     product"
                    + " WHERE"
                    + "     product_name    = :productName"
                    + " AND product_id      <> :productId"
            ,nativeQuery = true)
    int existsByProductNameAndProductIdNot(
                     @Param("productName")  String  productName
                    ,@Param("productId")    Integer productId);

    @Query(value =    " SELECT"
                    + "     COUNT(*)"
                    + " FROM"
                    + "     product"
                    + " WHERE"
                    + "     product_code    = :productCode"
                    + " AND product_id      <> :productId"
            ,nativeQuery = true)
    int existsByProductCodeAndProductIdNot(
                     @Param("productCode")  String  productCode
                    ,@Param("productId")    Integer productId);

    @Modifying
    @Query( value =   " UPDATE"
                    + "   product"
                    + " SET   product_code    = :productCode"
                    + ",      product_Name    = :productName"
                    + ",      purchase_Price  = :purchasePrice"
                    + ",      sale_price      = :salePrice"
                    + ",      version         = version + 1"
                    + " WHERE "
                    + "       product_id      = :productId"
                    + " AND   version         = :version"
            ,nativeQuery = true)
    int updateProduct(
                     @Param("productId")     Integer productId
                    ,@Param("productCode")   String  productCode
                    ,@Param("productName")   String  productName
                    ,@Param("purchasePrice") Double  purchasePrice
                    ,@Param("salePrice")     Double  salePrice
                    ,@Param("version")       Integer version
    );

    @Query( value =     " SELECT"
                    +       "  product_id "
                    +       ", product_name "
                    +       ", product_code "
                    +       ", sale_price "
                    +   " FROM"
                    +       " product"
                    +   " WHERE"
                    +       " product_code  = :productCode"
                    +   " AND is_deleted    = FALSE"
            ,nativeQuery = true)
    Map<String,Object> findByProductCode(@Param("productCode") String productCode);

    @Query( value =     " SELECT"
                    +       "  product_id "
                    +       ", product_name "
                    +       ", product_code "
                    +       ", sale_price "
                    +   " FROM"
                    +       " product"
                    +   " WHERE"
                    +       " product_name  = :productName"
                    +   " AND is_deleted    = FALSE"
            ,nativeQuery = true)
    Map<String,Object> findByProductName(@Param("productName") String productName);

    @Modifying
    @Query( value =     " INSERT INTO"
                    +   "       product"
                    +                   " ("
                    +                       "  product_code"
                    +                       ", product_name"
                    +                       ", purchase_price"
                    +                       ", sale_price"
                    +                   " )"
                    +   " VALUE "
                    +       " ("
                    +           "  :productCode"
                    +           ", :productName"
                    +           ", :purchasePrice"
                    +           ", :salePrice"
                    +       " )"
            ,nativeQuery = true)
    int createProduct(@Param("productCode")     String productCode
                    , @Param("productName")     String productName
                    , @Param("purchasePrice")   Double purchasePrice
                    , @Param("salePrice")       Double salePrice);


}