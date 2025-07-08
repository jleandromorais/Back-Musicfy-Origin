package Musicfy.MusicfyOrigin.Product.Controller;

import Musicfy.MusicfyOrigin.Product.Service.CartService;
import Musicfy.MusicfyOrigin.Product.dto.CartItemResponseDTO;
import Musicfy.MusicfyOrigin.Product.dto.ItemCarrinhoRequestDTO;
import Musicfy.MusicfyOrigin.Product.model.Cart;
import Musicfy.MusicfyOrigin.Product.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityNotFoundException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/carrinho")
public class CartController {

    @Autowired
    private CartService cartService;
    @Autowired
    private CartRepository cartRepository;

    // Criar carrinho com o primeiro item
    @PostMapping("/criar")
    public ResponseEntity<Map<String, Object>> criarCarrinho(@RequestBody ItemCarrinhoRequestDTO request) {
        System.out.println("✅ Criando carrinho com o primeiro item...");
        CartItemResponseDTO cartItemDTO = cartService.criarCarrinhoComItem(request.getProductId(), request.getQuantity());
        System.out.println("✅ Carrinho criado com sucesso - ID: " + cartItemDTO.getCartId());

        Map<String, Object> response = new HashMap<>();
        response.put("cartId", cartItemDTO.getCartId());
        response.put("item", cartItemDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Adicionar ou atualizar item no carrinho existente
    @PostMapping("/{cartId}/adicionar")
    public ResponseEntity<CartItemResponseDTO> adicionarItem(
            @PathVariable Long cartId,
            @RequestBody ItemCarrinhoRequestDTO request) {

        System.out.println("✅ Adicionando/Atualizando produto " + request.getProductId() + " no carrinho: " + cartId);

        CartItemResponseDTO itemAtualizado = cartService.adicionarItemACarrinhoExistente(
                cartId,
                request.getProductId(),
                request.getQuantity()
        );

        System.out.println("✅ Item adicionado/atualizado com sucesso!");
        return ResponseEntity.ok(itemAtualizado);
    }

    // Incrementar quantidade do item já existente no carrinho
    @PatchMapping("/{cartId}/incrementar/{productId}")
    public ResponseEntity<String> incrementarQuantidade(@PathVariable Long cartId, @PathVariable Long productId) {
        System.out.println("✅ Incrementando quantidade do produto " + productId + " no carrinho " + cartId);
        try {
            cartService.incrementarQuantidade(cartId, productId); // chama o service que incrementa
            System.out.println("✅ Quantidade incrementada com sucesso");
            return ResponseEntity.ok("Quantidade incrementada com sucesso");
        } catch (EntityNotFoundException e) {
            System.err.println("❌ Erro: Item não encontrado para incrementar quantidade");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item não encontrado no carrinho para incrementar");
        }
    }

    // Decrementar quantidade do item já existente no carrinho
    @PatchMapping("/{cartId}/decrementar/{productId}")
    public ResponseEntity<String> decrementarQuantidade(@PathVariable Long cartId, @PathVariable Long productId) {
        System.out.println("✅ Decrementando quantidade do produto " + productId + " no carrinho " + cartId);
        try {
            cartService.decrementarQuantidade(cartId, productId);
            System.out.println("✅ Quantidade decrementada com sucesso");
            return ResponseEntity.ok("Quantidade decrementada com sucesso");
        } catch (EntityNotFoundException e) {
            System.err.println("❌ Erro: Item não encontrado para decrementar quantidade");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item não encontrado no carrinho para decrementar");
        }
    }
    @DeleteMapping("/{cartId}/remover/{productId}")
    public ResponseEntity<String> removerItem(@PathVariable Long cartId, @PathVariable Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new EntityNotFoundException("Carrinho não encontrado: " + cartId));

        boolean removed = cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));

        if (!removed) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto não encontrado no carrinho.");
        }

        cartRepository.save(cart);
        System.out.println("✅ Produto " + productId + " removido com sucesso do carrinho " + cartId);
        return ResponseEntity.ok("Produto removido com sucesso.");
    }

    // Limpar carrinho (a implementar)
    @DeleteMapping("/{cartId}/limpar")
    public ResponseEntity<String> limparCarrinho(@PathVariable Long cartId) {
        System.out.println("✅ Limpando carrinho id " + cartId);
        cartService.limparCarrinho(cartId); // supondo que existe esse método no service
        System.out.println("✅ Carrinho limpo com sucesso");
        return ResponseEntity.ok("Carrinho limpo com sucesso");
    }
}
