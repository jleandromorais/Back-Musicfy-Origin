package Musicfy.MusicfyOrigin.Product.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ProductDTO {
    private Long id;
    private String name;
    private String subtitle;
    private Double price;
    private String imgPath;
    private List<String> features;

    // Construtor simplificado para casos onde só temos id e nome
    public ProductDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    // Se quiser usar este construtor completo em outro lugar, pode implementar:
    public ProductDTO(Long id, String name, String subtitle, Double price, String imgPath, List<String> features) {
        this.id = id;
        this.name = name;
        this.subtitle = subtitle;
        this.price = price;
        this.imgPath = imgPath;
        this.features = features;
    }
}
