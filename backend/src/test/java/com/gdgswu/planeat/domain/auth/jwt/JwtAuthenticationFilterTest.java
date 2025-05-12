package com.gdgswu.planeat.domain.auth.jwt;

import com.gdgswu.planeat.domain.auth.CustomUserDetails;
import com.gdgswu.planeat.domain.user.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();;
    }

    @Test
    @DisplayName("유효한 토큰이 있을 경우 SecurityContext에 인증 정보 저장")
    void doFilterInternal_validation_setsAuthentication() throws Exception {
        // given
        String token = "valid.jwt.token";
        String userEmail = "test@example.com";

        given(request.getHeader("Authorization")).willReturn("Bearer " + token);
        given(jwtProvider.validateToken(token)).willReturn(true);
        given(jwtProvider.getEmail(token)).willReturn(userEmail);
        given(userDetailsService.loadUserByUsername(userEmail))
                .willReturn(new CustomUserDetails(User.builder()
                        .email(userEmail).build()));

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isInstanceOf(UsernamePasswordAuthenticationToken.class);
        assertThat(auth.getName()).isEqualTo(userEmail);
        assertThat(auth.isAuthenticated()).isTrue();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Authorization 헤더가 없을 경우 인증x")
    void doFilterInternal_noHeader_continueFilterChain() throws Exception {
        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("유효하지 않은 토큰일 경우 인증x")
    void doFilterInternal_invalidToken_continueFilterChain() throws Exception {
        // given
        String invalidToken = "invalid.jwt.token";
        given(request.getHeader("Authorization")).willReturn("Bearer " + invalidToken);
        given(jwtProvider.validateToken(invalidToken)).willReturn(false);

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }
}