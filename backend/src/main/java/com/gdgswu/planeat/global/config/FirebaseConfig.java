package com.gdgswu.planeat.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initialize() {
        try {
            String firebaseKey = System.getenv("FIREBASE_CONFIG");
            if (firebaseKey == null) {
                throw new RuntimeException("FIREBASE_CONFIG 환경 변수가 설정되지 않았습니다.");
            }

            byte[] decodedKey = Base64.getDecoder().decode(firebaseKey);
            ByteArrayInputStream serviceAccount = new ByteArrayInputStream(decodedKey);

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        } catch (IOException e) {
            throw new RuntimeException("Firebase 초기화 실패", e);
        }
    }
}