package org.example.repository;

import org.example.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity,Integer> {

    @Query(   value = " SELECT"
                    + "     COUNT(*)"
                    + " FROM"
                    + "     customer"
                    + " WHERE"
                    + "     customer_name   LIKE CONCAT(:customerName, '%')"
                    + " AND phone_number    LIKE CONCAT(:phoneNumber, '%')"
            ,nativeQuery = true)
    int countSearch(
             @Param("customerName") String customerName
            ,@Param("phoneNumber")  String phoneNumber
    );

    @Query( value =   " SELECT"
                    + "     c.customer_id"
                    + ",    c.customer_name"
                    + ",    c.phone_number"
                    + ",    c.address"
                    + ",    c.version"
                    + ",    c.is_deleted"
                    + ",    a.account_name"
                    + " FROM"
                    + "     customer c"
                    + " JOIN account a ON a.account_id = c.administrator_id"
                    + " WHERE"
                    + "        c.customer_name  LIKE CONCAT(:customerName, '%')"
                    + " AND    c.phone_number   LIKE CONCAT(:phoneNumber, '%')"
                    + " ORDER BY "
                    + "     CASE"
                    + "         WHEN c.is_deleted = TRUE"
                    + "         THEN 1 ELSE 0 END"
                    + ",    SUBSTRING_INDEX(c.customer_name, ' ', -1)"
                    + ",    c.customer_id"
                    + " LIMIT :pageSize OFFSET :rowNumber"
            ,nativeQuery = true)
    List<Map<String,Object>> searchCustomer(
            @Param("customerName")  String customerName,
            @Param("phoneNumber")   String phoneNumber,
            @Param("rowNumber")     int     rowNumber,
            @Param("pageSize")      int     pageSize
    );

    @Modifying
    @Query(value =    " UPDATE"
                    + "     customer"
                    + " JOIN    account    ON customer.administrator_id     = account.account_id"
                    + " SET     customer.is_deleted                         = true"
                    + " WHERE   customer.customer_id                        = :customerId"
                    + " AND"
                    + "     ("
                    +           "   customer.administrator_id                = :accountId "
                    + "          OR :isAdmin                                 = TRUE"
                    + "     )"
            ,nativeQuery = true)
    int deleteCustomer(
             @Param("customerId")   Integer customerId
            ,@Param("accountId")    Integer accountId
            ,@Param("isAdmin")      boolean isAdmin);

    @Modifying
    @Query(value =    " UPDATE"
                    + "     customer"
                    + "  INNER JOIN account ON customer.administrator_id    = account.account_id"
                    + "  INNER JOIN role    ON account.role_id              = role.role_id"
                    + "  SET"
                    + "     customer.customer_name      = :customerName"
                    + ",    customer.phone_number       = :phoneNumber"
                    + ",    customer.address            = :address"
                    + ",    customer.version            = customer.version + 1"
                    + "  WHERE"
                    + "     customer.customer_id        = :customerId"
                    + " AND customer.version            = :version"
                    + " AND ("
                    + "     customer.administrator_id   = :accountId"
                    + " OR  role.role_name              = 'ROLE_ADMIN'"
                    + "     )",
            nativeQuery = true)
    int updateCustomer(
            @Param("customerId")    Integer customerId,
            @Param("accountId")     Integer accountId,
            @Param("customerName")  String  customerName,
            @Param("phoneNumber")   String  phoneNumber,
            @Param("address")       String  address,
            @Param("version")       Integer version
    );

    @Query(value =    " SELECT"
                    + "     c.customer_id"
                    + ",    c.customer_name"
                    + ",    c.phone_number"
                    + ",    c.address"
                    + ",    c.is_deleted"
                    + ",    c.version"
                    + ",    a.account_name"
                    + " FROM"
                    + "     customer c"
                    + " JOIN account a ON a.account_id = c.administrator_id"
                    + " WHERE"
                    + "     c.is_deleted         =  FALSE"
                    + " AND c.phone_number     = :phoneNumber"
            ,nativeQuery = true)
    Map<String,Object> findByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    @Query(value =    " SELECT"
                    + "     COUNT(*)"
                    + " FROM"
                    + "     customer"
                    + " WHERE"
                    + "     phone_number = :phoneNumber"
            ,nativeQuery = true)
    int existsByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    @Query(value =    " SELECT"
                    + "     COUNT(*)"
                    + " FROM"
                    + "     account"
                    + " WHERE"
                    + "     phone_number    = :phoneNumber"
                    + " AND customer_id     = :customerId"
            ,nativeQuery = true)
    int existsByPhoneNumberAndCustomerIdNot(
                                @Param("phoneNumber") String phoneNumber
                               ,@Param("customerId") Integer customerId);

    @Query(value =    " SELECT"
                    + "     c.customer_id"
                    + ",    c.customer_name"
                    + ",    c.phone_number"
                    + ",    c.address"
                    + ",    c.is_deleted"
                    + ",    c.version"
                    + ",    a.account_name"
                    + " FROM"
                    + "     customer c"
                    + " JOIN account a ON a.account_id = c.administrator_id"
                    + " WHERE"
                    + "     c.is_deleted         =  FALSE"
                    + " AND c.customer_name     = :customerName"
                    + " LIMIT 1"
            ,nativeQuery = true)
    Map<String,Object> findByCustomerName(@Param("customerName")String customerName);

}
