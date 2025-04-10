package com.gdgswu.planeat.domain.auth.firebase;

import com.gdgswu.planeat.global.exception.CustomException;
import org.springframework.stereotype.Service;

import static com.gdgswu.planeat.global.exception.ErrorCode.ID_TOKEN_INVALID;

@Service
public class TestFirebaseTokenVerifier implements FirebaseTokenVerifier {
    @Override
    public String verifyIdTokenAndGetEmail(String IdToken) {
        if (IdToken.equals("valid_idToken")) {
            return "user@gmail.com";
        } else {
            throw new CustomException(ID_TOKEN_INVALID);
        }
    }
}
