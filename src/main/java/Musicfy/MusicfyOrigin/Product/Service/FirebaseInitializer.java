import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

@Service
public class FirebaseInitializer {

    @PostConstruct
    public void initialize() {
        try {
            String firebaseConfig = System.getenv("FIREBASE_CONFIG");

            if (firebaseConfig == null || firebaseConfig.isEmpty()) {
                throw new IllegalStateException("Variável FIREBASE_CONFIG não encontrada ou está vazia");
            }

            ByteArrayInputStream serviceAccountStream =
                    new ByteArrayInputStream(firebaseConfig.getBytes(StandardCharsets.UTF_8));

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccountStream))
                    .build();

            FirebaseApp.initializeApp(options);
            System.out.println("✅ Firebase inicializado com sucesso!");

        } catch (Exception e) {
            System.err.println("❌ Erro ao inicializar Firebase: " + e.getMessage());
            throw new RuntimeException("Falha ao inicializar Firebase Admin SDK", e);
        }
    }
}
