package Musicfy.MusicfyOrigin.Product.Controller;

import com.google.firebase.auth.FirebaseAuthException;
import Musicfy.MusicfyOrigin.Product.Service.UsuarioService;
import Musicfy.MusicfyOrigin.Product.dto.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuario")
@CrossOrigin(origins = "*")
public class UserController {

    private final UsuarioService userService;

    public UserController(UsuarioService userService) {
        this.userService = userService;
    }

    @PostMapping("/criar")
    public ResponseEntity<String> createUser(
            @RequestHeader("Authorization") String token,
            @RequestBody UserDTO userDTO
    ) {
        try {
            userService.criarUsuarioComCarrinho( // Verifique se esse método existe em UsuarioService
                    token.replace("Bearer ", ""),
                    userDTO
            );
            return ResponseEntity.ok("Usuário e carrinho criados com sucesso!");
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(401).body("Token inválido: " + e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro ao processar requisição");
        }
    }
}