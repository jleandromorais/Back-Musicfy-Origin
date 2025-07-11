package Musicfy.MusicfyOrigin.Product.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
//oka
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "sub_title")
    private String subtitle;

    private Double price;

    @Column(name = "img_path")
    private String imgPath;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "product_features",
            joinColumns = @JoinColumn(name = "product_id")
    )
    @Column(name = "feature_value")
    @Builder.Default
    private List<String> features = new ArrayList<>();

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", price=" + price +
                ", imgPath='" + imgPath + '\'' +
                ", features=" + features +
                '}';
    }
}