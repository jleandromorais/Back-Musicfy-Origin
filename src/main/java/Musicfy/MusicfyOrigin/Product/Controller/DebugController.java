package Musicfy.MusicfyOrigin.Product.Controller;

import com.google.firebase.auth.FirebaseAuth;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/debug")
public class DebugController {

    @GetMapping("/generate-test-token")
    public ResponseEntity<Map<String, String>> generateTestToken() {
        try {
            String uid = "test-uid-" + System.currentTimeMillis();
            String token = FirebaseAuth.getInstance().createCustomToken(uid);

            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("uid", uid);
            response.put("email", "test-" + uid.substring(0, 8) + "@test.com");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
