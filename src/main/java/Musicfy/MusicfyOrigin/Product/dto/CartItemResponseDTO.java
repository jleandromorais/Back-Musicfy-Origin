package Musicfy.MusicfyOrigin.Product.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class CartItemResponseDTO {
    private Long id;
    private Long cartId;
    private ProductDTO product;
    private int quantity;
    private double totalPrice;

    // Construtor que inicializa todos os campos
    public CartItemResponseDTO(Long id, Long cartId, ProductDTO product, int quantity) {
        this.id = id;
        this.cartId = cartId;
        this.product = product;
        this.quantity = quantity;
        this.totalPrice = product.getPrice() * quantity; // calcula o total aqui
    }
}
