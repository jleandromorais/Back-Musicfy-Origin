package Musicfy.MusicfyOrigin.Product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ItemCarrinho {
    private Product productId;  // corrigido aqui: tipo Product
    private int quantidade;

    public Product getProductID(Long productId) {  // getter correto, sem parâmetro
        return this.productId;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public void incrementarQuantidade(int valor) {
        this.quantidade += valor;
    }
}