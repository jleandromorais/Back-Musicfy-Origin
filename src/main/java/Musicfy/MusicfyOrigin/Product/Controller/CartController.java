package Musicfy.MusicfyOrigin.Product.Controller;

import Musicfy.MusicfyOrigin.Product.Service.CartService;
import Musicfy.MusicfyOrigin.Product.dto.CartDTO;
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

/**
 * Controller unificado para gerenciar o Carrinho de Compras.
 * Contém endpoints para:
 * 1. Usuários Logados (baseados em `firebaseUid`).
 * 2. Usuários Visitantes/Convidados (baseados em `cartId`).
 */
@RestController
@RequestMapping("/api/carrinho")
public class CartController {

    @Autowired
    private CartService cartService;

    // A injeção direta do repositório no controller não é uma boa prática.
    // A lógica de negócio, como a remoção de um item, deve estar no Service.
    // Mantido por compatibilidade com o código enviado, mas idealmente seria refatorado.
    @Autowired
    private CartRepository cartRepository;

    // ===================================================================
    // == Endpoints para Usuários LOGADOS (baseados em firebaseUid)      ==
    // ===================================================================

    /**
     * Adiciona ou atualiza um item no carrinho de um usuário logado.
     * @param firebaseUid O ID do Firebase do usuário.
     * @param request O corpo da requisição com productId e quantity.
     * @return O estado atualizado do carrinho.
     */
    @PostMapping("/user/{firebaseUid}/adicionar")
    public ResponseEntity<CartDTO> adicionarItemAoCarrinhoLogado(
            @PathVariable String firebaseUid,
            @RequestBody ItemCarrinhoRequestDTO request) {

        System.out.println("✅ LOGADO: Adicionando produto " + request.getProductId() + " ao carrinho do usuário " + firebaseUid);
        CartDTO updatedCart = cartService.addItemToUserCart(
                firebaseUid,
                request.getProductId(),
                request.getQuantity()
        );
        return ResponseEntity.ok(updatedCart);
    }

    /**
     * Recupera o carrinho completo de um usuário logado.
     * @param firebaseUid O ID do Firebase do usuário.
     * @return O carrinho do usuário.
     */
    @GetMapping("/user/{firebaseUid}")
    public ResponseEntity<CartDTO> getCarrinhoDoUsuario(@PathVariable String firebaseUid) {
        System.out.println("✅ LOGADO: Buscando carrinho para o usuário " + firebaseUid);
        CartDTO cart = cartService.getCartByFirebaseUid(firebaseUid);
        return ResponseEntity.ok(cart);
    }


    // ===================================================================
    // == Endpoints para VISITANTES (baseados em cartId)                 ==
    // ===================================================================

    /**
     * Cria um novo carrinho para um visitante com o primeiro item.
     * O frontend deve armazenar o 'cartId' retornado para operações futuras.
     */
    @PostMapping("/criar")
    public ResponseEntity<Map<String, Object>> criarCarrinho(@RequestBody ItemCarrinhoRequestDTO request) {
        System.out.println("✅ VISITANTE: Criando carrinho com o primeiro item...");
        // Supondo que o CartService tenha este método.
        CartItemResponseDTO cartItemDTO = cartService.criarCarrinhoComItem(request.getProductId(), request.getQuantity());
        System.out.println("✅ VISITANTE: Carrinho criado com sucesso - ID: " + cartItemDTO.getCartId());

        Map<String, Object> response = new HashMap<>();
        response.put("cartId", cartItemDTO.getCartId());
        response.put("item", cartItemDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Adiciona um item a um carrinho de visitante existente.
     */
    @PostMapping("/{cartId}/adicionar")
    public ResponseEntity<CartItemResponseDTO> adicionarItem(
            @PathVariable Long cartId,
            @RequestBody ItemCarrinhoRequestDTO request) {

        System.out.println("✅ VISITANTE: Adicionando produto " + request.getProductId() + " no carrinho: " + cartId);
        // Supondo que o CartService tenha este método.
        CartItemResponseDTO itemAtualizado = cartService.adicionarItemACarrinhoExistente(
                cartId,
                request.getProductId(),
                request.getQuantity()
        );

        System.out.println("✅ VISITANTE: Item adicionado com sucesso!");
        return ResponseEntity.ok(itemAtualizado);
    }

    /**
     * Incrementa a quantidade de um item no carrinho de um visitante.
     */
    @PatchMapping("/{cartId}/incrementar/{productId}")
    public ResponseEntity<String> incrementarQuantidade(@PathVariable Long cartId, @PathVariable Long productId) {
        System.out.println("✅ VISITANTE: Incrementando quantidade do produto " + productId + " no carrinho " + cartId);
        try {
            // Supondo que o CartService tenha este método.
            cartService.incrementarQuantidade(cartId, productId);
            return ResponseEntity.ok("Quantidade incrementada com sucesso");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item não encontrado no carrinho para incrementar");
        }
    }

    /**
     * Decrementa a quantidade de um item no carrinho de um visitante.
     */
    @PatchMapping("/{cartId}/decrementar/{productId}")
    public ResponseEntity<String> decrementarQuantidade(@PathVariable Long cartId, @PathVariable Long productId) {
        System.out.println("✅ VISITANTE: Decrementando quantidade do produto " + productId + " no carrinho " + cartId);
        try {
            // Supondo que o CartService tenha este método.
            cartService.decrementarQuantidade(cartId, productId);
            return ResponseEntity.ok("Quantidade decrementada com sucesso");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item não encontrado no carrinho para decrementar");
        }
    }

    /**
     * Remove um item do carrinho de um visitante.
     * RECOMENDAÇÃO: Mover a lógica para dentro do CartService.
     */
    @DeleteMapping("/{cartId}/remover/{productId}")
    public ResponseEntity<String> removerItem(@PathVariable Long cartId, @PathVariable Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new EntityNotFoundException("Carrinho não encontrado: " + cartId));

        boolean removed = cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));

        if (!removed) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto não encontrado no carrinho.");
        }

        cartRepository.save(cart);
        System.out.println("✅ VISITANTE: Produto " + productId + " removido com sucesso do carrinho " + cartId);
        return ResponseEntity.ok("Produto removido com sucesso.");
    }

    /**
     * Limpa todos os itens de um carrinho de visitante.
     */
    @DeleteMapping("/{cartId}/limpar")
    public ResponseEntity<String> limparCarrinho(@PathVariable Long cartId) {
        System.out.println("✅ VISITANTE: Limpando carrinho id " + cartId);
        // Supondo que o CartService tenha este método.
        cartService.limparCarrinho(cartId);
        return ResponseEntity.ok("Carrinho limpo com sucesso");
    }
}