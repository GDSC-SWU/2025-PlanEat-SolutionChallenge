package com.gdgswu.planeat.domain.auth;

import com.gdgswu.planeat.domain.auth.dto.response.GoogleUserInfo;
import com.gdgswu.planeat.global.exception.CustomException;
import com.gdgswu.planeat.global.exception.ErrorCode;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

import static com.gdgswu.planeat.global.exception.ErrorCode.*;

@Service
public class GoogleOAuth2Verifier {
    private final GoogleIdTokenVerifier verifier;

    public GoogleOAuth2Verifier(@Value("${google.client-id}") String clientId) {
        this.verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), new JacksonFactory())
                .setAudience(Collections.singletonList(clientId))
                .build();
    }


    public GoogleUserInfo verifyIdToken(String idToken) {
        try {
            GoogleIdToken token = verifier.verify(idToken);
            if (token == null) {
                throw new CustomException(ID_TOKEN_INVALID);
            }
            Payload payload = token.getPayload();
            return new GoogleUserInfo(payload.getEmail());
        } catch (GeneralSecurityException | IOException | IllegalArgumentException e) {
            throw new CustomException(GOOGLE_TOKEN_ERROR);
        }
    }
}
