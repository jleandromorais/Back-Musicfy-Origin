package Musicfy.MusicfyOrigin.Product.repository;

import Musicfy.MusicfyOrigin.Product.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario findByFirebaseUid(String firebaseUid);
    boolean existsByFirebaseUid(String firebaseUid);
}