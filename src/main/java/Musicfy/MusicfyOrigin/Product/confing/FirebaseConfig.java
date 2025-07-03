package Musicfy.MusicfyOrigin.Product.confing;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initialize() {
        try (InputStream serviceAccount = new ClassPathResource("serviceAccountKey.json").getInputStream()) {

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    //.setDatabaseUrl("https://your-project-id.firebaseio.com") // Se usar Realtime Database
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("Firebase inicializado com sucesso!");
            }

        } catch (IOException e) {
            System.err.println("Erro ao inicializar Firebase: " + e.getMessage());
            throw new RuntimeException("Erro ao inicializar Firebase", e);
        }
    }

    // Define o bean para permitir a injeção de FirebaseAuth
    @Bean
    public FirebaseAuth firebaseAuth() {
        return FirebaseAuth.getInstance();
    }
}

