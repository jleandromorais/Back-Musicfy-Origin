package Musicfy.MusicfyOrigin.Product.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class ItemCarrinhoRequestDTO {
    private Long productId;  // Campo obrigatório
    @JsonAlias({"img", "imgPath"})
    private String img;  // ou imgPath
    private String name;
    @JsonAlias({"subTitle", "subtitle"})
    private String subTitle; // ou subtitle
    private List<String> features;
    private Double price;
    private Integer quantity;

    // Getters e Setters

}