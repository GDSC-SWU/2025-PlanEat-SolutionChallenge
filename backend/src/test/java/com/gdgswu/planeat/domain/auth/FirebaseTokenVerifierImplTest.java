package com.gdgswu.planeat.domain.auth;

import com.gdgswu.planeat.domain.auth.firebase.FirebaseTokenVerifierImpl;
import com.gdgswu.planeat.global.exception.CustomException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.gdgswu.planeat.global.exception.ErrorCode.ID_TOKEN_INVALID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class FirebaseTokenVerifierImplTest {

    @Mock
    private FirebaseAuth firebaseAuth;

    @Mock
    private FirebaseToken firebaseToken;

    @InjectMocks
    private FirebaseTokenVerifierImpl firebaseTokenVerifierImpl;

    @Test
    @DisplayName("유효한 idToken이면 유저 email 반환")
    void verifyIdTokenAndGetEmail_success() throws Exception {
        // given
        String idToken = "valid_token";
        given(firebaseAuth.verifyIdToken(idToken)).willReturn(firebaseToken);
        given(firebaseToken.getEmail()).willReturn("user@email.com");

        // when
        String email = firebaseTokenVerifierImpl.verifyIdTokenAndGetEmail(idToken);

        // then
        assertThat(email).isEqualTo(email);
    }

    @Test
    @DisplayName("유효하지 않은 idToken이면 ID_TOKEN_INVALID 예외 발생")
    void verifyIdToken_AndGetEmail_fail() throws FirebaseAuthException {
        // given
        String idToken = "invalid_token";
        given(firebaseAuth.verifyIdToken(idToken))
                .willThrow(mock(FirebaseAuthException.class));

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> firebaseTokenVerifierImpl.verifyIdTokenAndGetEmail(idToken));
        assertThat(exception.getErrorCode()).isEqualTo(ID_TOKEN_INVALID);

    }
}