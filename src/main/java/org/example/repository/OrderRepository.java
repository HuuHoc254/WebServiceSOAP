package org.example.repository;

import org.example.entity.AccountEntity;
import org.example.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Integer> {
    @Query(value = "SELECT COUNT(*)"
            + " FROM orders"
            + " WHERE ( :isAdmin = true OR ( account_id = :accountId AND is_deleted = FALSE))", nativeQuery = true )
    int totalRecordFindAll( @Param("isAdmin") boolean isAdmin
                            ,@Param("accountId") Integer accountId);

    @Query(   "SELECT e FROM OrderEntity e"
            + " WHERE (:isAdmin=true OR (e.account = :account AND e.isDeleted = FALSE ) )"
            + " ORDER BY CASE WHEN e.isDeleted = TRUE THEN 1 ELSE 0 END,e.orderDate DESC"
            + " LIMIT :pageSize OFFSET :recordNumber")
    List<OrderEntity> findAll(
            @Param("isAdmin") boolean isAdmin
            ,@Param("account") AccountEntity account
            ,@Param("recordNumber") int recordNumber
            ,@Param("pageSize") int pageSize );
    @Query(   "SELECT COUNT(e)"
            + " FROM OrderEntity e"
            + " WHERE e.product.productCode LIKE CONCAT(:productCode, '%')"
            + " AND e.product.productName LIKE CONCAT(:productName, '%')"
            + " AND e.account.accountName LIKE CONCAT(:accountName, '%')"
            + " AND e.account.fullName LIKE CONCAT(:staffFullName, '%')"
            + " AND e.customer.customerName LIKE CONCAT(:customerName, '%')"
            + " AND e.customer.phoneNumber LIKE CONCAT(:customerPhoneNumber, '%')"
            + " AND e.orderDate BETWEEN :beginOrderDate AND :endOrderDate"
            + " AND e.orderStatus.orderStatusId = :orderStatusId"
            + " AND (:isAdmin = true OR (e.account.accountId = :accountId AND e.isDeleted = FALSE))"
    )
    int totalRecordSearch(
             @Param("accountId") Integer accountId
            ,@Param("accountName") String accountName
            ,@Param("staffFullName") String staffFullName
            ,@Param("productCode") String productCode
            ,@Param("productName") String productName
            ,@Param("customerName") String customerName
            ,@Param("customerPhoneNumber") String customerPhoneNumber
            ,@Param("beginOrderDate") LocalDateTime beginOrderDate
            ,@Param("endOrderDate") LocalDateTime endOrderDate
            ,@Param("orderStatusId") Integer orderStatusId
            ,@Param("isAdmin") boolean isAdmin );

    @Query(   "SELECT e FROM OrderEntity e"
            + " WHERE e.product.productCode LIKE CONCAT(:productCode, '%')"
            + " AND e.product.productName LIKE CONCAT(:productName, '%')"
            + " AND e.account.accountName LIKE CONCAT(:accountName, '%')"
            + " AND e.account.fullName LIKE CONCAT(:staffFullName, '%')"
            + " AND e.customer.customerName LIKE CONCAT(:customerName, '%')"
            + " AND e.customer.phoneNumber LIKE CONCAT(:customerPhoneNumber, '%')"
            + " AND e.orderDate BETWEEN :beginOrderDate AND :endOrderDate"
            + " AND e.orderStatus.orderStatusId = :orderStatusId"
            + " AND (:isAdmin = true OR (e.account.accountId = :accountId AND e.isDeleted = FALSE))"
            + " ORDER BY CASE WHEN e.isDeleted = TRUE THEN 1 ELSE 0 END, e.orderDate DESC"
            + " LIMIT :pageSize OFFSET :recordNumber"
    )
    List<OrderEntity> search(
            @Param("accountId") Integer accountId
            ,@Param("accountName") String accountName
            ,@Param("staffFullName") String staffFullName
            ,@Param("productCode") String productCode
            ,@Param("productName") String productName
            ,@Param("customerName") String customerName
            ,@Param("customerPhoneNumber") String customerPhoneNumber
            ,@Param("beginOrderDate") LocalDateTime beginOrderDate
            ,@Param("endOrderDate") LocalDateTime endOrderDate
            ,@Param("orderStatusId") Integer orderStatusId
            ,@Param("recordNumber") Integer recordNumber
            ,@Param("pageSize") Integer pageSize
            ,@Param("isAdmin") boolean isAdmin  );

    @Modifying
    @Query( value =   " UPDATE orders"
                    + " SET product_id = :productId"
                    + ", customer_id = :customerId"
                    + ", quantity = :quantity"
                    + ", version = version + 1"
                    + " WHERE order_id = :orderId"
                    + " AND version = :version"
                    + " AND is_deleted  = FALSE"
                    + " AND order_status_id = 1"
            ,nativeQuery = true
    )
    int updateOrder(
            @Param("orderId") Integer orderId
            ,@Param("productId") Integer productId
            ,@Param("customerId") Integer customerId
            ,@Param("quantity") Integer quantity
            ,@Param("version") Integer version);
    @Modifying
    @Query( value = " UPDATE orders "
                  + " SET orders.is_deleted = true"
                  + " WHERE orders.order_id = :orderId "
                  + " AND is_delete = FALSE"
                  + " AND order_status_id = 1",
                  nativeQuery = true)
    int deleteOrder(@Param("orderId") Integer orderId);
}
