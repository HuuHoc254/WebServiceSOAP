package org.example.repository;

import org.example.entity.AccountEntity;
import org.example.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity,Integer> {

    @Query(   "SELECT c FROM CustomerEntity c"
            + " ORDER BY c.customerId"
            + " LIMIT :pageSize OFFSET :rowNumber")
    List<CustomerEntity> findAll(
            @Param("rowNumber") int rowNumber
            ,@Param("pageSize") int pageSize );

    @Query(   "SELECT COUNT(c)"
            + " FROM CustomerEntity c"
            + " WHERE c.customerName LIKE CONCAT(:customerName, '%')"
            + " AND c.phoneNumber LIKE CONCAT(:phoneNumber, '%')")
    int countSearch(
             @Param("customerName") String customerName
            ,@Param("phoneNumber") String phoneNumber
    );

    @Query(   "SELECT c FROM CustomerEntity c"
            + " WHERE c.customerName LIKE CONCAT(:customerName, '%')"
            + " AND c.phoneNumber LIKE CONCAT(:phoneNumber, '%')"
            + " ORDER BY c.customerName"
            + " LIMIT :pageSize OFFSET :rowNumber")
    List<CustomerEntity> searchCustomer(
            @Param("customerName") String customerName,
            @Param("phoneNumber") String phoneNumber,
            @Param("rowNumber") int rowNumber,
            @Param("pageSize") int pageSize
    );

    @Modifying
    @Transactional
//    @Query("DELETE FROM CustomerEntity c " +
//            " WHERE c.customerId = :customerId " +
//            " AND (c.account = :account OR c.account.role.roleName = 'ROLE_ADMIN')")
    @Query(value = "UPDATE customer "
            + " INNER JOIN account ON customer.administrator_id = account.account_id"
            + " INNER JOIN role ON account.role_id = role.role_id"
            + " SET customer.is_deleted = true"
            + " WHERE customer.customer_id = :customerId"
            + " AND (customer.administrator_id = :accountId OR role.role_name = 'ROLE_ADMIN')",
            nativeQuery = true)
    int deleteCustomer(@Param("customerId") Integer customerId,
                             @Param("accountId") Integer accountId);

}
