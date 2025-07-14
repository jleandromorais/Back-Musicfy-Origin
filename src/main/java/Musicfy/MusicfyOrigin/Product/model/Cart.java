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
@Table(name = "cart")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<CartItem> items = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",unique = true)
    private Usuario user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "endereco_id")
    private Endereco endereço;

    public void addItem(CartItem item) {
        if (item != null) {
            item.setCart(this);
            this.items.add(item);
        }
    }

    public void removeItem(CartItem item) {
        if (item != null) {
            this.items.remove(item);
            item.setCart(null);
        }
    }
}
