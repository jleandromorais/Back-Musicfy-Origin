package Musicfy.MusicfyOrigin.Product.Service;

import Musicfy.MusicfyOrigin.Product.dto.UserDTO;
import Musicfy.MusicfyOrigin.Product.model.Cart;
import Musicfy.MusicfyOrigin.Product.model.Usuario;
import Musicfy.MusicfyOrigin.Product.repository.CartRepository;
import Musicfy.MusicfyOrigin.Product.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarioRepository;
    private final CartRepository cartRepository;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          CartRepository cartRepository) {
        this.usuarioRepository = usuarioRepository;
        this.cartRepository = cartRepository;
    }

    @Transactional
    public void criarUsuarioComCarrinho(UserDTO userDTO) {
        try {
            String uid = userDTO.getFirebaseUid();
            String email = userDTO.getEmail();

            System.out.println("Recebendo dados - UID: " + uid + ", Email: " + email);

            Optional<Usuario> usuarioExistente = Optional.ofNullable(usuarioRepository.findByFirebaseUid(uid));
            if (usuarioExistente.isPresent()) {
                System.out.println("Usuário já existe no banco: " + uid);
                throw new IllegalStateException("Usuário já cadastrado");
            }

            Usuario usuario = new Usuario();
            usuario.setFirebaseUid(uid);
            usuario.setName(userDTO.getFullName());
            usuario.setEmail(email);

            System.out.println("Salvando usuário no banco...");
            usuario = usuarioRepository.save(usuario);

            Cart cart = new Cart();
            cart.setUser(usuario);

            System.out.println("Salvando carrinho no banco...");
            cartRepository.save(cart);

            System.out.println("Usuário e carrinho criados com sucesso!");

        } catch (Exception e) {
            System.err.println("Erro ao criar usuário: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
