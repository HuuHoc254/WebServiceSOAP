package org.example.service.impl;

import org.example.dto.request.account.CreateAccountRequest;
import org.example.dto.request.account.UpdateAccountRequest;
import org.example.entity.AccountEntity;
import org.example.entity.RoleEntity;
import org.example.repository.AccountRepository;
import org.example.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {
    @Autowired
    AccountRepository accountRepository;
    @Override
    public int saveAccount(CreateAccountRequest request) {
        return accountRepository.createAccount(
                            request.getAccountName()
                            ,request.getFullName()
                            ,request.getPhoneNumber()
                            ,request.getPassword());
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
    public Optional<AccountEntity> findById(Integer accountId) {
        return accountRepository.findById(accountId);
    }

    @Override
    @Transactional
    public void saveAccount(UpdateAccountRequest request) {
        int rowUpdate = accountRepository
                .updateAccount(
                        request.getAccountId()
                        ,request.getAccountName()
                        ,request.getPassword()
                        ,request.getFullName()
                        ,request.getPhoneNumber()
                        ,request.getVersion()
                );

        if (rowUpdate==0) {
            throw new OptimisticLockingFailureException
                    ("Phiên bản không trùng khớp. Có thể đã có người cập nhật thông tin tài khoản.");
        }
    }

    @Override
    public void delete(AccountEntity account) {
        accountRepository.delete(account);
    }

    @Override
    public int totalRowSearch(String accountName, String phoneNumber, String fullName) {
        return accountRepository.countSearch(accountName,phoneNumber,fullName);
    }

    @Override
    public List<Map<String,Object>> search(String accountName, String phoneNumber, String fullName, int rowNumber, int pageSize) {
        return accountRepository
                .searchAccount(
                        accountName
                        ,phoneNumber
                        ,fullName
                        ,rowNumber
                        ,pageSize);
    }

    @Override
    public AccountEntity findByAccountName(String accountName) {
        return accountRepository.findByAccountName(accountName).orElse(null);
    }

    @Override
    public void createToken(AccountEntity accountEntity) {
        accountRepository.save(accountEntity);
    }

    @Override
    public Optional<AccountEntity> findByToken(String token) {
        return accountRepository.findByToken(token);
    }

    @Override
    public void logout(Integer accountId) {
        accountRepository.deleteToken(accountId);
    }
}
