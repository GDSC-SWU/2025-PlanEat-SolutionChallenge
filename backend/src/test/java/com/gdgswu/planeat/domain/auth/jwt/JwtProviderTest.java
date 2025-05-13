package com.gdgswu.planeat.domain.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
class JwtProviderTest {

    @Autowired
    private JwtProvider jwtProvider;
    private static String secret = "test_secret_xxxxxxxxxxxxxxxxxxxxxxxxxxx";

    @Test
    @DisplayName("email로 jwt 토큰 생성")
    void createTokenWithEmail() {
        // given
        String userEmail = "example@gmail.com";

        // when
        String token = jwtProvider.createToken(userEmail);

        // then
        Claims jwtParser = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
        assertThat(jwtParser.getSubject()).isEqualTo(userEmail);
        assertThat(jwtProvider.validateToken(token)).isTrue();
        assertThat(jwtProvider.getEmail(token)).isEqualTo(userEmail);
    }

    @Test
    @DisplayName("토큰 유효성 검사 성공")
    void validateToken_success() {
        // given
        String token = jwtProvider.createToken("example@gmail.com");

        // when & then
        assertThat(jwtProvider.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("유효하지 않은 토큰은 유효성 검사 실패")
    void validateToken_fail() {
        // given
        String invalidToken = "invalid.token.value";

        // when & then
        assertThat(jwtProvider.validateToken(invalidToken)).isFalse();
    }

    @Test
    @DisplayName("토큰에서 유저 이메일 추출 성공")
    void getEmail_success() {
        // given
        String userEmail = "example@gmail.com";
        String token = jwtProvider.createToken(userEmail);

        // when & then
        assertThat(jwtProvider.getEmail(token)).isEqualTo(userEmail);
    }

    @TestConfiguration
    static class TestJwtConfig {
        @Bean
        public JwtProvider jwtProvider() {
            return new JwtProvider(
                    new JwtProperties(
                            secret,
                            864000
                    ));
        }
    }

}