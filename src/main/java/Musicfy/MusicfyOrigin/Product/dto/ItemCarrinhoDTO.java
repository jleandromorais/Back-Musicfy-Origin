package Musicfy.MusicfyOrigin.Product.dto;

import Musicfy.MusicfyOrigin.Product.model.CartItem;import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ItemCarrinhoDTO {
    private String nomeProduto;
    private double precoUnitario;
    private int quantidade;
    private double totalItem;

    public ItemCarrinhoDTO(String nomeProduto, double precoUnitario, int quantidade) {
        this.nomeProduto = nomeProduto;
        this.precoUnitario = precoUnitario;
        this.quantidade = quantidade;
        this.totalItem = precoUnitario * quantidade;
    }

    // Construtor para criar DTO a partir de CartItem
    public ItemCarrinhoDTO(CartItem item) {
        this.nomeProduto = item.getProduct().getName();
        this.precoUnitario = item.getProduct().getPrice();
        this.quantidade = item.getQuantity();
        this.totalItem = precoUnitario * quantidade;
    }
}