package Musicfy.MusicfyOrigin.Product.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "usuarios")
@Data
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    @Column(name = "firebase_uid", unique = true)
    private String firebaseUid;

    // Relacionamento 1:1 com Carrinho
    @OneToOne(mappedBy = "user")  // Agora corresponde ao nome em Cart
    private Cart cart;

    public void setCart(Cart cart) {
        this.cart = cart;
        if (cart != null && cart.getUser() != this) {
            cart.setUser(this);
        }
    }
}