package org.example.repository;

import org.example.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Integer> {
    final String SQL_SEARCH =   " FROM"
                              + "      account"
                              + " WHERE"
                              +     "  account_name    LIKE CONCAT(:accountName, '%')"
                              + " AND  phone_number    LIKE CONCAT(:phoneNumber, '%')"
                              + " AND  full_name       LIKE CONCAT(:fullName, '%')" ;
    @Query(value =    " SELECT"
                    + "     COUNT(*)"
                    + " FROM"
                    + "     account"
                    + " WHERE"
                    + "     phone_number = :phoneNumber"
                    + " AND account_id  <> :accountId"
            ,nativeQuery = true)
    int existsByPhoneNumberAndAccountIdNot(@Param("phoneNumber")    String  phoneNumber
                                          ,@Param("accountId")      Integer accountId);
    @Query(value =    " SELECT"
                    + "     COUNT(*)"
                    + " FROM"
                    + "     account"
                    + " WHERE"
                    + "     account_name = :accountName"
                    + " AND account_id  <> :accountId"
            ,nativeQuery = true)
    int existsByAccountNameAndAccountIdNot(@Param("accountName")    String  accountName
                                          ,@Param("accountId")      Integer accountId);

    @Query(value =    " SELECT"
                    + "     COUNT(*)"
                    + " FROM"
                    + "     account"
                    + " WHERE"
                    + "     account_name = :accountName"
            ,nativeQuery = true)
    int existsByAccountName(@Param("accountName") String accountName);

    @Query(value =    " SELECT"
            + "     COUNT(*)"
            + " FROM"
            + "     account"
            + " WHERE"
            + "     phone_number = :phoneNumber"
            ,nativeQuery = true)
    int existsByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    @Modifying
    @Query(   value = " UPDATE"
                    + "     account"
                    + " SET     "
                    + "     account_name    = :accountName"
                    + ",    password        = :password"
                    + ",    fullName        = :fullName"
                    + ",    phoneNumber     = :phoneNumber"
                    + ",    version         = version + 1"
                    + " WHERE"
                    + "     account_id      = :accountId"
                    + " AND version         = :version"
            ,nativeQuery = true)
    int updateAccount(
            @Param("accountId")     Integer accountId,
            @Param("accountName")   String  accountName,
            @Param("password")      String  password,
            @Param("fullName")      String  fullName,
            @Param("phoneNumber")   String  phoneNumber,
            @Param("version")       Integer version
    );
    @Query( value =   " SELECT"
                    + "     COUNT(*)"
                    + SQL_SEARCH
            ,nativeQuery = true)
    int countSearch(
            @Param("accountName")   String accountName,
            @Param("phoneNumber")   String phoneNumber,
            @Param("fullName")      String fullName
    );

    @Query( value =   " SELECT"
                    +     "  account_id"
                    +     ", account_name"
                    +     ", full_name"
                    +     ", phone_number"
                    +     ", is_online"
                    +     ", version"
                    +     ", is_deleted"
                    + SQL_SEARCH
                    + " ORDER BY"
                    +     "  CASE"
                    +     "  WHEN is_deleted = TRUE"
                    +     "  THEN 1 ELSE 0 END"
                    + ",SUBSTRING_INDEX(full_name, ' ', -1)"
                    + " LIMIT :pageSize OFFSET :rowNumber"
            ,nativeQuery = true)
    List<Map<String,Object>> searchAccount(
            @Param("accountName")   String  accountName,
            @Param("phoneNumber")   String  phoneNumber,
            @Param("fullName")      String  fullName,
            @Param("rowNumber")     int     rowNumber,
            @Param("pageSize")      int     pageSize
    );
    Optional<AccountEntity> findByAccountName(String username);
    Optional<AccountEntity> findByToken(String token);

    @Modifying
    @Query(value =    " INSERT INTO"
                +           " account"
                +               "("
                +                   "  account_name"
                +                   ", full_name"
                +                   ", phone_number"
                +                   ", password"
                +               ")"
                +     " VALUE"
                +           "("
                +               "  :accountName"
                +               ", :fullName"
                +               ", :phoneNumber"
                +               ", :password"
                +           ")"
            ,nativeQuery = true)
    int createAccount(   @Param("accountName")  String accountName
                        ,@Param("fullName")     String fullName
                        ,@Param("phoneNumber")  String phoneNumber
                        ,@Param("password")     String password);

}

