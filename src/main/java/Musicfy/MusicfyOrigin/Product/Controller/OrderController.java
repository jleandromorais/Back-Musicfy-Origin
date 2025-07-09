package Musicfy.MusicfyOrigin.Product.Controller;

import Musicfy.MusicfyOrigin.Product.Service.OrderService;
import Musicfy.MusicfyOrigin.Product.dto.OrderDTO;
import Musicfy.MusicfyOrigin.Product.model.OrderStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:5173")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/user/{firebaseUid}")
    public ResponseEntity<List<OrderDTO>> getUserOrders(@PathVariable String firebaseUid) {
        if (firebaseUid == null || firebaseUid.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        List<OrderDTO> orders = orderService.getUserOrders(firebaseUid);
        System.out.println("✅ Pedidos do usuário recuperados com sucesso!");
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long orderId) {
        if (orderId == null || orderId <= 0) {
            return ResponseEntity.badRequest().build();
        }
        try {
            OrderDTO order = orderService.getOrderById(orderId);
            System.out.println("✅ Pedido recuperado com sucesso! ID: " + orderId);
            return ResponseEntity.ok(order);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long orderId, @RequestBody Map<String, String> statusUpdate) {
        if (orderId == null || orderId <= 0) {
            return ResponseEntity.badRequest().body("ID do pedido inválido");
        }
        if (statusUpdate == null || !statusUpdate.containsKey("status")) {
            return ResponseEntity.badRequest().body("Campo 'status' obrigatório");
        }
        String newStatusString = statusUpdate.get("status");
        if (newStatusString == null || newStatusString.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Campo 'status' não pode ser vazio");
        }
        try {
            OrderStatus newStatus = OrderStatus.valueOf(newStatusString.toUpperCase());
            OrderDTO updatedOrder = orderService.updateOrderStatus(orderId, newStatus);

            System.out.println("✅ Status do pedido atualizado com sucesso! ID: " + orderId + ", Novo status: " + newStatus);

            if (newStatus == OrderStatus.CANCELLED) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(updatedOrder);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Status inválido: " + newStatusString);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pedido não encontrado: " + orderId);
        }
    }

}