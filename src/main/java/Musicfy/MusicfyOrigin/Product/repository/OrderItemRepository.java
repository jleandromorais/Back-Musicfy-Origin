package Musicfy.MusicfyOrigin.Product.repository;

import Musicfy.MusicfyOrigin.Product.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // Nenhum método personalizado necessário por enquanto, JpaRepository fornece CRUD básico
}