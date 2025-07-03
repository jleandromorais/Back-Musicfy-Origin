package Musicfy.MusicfyOrigin.Product.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
public class CartDTO {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdated;
    private List<ItemCarrinhoDTO> items;
    private Double total;
}