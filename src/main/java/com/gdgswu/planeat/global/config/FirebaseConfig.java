package com.gdgswu.planeat.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initialize() {
        try {
            String firebaseKey = System.getenv("FIREBASE_CONFIG");
            if (firebaseKey == null) {
                throw new RuntimeException("FIREBASE_CONFIG 환경 변수가 설정되지 않았습니다.");
            }

            File tempFile = File.createTempFile("firebase", ".json");
            try (FileWriter writer = new FileWriter(tempFile)) {
                writer.write(firebaseKey);
            }

            FileInputStream serviceAccount = new FileInputStream(tempFile);

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
        } catch (Exception e) {
            throw new RuntimeException("Firebase 초기화 실패", e);
        }
    }
}
