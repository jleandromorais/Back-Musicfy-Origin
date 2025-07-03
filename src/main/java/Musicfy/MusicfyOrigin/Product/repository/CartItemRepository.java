package Musicfy.MusicfyOrigin.Product.repository;

import Musicfy.MusicfyOrigin.Product.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // Consulta JPQL para buscar por cartId e productId
    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.id = :cartId AND ci.product.id = :productId")
    Optional<CartItem> findByCartIdAndProductId(
            @Param("cartId") Long cartId,
            @Param("productId") Long productId
    );

    // Delete por cartId e productId
    @Modifying
    @Transactional
    @Query("DELETE FROM CartItem ci WHERE ci.cart.id = :cartId AND ci.product.id = :productId")
    int deleteByCartIdAndProductId(
            @Param("cartId") Long cartId,
            @Param("productId") Long productId
    );

    // Consulta por productId apenas
    @Query("SELECT ci FROM CartItem ci WHERE ci.product.id = :productId")
    List<CartItem> findByProductId(@Param("productId") Long productId);

    // Delete todos os itens com determinado productId
    @Modifying
    @Transactional
    @Query("DELETE FROM CartItem ci WHERE ci.product.id = :productId")
    void deleteAllByProductId(@Param("productId") Long productId);

    // Consulta nativa (opcional)
    @Query(value = "SELECT * FROM cart_item WHERE product_id = :productId", nativeQuery = true)
    List<CartItem> findByProductIdNative(@Param("productId") Long productId);
}