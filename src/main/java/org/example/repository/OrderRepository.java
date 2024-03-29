package org.example.repository;

import org.example.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Integer> {
    final String SEARCH_SQL =
                      " FROM orders o"
                    + " JOIN product    p           ON p.product_id     = o.product_id"
                    + " JOIN account    a           ON a.account_id     = o.account_id"
                    + " JOIN customer   c           ON c.customer_id    = o.customer_id"
                    + " JOIN order_status ost ON ost.order_status_id    = o.order_status_id"
                    + " WHERE"
                    +     " p.product_code  LIKE CONCAT(:productCode,'%')"
                    + " AND p.product_name  LIKE CONCAT(:productName,'%')"
                    + " AND a.account_name  LIKE CONCAT(:accountName,'%')"
                    + " AND a.full_name     LIKE CONCAT(:staffFullName,'%')"
                    + " AND c.customer_name LIKE CONCAT(:customerName,'%')"
                    + " AND c.phone_number  LIKE CONCAT(:customerPhoneNumber,'%')"
                    + " AND o.order_date    BETWEEN :beginOrderDate AND :endOrderDate"
                    + " AND o.order_status_id = :orderStatusId"
                    + " AND ("
                    +       ":isAdmin = true"
                    + "     OR (a.account_id = :accountId AND o.is_deleted = FALSE)"
                    +     " )";

    final String SQL_CUSTOMER_ZERO_ORDER =
                      " FROM"
                    + "       orders o"
                    + " RIGHT JOIN"
                    + "       customer c ON c.customer_id = o.customer_id"
                    + " WHERE"
                    + "       c.is_deleted = FALSE"
                    + " AND  ("
                    + "       o.order_id      IS NULL"
                    + " OR    o.customer_id   NOT IN ("
                    + "               SELECT"
                    + "                   customer_id"
                    + "               FROM"
                    + "                   orders"
                    + "               WHERE"
                    + "                   order_date"
                    + "               BETWEEN :startDate AND DATE_ADD(:endDate, INTERVAL 1 DAY)"
                    + "                          )"
                    + "       )";
    final String SQL_PRODUCT_BEST_SELLER =
                      " FROM"
                    + "       orders o"
                    + " JOIN"
                    + "        product p ON p.product_id = o.product_id"
                    + " WHERE"
                    + "       o.order_date BETWEEN :startDate AND DATE_ADD(:endDate, INTERVAL 1 DAY)"
                    + " AND  p.is_deleted = FALSE "
                    + " GROUP BY"
                    + "     o.product_id";

    final String SQL_PRODUCT_ZERO_ORDER =
                      " FROM"
                    + "       orders o"
                    + " RIGHT JOIN"
                    + "       product p ON p.product_id = o.product_id"
                    + " WHERE"
                    + "       p.is_deleted = FALSE"
                    + " AND  ("
                    + "       o.order_id        IS NULL"
                    + " OR    o.product_id      NOT IN ("
                    + "               SELECT"
                    + "                   product_id"
                    + "               FROM"
                    + "                   orders"
                    + "               WHERE"
                    + "                   order_date"
                    + "               BETWEEN :startDate AND DATE_ADD(:endDate, INTERVAL 1 DAY)"
                    + "                          )"
                    + "       )";
    @Query(   value = "SELECT COUNT(*)"
                    + SEARCH_SQL
            , nativeQuery = true
    )
    int totalRecordSearch(
                     @Param("accountId")            Integer         accountId
                    ,@Param("accountName")          String          accountName
                    ,@Param("staffFullName")        String          staffFullName
                    ,@Param("productCode")          String          productCode
                    ,@Param("productName")          String          productName
                    ,@Param("customerName")         String          customerName
                    ,@Param("customerPhoneNumber")  String          customerPhoneNumber
                    ,@Param("beginOrderDate")       LocalDateTime   beginOrderDate
                    ,@Param("endOrderDate")         LocalDateTime   endOrderDate
                    ,@Param("orderStatusId")        Integer         orderStatusId
                    ,@Param("isAdmin")              boolean         isAdmin
                    );

    @Query(value =    " SELECT"
                    + "     o.order_date            AS orderDate,"
                    + "     p.product_code          AS productCode,"
                    + "     p.product_name          AS productName,"
                    + "     o.unit_price            AS unitPrice,"
                    + "     o.quantity              AS quantity,"
                    + "     c.customer_name         AS customerName,"
                    + "     o.phone_number_customer AS phoneNumber,"
                    + "     o.address_customer      AS address,"
                    + "     ost.order_status_name   AS orderStatusName,"
                    + "     o.allocation_date       AS allocationDate,"
                    + "     a.account_name          AS accountName,"
                    + "     a.full_name             AS staffName,"
                    + "     o.version               AS version,"
                    + "     o.is_deleted            AS isDeleted"
                    + SEARCH_SQL +
                    " ORDER BY"
                        + " CASE WHEN o.is_deleted = TRUE THEN 1 ELSE 0 END"
                        + ", o.order_date DESC" +
                    " LIMIT :pageSize OFFSET :recordNumber"
            ,nativeQuery = true
    )
    List<Map<String, Object>> search(
                     @Param("accountId")                Integer         accountId
                    ,@Param("accountName")              String          accountName
                    ,@Param("staffFullName")            String          staffFullName
                    ,@Param("productCode")              String          productCode
                    ,@Param("productName")              String          productName
                    ,@Param("customerName")             String          customerName
                    ,@Param("customerPhoneNumber")      String          customerPhoneNumber
                    ,@Param("beginOrderDate")           LocalDateTime   beginOrderDate
                    ,@Param("endOrderDate")             LocalDateTime   endOrderDate
                    ,@Param("orderStatusId")            Integer         orderStatusId
                    ,@Param("recordNumber")             Integer         recordNumber
                    ,@Param("pageSize")                 Integer         pageSize
                    ,@Param("isAdmin")                  boolean         isAdmin
            );

    @Modifying
    @Query(value =    " UPDATE"
                    + "     orders "
                    + " SET orders.is_deleted   = true"
                    + " WHERE"
                    + "     orders.order_id     = :orderId"
                    + " AND is_delete           = FALSE"
                    + " AND order_status_id     = 1"
            ,nativeQuery = true)
    int deleteOrder(@Param("orderId") Integer orderId);

    @Query( value = " SELECT COUNT(*)"
                  + SQL_CUSTOMER_ZERO_ORDER
            ,nativeQuery = true)
    int totalFindCustomerZeroOrder(
                    @Param("startDate")        String   startDate
                  , @Param("endDate")          String   endDate
                                );
    @Query( value = " SELECT DISTINCT"
                  + "       c.customer_id"
                  + ",      c.customer_name"
                  + ",      c.phone_number"
                  + ",      c.address"
                  + SQL_CUSTOMER_ZERO_ORDER
                  + " ORDER BY"
                  + "       SUBSTRING_INDEX(c.customer_name, ' ', -1)"
                  + " LIMIT :pageSize OFFSET :recordNumber"
            ,nativeQuery = true)
    List<Map<String,Object>> findCustomerZeroOrder(
                     @Param("startDate")        String  startDate
                   , @Param("endDate")          String  endDate
                   , @Param("recordNumber")     int     recordNumber
                   , @Param("pageSize")         int     pageSize
                   );

    @Query( value = " SELECT"
                  + "       p.product_id"
                  + ",      p.product_code"
                  + ",      p.product_name"
                  + ",      SUM(o.quantity) as quantity"
                  + SQL_PRODUCT_BEST_SELLER
                  + " ORDER BY"
                  + "       quantity DESC"
                  + ",      p.product_id"
                  + " LIMIT :pageSize OFFSET :recordNumber"
            ,nativeQuery = true)
    List<Map<String,Object>> findProductsBestSeller(
                      @Param("startDate")        String startDate
                    , @Param("endDate")          String endDate
                    , @Param("recordNumber")     int    recordNumber
                    , @Param("pageSize")         int    pageSize
            );

    @Query( value = " SELECT DISTINCT"
                    + "       p.product_id"
                    + ",      p.product_code"
                    + ",      p.product_name"
                    + SQL_PRODUCT_ZERO_ORDER
                    + " ORDER BY"
                    + "       p.product_code"
                    + " LIMIT :pageSize OFFSET :recordNumber"
            ,nativeQuery = true)
    List<Map<String,Object>> findProductZeroOrders(
                      @Param("startDate")        String startDate
                    , @Param("endDate")          String endDate
                    , @Param("recordNumber")     int    recordNumber
                    , @Param("pageSize")         int    pageSize);

    @Query( value =
                    " SELECT COUNT(*)"
                    + " FROM ("
                    + " SELECT COUNT(*)"
                    + SQL_PRODUCT_BEST_SELLER
                    + "       ) AS sub_query"
            ,nativeQuery = true)
    int totalFindProductBestSeller(
                      @Param("startDate")        String   startDate
                    , @Param("endDate")          String   endDate
    );

    @Query( value = " SELECT COUNT(*)"
                    + SQL_PRODUCT_ZERO_ORDER
            ,nativeQuery = true)
    int totalFindProductZeroOrder(
                      @Param("startDate")        String   startDate
                    , @Param("endDate")          String   endDate
    );
}
