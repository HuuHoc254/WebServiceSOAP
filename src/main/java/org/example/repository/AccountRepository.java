package org.example.repository;

import org.example.entity.AccountEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Integer> {
    boolean existsByPhoneNumberAndAccountIdNot(String phoneNumber,Integer accountId);
   /* SELECT COUNT(*) > 0
    FROM your_entity_table
    WHERE account_name = :accountName
    AND account_id != :accountId;*/
    boolean existsByAccountNameAndAccountIdNot(String accountName,Integer accountId);

    boolean existsByAccountName(String accountName);

    boolean existsByPhoneNumber(String phoneNumber);

    @Modifying
    @Query(   "UPDATE AccountEntity e "
            + "SET e.accountName = :accountName"
            + ", e.password = :password"
            + ", e.fullName = :fullName"
            + ", e.phoneNumber = :phoneNumber"
            + ", e.version = e.version + 1"
            + " WHERE e.accountId = :accountId AND e.version = :version")
    int updateAccount(
            @Param("accountId") Integer accountId,
            @Param("accountName") String accountName,
            @Param("password") String password,
            @Param("fullName") String fullName,
            @Param("phoneNumber") String phoneNumber,
            @Param("version") Integer version
    );
    @Query(   "SELECT COUNT(e)"
            + " FROM AccountEntity e"
            + " WHERE e.accountName LIKE CONCAT(:accountName, '%')"
            + " AND e.phoneNumber LIKE CONCAT(:phoneNumber, '%')"
            + " AND e.fullName LIKE CONCAT(:fullName, '%')")
    int countSearch(
            @Param("accountName") String accountName,
            @Param("phoneNumber") String phoneNumber,
            @Param("fullName") String fullName
    );

    @Query(   "SELECT e FROM AccountEntity e"
            + " WHERE e.accountName LIKE CONCAT(:accountName, '%')"
            + " AND e.phoneNumber LIKE CONCAT(:phoneNumber, '%')"
            + " AND e.fullName LIKE CONCAT(:fullName, '%')"
            + " ORDER BY e.fullName"
            + " LIMIT :pageSize OFFSET :rowNumber")
    List<AccountEntity> searchAccount(
            @Param("accountName") String accountName,
            @Param("phoneNumber") String phoneNumber,
            @Param("fullName") String fullName,
            @Param("rowNumber") int rowNumber,
            @Param("pageSize") int pageSize
    );

   @Query(   "SELECT e FROM AccountEntity e"
           + " ORDER BY e.accountId"
           + " LIMIT :pageSize OFFSET :rowNumber")
   List<AccountEntity> findAll(
            @Param("rowNumber") int rowNumber
           ,@Param("pageSize") int pageSize );

    Optional<AccountEntity> findByAccountName(String username);
    Optional<AccountEntity> findByToken(String token);
}

