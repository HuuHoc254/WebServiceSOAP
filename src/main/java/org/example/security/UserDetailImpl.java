package org.example.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.entity.AccountEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailImpl implements UserDetails {
    private Integer accountId;
    private String accountName;
    private String password;
    private boolean isAccountNonLocked;
    private List<? extends GrantedAuthority> authorities;

    public static UserDetailImpl convertAccountEntityToAccountDetail(AccountEntity account){

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(account.getRole().getRoleName()));
        return new UserDetailImpl(
                account.getAccountId(),
                account.getAccountName(),
                account.getPassword(),
                !account.getIsDeleted(),
                authorities
        );
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return accountName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
