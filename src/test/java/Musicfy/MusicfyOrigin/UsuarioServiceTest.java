package Musicfy.MusicfyOrigin;

import Musicfy.MusicfyOrigin.Product.Service.UsuarioService;
import Musicfy.MusicfyOrigin.Product.dto.UserDTO;
import Musicfy.MusicfyOrigin.Product.model.Usuario;
import Musicfy.MusicfyOrigin.Product.repository.UsuarioRepository;
import com.google.firebase.auth.FirebaseAuthException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private UserDTO userDTO;
    private Usuario usuario;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        userDTO = new UserDTO();
        userDTO.setFirebaseUid("firebase-uid");
        userDTO.setFullName("Test User");
        userDTO.setEmail("test@example.com");

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setFirebaseUid("firebase-uid");
        usuario.setName("Test User");
        usuario.setEmail("test@example.com");
    }

    @Test
    public void testCriarUsuarioComCarrinho_Success() {
        when(usuarioRepository.findByFirebaseUid(any())).thenReturn(null);
        when(usuarioRepository.save(any())).thenReturn(usuario);

        Usuario result = usuarioService.criarUsuarioComCarrinho(userDTO);

        assertNotNull(result);
        assertEquals(usuario.getId(), result.getId());
        verify(usuarioRepository, times(1)).save(any());
    }

    @Test
    public void testCriarUsuarioComCarrinho_UsuarioJaExiste() {
        when(usuarioRepository.findByFirebaseUid(any())).thenReturn(usuario);

        assertThrows(IllegalStateException.class, () -> {
            usuarioService.criarUsuarioComCarrinho(userDTO);
        });
    }

    @Test
    public void testBuscarUsuarioPorFirebaseUid_UsuarioEncontrado() {
        when(usuarioRepository.findByFirebaseUid(any())).thenReturn(usuario);

        Optional<Usuario> result = usuarioService.buscarUsuarioPorFirebaseUid("firebase-uid");

        assertTrue(result.isPresent());
        assertEquals(usuario.getId(), result.get().getId());
    }

    @Test
    public void testBuscarUsuarioPorFirebaseUid_UsuarioNaoEncontrado() {
        when(usuarioRepository.findByFirebaseUid(any())).thenReturn(null);

        Optional<Usuario> result = usuarioService.buscarUsuarioPorFirebaseUid("firebase-uid");

        assertFalse(result.isPresent());
    }
}