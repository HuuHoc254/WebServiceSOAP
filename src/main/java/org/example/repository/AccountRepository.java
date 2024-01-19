package org.example.repository;

import org.example.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Integer> {
    boolean existsByPhoneNumberAndAccountIdNot(String phoneNumber,Integer accountId);
    boolean existsByAccountNameAndAccountIdNot(String accountName,Integer accountId);

    boolean existsByAccountName(String accountName);

    boolean existsByPhoneNumber(String phoneNumber);

    List<AccountEntity> findByAccountNameContainingAndPhoneNumberContainingAndFullNameContaining(String accountName, String phoneNumber, String fullName);
}

