package org.example.service;

import org.example.dto.request.SearchAccountRequest;
import org.example.dto.request.UpdateAccountRequest;
import org.example.entity.AccountEntity;
import org.example.dto.request.CreateAccountRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

public interface AccountService {
    AccountEntity saveAccount(CreateAccountRequest createAccountRequest);

    Page<AccountEntity> findAllAccount(PageRequest pageRequest);

    AccountEntity updateAccount(UpdateAccountRequest updateAccountRequest);

    Optional<AccountEntity> findById(Integer accountId);

    void save(AccountEntity existingAccount);

    void delete(AccountEntity account);

    List<AccountEntity> searchAccount(SearchAccountRequest searchAccountRequest);
}
