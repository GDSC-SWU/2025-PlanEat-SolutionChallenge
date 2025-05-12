package com.gdgswu.planeat.domain.auth;

import com.gdgswu.planeat.domain.user.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    // 비밀번호 로그인 없음
    @Override
    public String getPassword() {
        return null;
    }

    // username 대신 email 사용
    @Override
    public String getUsername() {
        return user.getEmail();
    }
}
