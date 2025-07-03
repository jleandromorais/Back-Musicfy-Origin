package Musicfy.MusicfyOrigin.Product.Service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import Musicfy.MusicfyOrigin.Product.dto.UserDTO;
import Musicfy.MusicfyOrigin.Product.model.Cart;
import Musicfy.MusicfyOrigin.Product.model.Usuario;
import Musicfy.MusicfyOrigin.Product.repository.CartRepository;
import Musicfy.MusicfyOrigin.Product.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final CartRepository cartRepository;
    private final FirebaseAuth firebaseAuth;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          CartRepository cartRepository,
                          FirebaseAuth firebaseAuth) {
        this.usuarioRepository = usuarioRepository;
        this.cartRepository = cartRepository;
        this.firebaseAuth = firebaseAuth;
    }

    @Transactional
    public void criarUsuarioComCarrinho(String token, UserDTO userDTO) throws FirebaseAuthException {
        // Verifica o token via Firebase
        FirebaseToken decodedToken = firebaseAuth.verifyIdToken(token);
        String uid = decodedToken.getUid();

        // Validação de segurança: o UID do token precisa bater com o do DTO
        if (!userDTO.getFirebaseUid().equals(uid)) {
            throw new SecurityException("UID do token não corresponde ao UID do usuário");
        }

        // Cria e popula o usuário
        Usuario usuario = new Usuario();
        usuario.setId(userDTO.getId());
        usuario.setName(userDTO.getFullName());
        usuario.setEmail(userDTO.getEmail());
        // TODO: setar outros campos do Usuario conforme necessário

        usuarioRepository.save(usuario);

        // Cria e salva o carrinho vinculado ao usuário
        Cart cart = new Cart();
        cart.setUser(usuario);
        cartRepository.save(cart);
    }
}
