package Musicfy.MusicfyOrigin.Product.dto;

import Musicfy.MusicfyOrigin.Product.model.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrderDTO {
    private Long id;
    private String userId; // Firebase UID do usuário
    private String orderDate;
    private OrderStatus status;
    private Double totalPrice;
    private EnderecoDTO deliveryAddress; // Endereço associado ao pedido
    private List<OrderItemDTO> items;
}