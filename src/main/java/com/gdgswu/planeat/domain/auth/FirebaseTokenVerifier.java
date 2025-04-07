package com.gdgswu.planeat.domain.auth;

import com.gdgswu.planeat.global.exception.CustomException;
import com.gdgswu.planeat.global.exception.ErrorCode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.stereotype.Service;

import static com.gdgswu.planeat.global.exception.ErrorCode.ID_TOKEN_INVALID;


@Service
public class FirebaseTokenVerifier {

    private final FirebaseAuth firebaseAuth;

    public FirebaseTokenVerifier() {
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    public FirebaseTokenVerifier(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    public FirebaseToken verifyIdToken(String idToken) {
        try {
            return firebaseAuth.verifyIdToken(idToken);
        } catch (FirebaseAuthException e) {
            throw new CustomException(ID_TOKEN_INVALID);
        }
    }
}