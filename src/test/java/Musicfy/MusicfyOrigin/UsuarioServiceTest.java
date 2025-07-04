package Musicfy.MusicfyOrigin.Product.Service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import Musicfy.MusicfyOrigin.Product.dto.UserDTO;
import Musicfy.MusicfyOrigin.Product.model.Cart;
import Musicfy.MusicfyOrigin.Product.model.Usuario;
import Musicfy.MusicfyOrigin.Product.repository.CartRepository;
import Musicfy.MusicfyOrigin.Product.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private FirebaseAuth firebaseAuth;

    @InjectMocks
    private UsuarioService usuarioService;

    private final String testToken = "test-token";
    private final String testUid = "test-uid-123";
    private final String testEmail = "test@example.com";
    private UserDTO testUserDTO;
    private FirebaseToken testDecodedToken;

    @BeforeEach
    void setUp() throws FirebaseAuthException {
        // Configuração do DTO de teste
        testUserDTO = new UserDTO();
        testUserDTO.setFirebaseUid(testUid);
        testUserDTO.setFullName("Test User");
        testUserDTO.setEmail(testEmail);

        // Configuração do token decodificado mock
        testDecodedToken = mock(FirebaseToken.class);
        when(testDecodedToken.getUid()).thenReturn(testUid);
        when(testDecodedToken.getEmail()).thenReturn(testEmail);

        // Configuração padrão do FirebaseAuth mock
        when(firebaseAuth.verifyIdToken(testToken)).thenReturn(testDecodedToken);
    }

    @Test
    void criarUsuarioComCarrinho_deveSalvarUsuarioECarrinho_quandoDadosValidos() throws FirebaseAuthException {
        // Arrange
        when(usuarioRepository.findByFirebaseUid(testUid)).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuario = invocation.getArgument(0);
            usuario.setId(1L); // Simula a geração de ID
            return usuario;
        });

        // Act
        usuarioService.criarUsuarioComCarrinho(testToken, testUserDTO);

        // Assert
        verify(usuarioRepository).findByFirebaseUid(testUid);
        verify(usuarioRepository).save(any(Usuario.class));
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void criarUsuarioComCarrinho_deveLancarExcecao_quandoUsuarioJaExiste() {
        // Arrange
        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setFirebaseUid(testUid);
        when(usuarioRepository.findByFirebaseUid(testUid)).thenReturn(Optional.of(usuarioExistente));

        // Act & Assert
        assertThatThrownBy(() -> usuarioService.criarUsuarioComCarrinho(testToken, testUserDTO))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Usuário já cadastrado");
    }

    @Test
    void criarUsuarioComCarrinho_deveLancarExcecao_quandoUidNaoCorresponde() throws FirebaseAuthException {
        // Arrange
        when(testDecodedToken.getUid()).thenReturn("outro-uid");
        when(firebaseAuth.verifyIdToken(testToken)).thenReturn(testDecodedToken);
        when(usuarioRepository.findByFirebaseUid("outro-uid")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> usuarioService.criarUsuarioComCarrinho(testToken, testUserDTO))
                .isInstanceOf(SecurityException.class)
                .hasMessage("UID do token não corresponde ao UID do usuário");
    }

    @Test
    void criarUsuarioComCarrinho_deveLancarExcecao_quandoEmailNaoCorresponde() throws FirebaseAuthException {
        // Arrange
        when(testDecodedToken.getEmail()).thenReturn("outro@email.com");
        when(firebaseAuth.verifyIdToken(testToken)).thenReturn(testDecodedToken);
        when(usuarioRepository.findByFirebaseUid(testUid)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> usuarioService.criarUsuarioComCarrinho(testToken, testUserDTO))
                .isInstanceOf(SecurityException.class)
                .hasMessage("Email do token não corresponde ao email do usuário");
    }

    @Test
    void criarUsuarioComCarrinho_deveLancarExcecao_quandoTokenInvalido() throws FirebaseAuthException {
        // Arrange
        when(firebaseAuth.verifyIdToken(testToken)).thenThrow(new FirebaseAuthException("invalid-token", "Token inválido"));

        // Act & Assert
        assertThatThrownBy(() -> usuarioService.criarUsuarioComCarrinho(testToken, testUserDTO))
                .isInstanceOf(FirebaseAuthException.class)
                .hasMessageContaining("Token inválido");
    }

    @Test
    void criarUsuarioComCarrinho_deveAssociarCarrinhoAoUsuario() throws FirebaseAuthException {
        // Arrange
        when(usuarioRepository.findByFirebaseUid(testUid)).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuario = invocation.getArgument(0);
            usuario.setId(1L);
            return usuario;
        });

        // Act
        usuarioService.criarUsuarioComCarrinho(testToken, testUserDTO);

        // Assert
        verify(cartRepository).save(argThat(cart ->
                cart.getUser() != null &&
                        cart.getUser().getFirebaseUid().equals(testUid)
        ));
    }
}