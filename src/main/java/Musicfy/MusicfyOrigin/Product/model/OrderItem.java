package Musicfy.MusicfyOrigin.Product.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "order_item")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference // Para prevenir recursão infinita na serialização JSON
    private Order order;

    @ManyToOne(fetch = FetchType.EAGER) // Carrega os detalhes do produto de forma ansiosa para um item de pedido
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; // Link para o produto real

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Double unitPriceAtPurchase; // Preço no momento da compra

    // Construtor para conveniência
    public OrderItem(Order order, Product product, Integer quantity, Double unitPriceAtPurchase) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.unitPriceAtPurchase = unitPriceAtPurchase;
    }
}