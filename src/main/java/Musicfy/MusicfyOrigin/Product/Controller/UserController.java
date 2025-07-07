package Musicfy.MusicfyOrigin.Product.Controller;

import Musicfy.MusicfyOrigin.Product.Service.UsuarioService;
import Musicfy.MusicfyOrigin.Product.dto.UserDTO;
import Musicfy.MusicfyOrigin.Product.model.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuario")
public class UserController {

    private final UsuarioService userService;

    public UserController(UsuarioService userService) {
        this.userService = userService;
    }

    @PostMapping("/criar")
    public ResponseEntity<?> createUser(@RequestBody UserDTO userDTO) {
        try {
            Usuario novoUsuario = userService.criarUsuarioComCarrinho(userDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(novoUsuario));
        } catch (IllegalStateException e) {
            Optional<Usuario> existente = userService.buscarUsuarioPorFirebaseUid(userDTO.getFirebaseUid());
            if (existente.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(convertToDTO(existente.get()));
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("message", "Usuário já existe, mas não foi possível recuperá-lo."));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "Erro interno: " + e.getMessage()));
        }
    }

    @GetMapping("/firebase/{firebaseUid}")
    public ResponseEntity<UserDTO> getUserByFirebaseUid(@PathVariable String firebaseUid) {
        Optional<Usuario> usuario = userService.buscarUsuarioPorFirebaseUid(firebaseUid);
        return usuario.map(value -> ResponseEntity.ok(convertToDTO(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private UserDTO convertToDTO(Usuario usuario) {
        UserDTO dto = new UserDTO();
        dto.setId(usuario.getId());
        dto.setFirebaseUid(usuario.getFirebaseUid());
        dto.setFullName(usuario.getName());
        dto.setEmail(usuario.getEmail());
        return dto;
    }
}
