package Musicfy.MusicfyOrigin.Product.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequestDTO {

    private List<ItemCarrinhoDTO> items;
    private Long cartId; // ID do carrinho
    private Long userId; // ID do usuário
    private Long enderecoId; // ID do endereço de entrega

}