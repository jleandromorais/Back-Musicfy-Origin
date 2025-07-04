package Musicfy.MusicfyOrigin.Product.Controller;

import Musicfy.MusicfyOrigin.Product.Service.UsuarioService;
import Musicfy.MusicfyOrigin.Product.dto.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuario")
public class UserController {

    private final UsuarioService userService;

    public UserController(UsuarioService userService) {
        this.userService = userService;
    }

    @PostMapping("/criar")
    public ResponseEntity<?> createUser(@RequestBody UserDTO userDTO) {
        System.out.println("Iniciando criação de usuário para: " + userDTO.getEmail());

        try {
            userService.criarUsuarioComCarrinho(userDTO);
            System.out.println("Usuário criado com sucesso!");
            return ResponseEntity.ok("Usuário e carrinho criados com sucesso!");
        } catch (Exception e) {
            System.err.println("Falha na criação: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Erro: " + e.getMessage());
        }
    }
}
