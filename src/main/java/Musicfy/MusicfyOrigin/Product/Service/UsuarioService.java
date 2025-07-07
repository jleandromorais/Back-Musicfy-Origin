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

    public UsuarioService(UsuarioRepository usuarioRepository, CartRepository cartRepository) {
        this.usuarioRepository = usuarioRepository;
        this.cartRepository = cartRepository;
    }

    @Transactional
    public Usuario criarUsuarioComCarrinho(UserDTO userDTO) {
        String uid = userDTO.getFirebaseUid();

        Optional<Usuario> usuarioExistente = Optional.ofNullable(usuarioRepository.findByFirebaseUid(uid));
        if (usuarioExistente.isPresent()) {
            logger.warn("Tentativa de criar usuário já existente com Firebase UID: {}", uid);
            throw new IllegalStateException("Usuário já cadastrado");
        }

        Usuario usuario = new Usuario();
        usuario.setFirebaseUid(uid);
        usuario.setName(userDTO.getFullName());
        usuario.setEmail(userDTO.getEmail());

        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        logger.info("Usuário salvo com ID: {}", usuarioSalvo.getId());

        Cart cart = new Cart();
        cart.setUser(usuarioSalvo);
        cartRepository.save(cart);

        return usuarioSalvo;
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarUsuarioPorFirebaseUid(String firebaseUid) {
        logger.info("Buscando usuário com Firebase UID: {}", firebaseUid);
        return Optional.ofNullable(usuarioRepository.findByFirebaseUid(firebaseUid));
    }
}
