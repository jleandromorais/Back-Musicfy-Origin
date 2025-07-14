package Musicfy.MusicfyOrigin.Product.dto;

import Musicfy.MusicfyOrigin.Product.model.CartItem;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ItemCarrinhoDTO {
    private String nomeProduto;
    private Long productId;

    private double precoUnitario;
    private int quantidade;
    private double totalItem;

    public ItemCarrinhoDTO(String nomeProduto, double precoUnitario, int quantidade) {
        this.nomeProduto = nomeProduto;
        this.precoUnitario = precoUnitario;
        this.quantidade = quantidade;
        this.totalItem = precoUnitario * quantidade;
    }

    public ItemCarrinhoDTO(CartItem item) {
        if (item != null && item.getProduct() != null) {
            this.productId = item.getProduct().getId();
            this.nomeProduto = item.getProduct().getName();
            this.precoUnitario = item.getProduct().getPrice();
            this.quantidade = item.getQuantity();
            this.totalItem = this.precoUnitario * this.quantidade;
        } else if (item != null) {
            this.productId = null;
            this.nomeProduto = null;
            this.precoUnitario = 0.0;
            this.quantidade = item.getQuantity();
            this.totalItem = 0.0;
        } else {
            this.productId = null;
            this.nomeProduto = null;
            this.precoUnitario = 0.0;
            this.quantidade = 0;
            this.totalItem = 0.0;
        }
    }

    public ItemCarrinhoDTO(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantidade = quantity != null ? quantity : 0;
        this.nomeProduto = null;
        this.precoUnitario = 0.0;
        this.totalItem = 0.0;
    }
}