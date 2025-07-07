package Musicfy.MusicfyOrigin.Product.model;

public enum OrderStatus {
    PENDING,        // Pedido recebido, aguardando processamento
    PROCESSING,     // Em separação/preparo
    SHIPPED,        // Enviado para entrega
    OUT_FOR_DELIVERY, // Saiu para entrega (similar a "A caminho" no seu frontend)
    DELIVERED,      // Entregue
    CANCELLED,      // Cancelado
    REFUNDED        // Reembolsado
}
