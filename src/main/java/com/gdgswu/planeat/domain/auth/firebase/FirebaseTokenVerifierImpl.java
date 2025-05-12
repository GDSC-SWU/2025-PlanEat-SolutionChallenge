package com.gdgswu.planeat.domain.auth.firebase;

import com.gdgswu.planeat.global.exception.CustomException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import static com.gdgswu.planeat.global.exception.ErrorCode.ID_TOKEN_INVALID;


@Service
@DependsOn("firebaseConfig")
public class FirebaseTokenVerifierImpl implements FirebaseTokenVerifier {

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    public String verifyIdTokenAndGetEmail(String idToken) {
        try {
            return firebaseAuth.verifyIdToken(idToken).getEmail();
        } catch (FirebaseAuthException e) {
            throw new CustomException(ID_TOKEN_INVALID);
        }
    }
}