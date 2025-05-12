package com.gdgswu.planeat.domain.auth.firebase;

import org.springframework.stereotype.Component;

@Component
public interface FirebaseTokenVerifier {
    String verifyIdTokenAndGetEmail(String IdToken);
}
