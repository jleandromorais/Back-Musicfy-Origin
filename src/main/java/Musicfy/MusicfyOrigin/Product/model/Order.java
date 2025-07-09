package Musicfy.MusicfyOrigin.Product.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "orders") // Usando "orders" para evitar conflito com a palavra-chave "Order" em alguns bancos de dados
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Usuario user;

    @Column(nullable = false)
    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING) // Armazena o enum como String no DB
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private Double totalPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "endereco_id") // Opcional: Link para um endereço específico para o pedido
    private Endereco deliveryAddress;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // Para prevenir recursão infinita na serialização JSON
    private List<OrderItem> items = new ArrayList<>();

    // Método auxiliar para adicionar itens e manter o relacionamento bidirecional.
    public void addOrderItem(OrderItem item) {
        if (item != null) {
            item.setOrder(this);
            this.items.add(item);
        }
    }

    // Método auxiliar para remover itens.
    public void removeOrderItem(OrderItem item) {
        if (item != null) {
            this.items.remove(item);
            item.setOrder(null);
        }
    }
}