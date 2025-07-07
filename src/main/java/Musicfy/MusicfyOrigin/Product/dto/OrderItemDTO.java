package Musicfy.MusicfyOrigin.Product.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderItemDTO {
    private Long id;
    private Long productId;
    private String productName;
    private String productImgPath; // Inclui o caminho da imagem para exibição
    private Integer quantity;
    private Double unitPrice;
    private Double totalPrice; // quantity * unitPrice

    public OrderItemDTO(Long id, Long productId, String productName, String productImgPath, Integer quantity, Double unitPrice) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.productImgPath = productImgPath;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = quantity * unitPrice;
    }
}