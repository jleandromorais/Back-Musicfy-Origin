package Musicfy.MusicfyOrigin.Product.repository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import Musicfy.MusicfyOrigin.Product.model.Product;

import java.util.Optional;

@Repository
public interface ProductRepository  extends JpaRepository<Product,Long> {
        // Aqui você pode adicionar métodos personalizados, se necessário
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.features WHERE p.id = :id")
    Optional<Product> findByIdWithFeatures(@Param("id") Long id);

    default Product getByIdWithFeatures(Long id) {
        return findByIdWithFeatures(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));
    }
}
