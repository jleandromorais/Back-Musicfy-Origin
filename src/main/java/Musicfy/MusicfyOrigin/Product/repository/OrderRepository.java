package Musicfy.MusicfyOrigin.Product.repository;

import Musicfy.MusicfyOrigin.Product.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Encontra todos os pedidos para um usuário específico
    List<Order> findByUser_FirebaseUidOrderByOrderDateDesc(String firebaseUid);

    // Encontra um pedido pelo seu ID, buscando itens e produtos ansiosamente
    Optional<Order> findById(Long id);
}