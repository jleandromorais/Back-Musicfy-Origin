package Musicfy.MusicfyOrigin.Product.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name = "cart", schema = "musicfy") // Recomendado definir a tabela e schema
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Um carrinho tem vários itens
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<CartItem> items = new ArrayList<>();

    // Um carrinho pode pertencer a um usuário
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Usuario user;


    @ManyToOne(fetch = FetchType.LAZY)
    // Relacionamento muitos-para-um: múltiplos carrinhos/pedidos podem usar o mesmo endereço (ex: um usuário pode ter vários carrinhos ou pedidos entregues no mesmo endereço de casa).
    // 'fetch = FetchType.LAZY' para carregar o objeto Address apenas quando necessário.
    @JoinColumn(name = "endereco_id")
    private Endereco endereço;

    /**
     * Método auxiliar para adicionar um item ao carrinho
     * e garantir a consistência do relacionamento bidirecional.
     */
    public void addItem(CartItem item) {
        if (item != null) {
            item.setCart(this); // muito importante
            this.items.add(item);
        }
    }

    /**
     * Método auxiliar para remover um item do carrinho, se necessário.
     */
    public void removeItem(CartItem item) {
        if (item != null) {
            this.items.remove(item);
            item.setCart(null);
        }
    }
}

