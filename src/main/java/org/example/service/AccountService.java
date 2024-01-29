package org.example.service;

import org.example.dto.request.account.CreateAccountRequest;
import org.example.dto.request.account.SearchAccountRequest;
import org.example.dto.request.account.UpdateAccountRequest;
import org.example.entity.AccountEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

public interface AccountService {
    AccountEntity saveAccount(CreateAccountRequest createAccountRequest);

    long totalRowFindAll();

    List<AccountEntity> findAll(int rowNumber, int pageSize);

    Optional<AccountEntity> findById(Integer accountId);

    void saveAccount(UpdateAccountRequest request);

    void delete(AccountEntity account);

    int totalRowSearch(String accountName, String phoneNumber, String fullName);

    List<AccountEntity> search( String accountName
                              , String phoneNumber
                              , String fullName
                              , int rowNumber
                              , int pageSize);

    Optional<AccountEntity> findByAccountName(String accountName);

    void createToken(AccountEntity accountEntity);

    Optional<AccountEntity> findByToken(String token);
}
