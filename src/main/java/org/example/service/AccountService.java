package org.example.service;

import org.example.dto.request.account.CreateAccountRequest;
import org.example.dto.request.account.UpdateAccountRequest;
import org.example.entity.AccountEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AccountService {
    int saveAccount(CreateAccountRequest createAccountRequest);

    Optional<AccountEntity> findById(Integer accountId);

    void saveAccount(UpdateAccountRequest request);

    void delete(AccountEntity account);

    int totalRowSearch(String accountName, String phoneNumber, String fullName);

    List<Map<String,Object>> search(String accountName
                              , String phoneNumber
                              , String fullName
                              , int rowNumber
                              , int pageSize);

    AccountEntity findByAccountName(String accountName);

    void createToken(AccountEntity accountEntity);

    Optional<AccountEntity> findByToken(String token);

    void logout(Integer accountId);
}
