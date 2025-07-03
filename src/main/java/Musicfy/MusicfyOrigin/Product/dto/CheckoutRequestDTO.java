package Musicfy.MusicfyOrigin.Product.dto;

import java.util.List;

public class CheckoutRequestDTO {

    private List<ItemCarrinhoDTO> items;

    public List<ItemCarrinhoDTO> getItems() {
        return items;
    }

    public void setItems(List<ItemCarrinhoDTO> items) {
        this.items = items;
    }
}