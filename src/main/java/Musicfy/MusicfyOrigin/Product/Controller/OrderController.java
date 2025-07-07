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
        List<OrderDTO> orders = orderService.getUserOrders(firebaseUid);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long orderId) {
        try {
            OrderDTO order = orderService.getOrderById(orderId);
            return ResponseEntity.ok(order);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(@PathVariable Long orderId, @RequestBody Map<String, String> statusUpdate) {
        try {
            String newStatusString = statusUpdate.get("status");
            if (newStatusString == null) {
                return ResponseEntity.badRequest().body(null);
            }
            OrderStatus newStatus = OrderStatus.valueOf(newStatusString.toUpperCase());
            OrderDTO updatedOrder = orderService.updateOrderStatus(orderId, newStatus);
            return ResponseEntity.ok(updatedOrder);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // Status inválido fornecido
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}