package org.example.security;

import org.example.entity.AccountEntity;
import org.example.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailServiceImpl implements UserDetailsService {
    @Autowired
    private AccountRepository accountRepository;
    @Override
    @Transactional
        public UserDetails loadUserByUsername(String accountName) throws UsernameNotFoundException {
        AccountEntity account =  accountRepository.findByAccountName(accountName)
                .orElseThrow(() -> new UsernameNotFoundException("Account Not Found with accountName: " + accountName));
        return UserDetailImpl.convertAccountEntityToAccountDetail(account);
    }
}