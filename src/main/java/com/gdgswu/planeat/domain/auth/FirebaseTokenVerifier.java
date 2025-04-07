package com.gdgswu.planeat.domain.auth;

import com.gdgswu.planeat.global.exception.CustomException;
import com.gdgswu.planeat.global.exception.ErrorCode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.stereotype.Service;



@Service
public class FirebaseTokenVerifier {

    public FirebaseToken verifyIdToken(String idToken) {
        try {
            return FirebaseAuth.getInstance().verifyIdToken(idToken);
        } catch (FirebaseAuthException e) {
            throw new CustomException(ErrorCode.ID_TOKEN_INVALID);
        }
    }
}