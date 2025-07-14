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
    @JsonBackReference
    private Order order;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Double unitPriceAtPurchase;

    // Campos extras para "snapshot" do produto no momento da compra
    @Column(nullable = false)
    private String productName;

    @Column(nullable = true)
    private String productImgPath;

    // Construtor atualizado para receber essas infos
    public OrderItem(Order order, Product product, Integer quantity, Double unitPriceAtPurchase) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.unitPriceAtPurchase = unitPriceAtPurchase;
        this.productName = product.getName();
        this.productImgPath = product.getImgPath();
    }
}
