package org.example.service.impl;

import org.example.dto.request.SearchAccountRequest;
import org.example.dto.request.UpdateAccountRequest;
import org.example.entity.AccountEntity;
import org.example.entity.RoleEntity;
import org.example.repository.AccountRepository;
import org.example.service.AccountService;
import org.example.dto.request.CreateAccountRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    AccountRepository accountRepository;
    @Override
    public AccountEntity saveAccount(CreateAccountRequest createAccountRequest) {
        AccountEntity account = convertRequestToEntity(createAccountRequest);
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setRoleId(2);
        roleEntity.setRoleName("STAFF");
        roleEntity.setIsDeleted(false);
        account.setRole(roleEntity);
        accountRepository.save(account);
        return account;
    }

    private AccountEntity convertRequestToEntity(CreateAccountRequest createAccountRequest) {
        AccountEntity account = new AccountEntity();
        account.setAccountName(createAccountRequest.getAccountName());
        account.setPassword(createAccountRequest.getPassword());
        account.setFullName(createAccountRequest.getFullName());
        account.setPhoneNumber(createAccountRequest.getPhoneNumber());
        account.setIsOnline(false);
        account.setVersion(0);
        account.setIsDeleted(false);
        return account;
    }

    @Override
    public Page<AccountEntity> findAllAccount(PageRequest pageRequest) {
        return accountRepository.findAll(pageRequest);
    }

    @Override
    public AccountEntity updateAccount(UpdateAccountRequest updateAccountRequest) {
        AccountEntity account = accountRepository.findById(updateAccountRequest.getAccountId()).orElse(null);

        return null;
    }

    @Override
    public Optional<AccountEntity> findById(Integer accountId) {
        return accountRepository.findById(accountId);
    }

    @Override
    public void save(AccountEntity existingAccount) {
        // Kiểm tra xem version trên existingAccount có trùng với version trong cơ sở dữ liệu không
        AccountEntity databaseAccount = accountRepository.findById(existingAccount.getAccountId()).orElse(null);

        if (databaseAccount == null) {
            throw new RuntimeException("Không tìm thấy tài khoản trong cơ sở dữ liệu");
        }

        if (existingAccount.getVersion() != databaseAccount.getVersion()) {
            throw new OptimisticLockingFailureException
                    ("Phiên bản không trùng khớp. Có thể đã có người cập nhật thông tin tài khoản.");
        }
        existingAccount.setVersion(existingAccount.getVersion()+1);
        accountRepository.save(existingAccount);
    }

    @Override
    public void delete(AccountEntity account) {
        accountRepository.delete(account);
    }

    @Override
    public List<AccountEntity> searchAccount(SearchAccountRequest searchAccountRequest) {
        return accountRepository
                .findByAccountNameContainingAndPhoneNumberContainingAndFullNameContaining(
                    searchAccountRequest.getAccountName(),
                    searchAccountRequest.getPhoneNumber(),
                    searchAccountRequest.getFullName()
                );
    }
}
