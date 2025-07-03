package Musicfy.MusicfyOrigin.Product.Controller;

import jakarta.persistence.EntityNotFoundException;
import Musicfy.MusicfyOrigin.Product.Service.CartService;
import Musicfy.MusicfyOrigin.Product.dto.CartItemResponseDTO;
import Musicfy.MusicfyOrigin.Product.dto.ItemCarrinhoRequestDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @RestController: Define a classe como um controller REST, que retorna JSON por padrão.
 * @RequestMapping: Estabelece o caminho base para todos os endpoints nesta classe.
 * @CrossOrigin: Permite requisições do frontend (React/Vite) que roda em localhost:5173.
 */
@RestController
@RequestMapping("/api/carrinho")
@CrossOrigin(origins = "http://localhost:5173")
public class CartController {

    private final CartService cartService;

    // Injeção de dependência do CartService via construtor.
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * Endpoint para CRIAR um novo carrinho com um item.
     * @param request O corpo da requisição JSON, contendo productId e quantity.
     * @return Uma resposta HTTP 201 (Created) com o DTO do item criado.
     */
    @PostMapping("/criar")
    public ResponseEntity<CartItemResponseDTO> criarCarrinho(
            @RequestBody ItemCarrinhoRequestDTO request
    ) {
        try {
            // Validação básica da entrada.
            if (request.getProductId() == null || request.getQuantity() <= 0) {
                return ResponseEntity.badRequest().build();
            }
            // Chama o serviço para executar a lógica de negócio.
            CartItemResponseDTO responseDto = cartService.criarCarrinhoComItem(
                    request.getProductId(),
                    request.getQuantity()
            );
            return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            // Se o produto não for encontrado, retorna 404 Not Found.
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Endpoint para ADICIONAR um item a um carrinho já existente.
     * @param cartId O ID do carrinho, vindo da URL.
     * @param request O corpo da requisição com os dados do item.
     * @return Uma resposta HTTP 200 (OK) com o DTO do item adicionado/atualizado.
     */
    @PostMapping("/{cartId}/adicionar")
    public ResponseEntity<CartItemResponseDTO> adicionarItem(
            @PathVariable Long cartId,
            @RequestBody ItemCarrinhoRequestDTO request
    ) {
        try {
            if (request.getProductId() == null || request.getQuantity() <= 0) {
                return ResponseEntity.badRequest().build();
            }
            CartItemResponseDTO responseDto = cartService.adicionarItemACarrinhoExistente(
                    cartId,
                    request.getProductId(),
                    request.getQuantity()
            );
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Endpoint para REMOVER um item de um carrinho.
     * @param cartId O ID do carrinho.
     * @param productId O ID do produto a ser removido.
     * @return Uma resposta HTTP 200 (OK) com uma mensagem de sucesso.
     */
    @DeleteMapping("/{cartId}/remover/{productId}")
    public ResponseEntity<String> removerItem(
            @PathVariable Long cartId,
            @PathVariable Long productId
    ) {
        try {
            String resultado = cartService.removerItemPorProductId(cartId, productId);
            return ResponseEntity.ok(resultado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    /**
     * Endpoint para INCREMENTAR a quantidade de um item.
     * @return Uma resposta HTTP 204 (No Content) indicando sucesso sem corpo.
     */
    @PatchMapping("/{cartId}/incrementar/{productId}")
    public ResponseEntity<Void> incrementarQuantidade(
            @PathVariable Long cartId,
            @PathVariable Long productId
    ) {
        cartService.incrementarQuantidade(cartId, productId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint para DECREMENTAR a quantidade de um item.
     * @return Uma resposta HTTP 204 (No Content) indicando sucesso.
     */
    @PatchMapping("/{cartId}/decrementar/{productId}")
    public ResponseEntity<Void> decrementarQuantidade(
            @PathVariable Long cartId,
            @PathVariable Long productId
    ) {
        cartService.decrementarQuantidade(cartId, productId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Manipulador de exceções local para este controller.
     * Captura exceções de negócio específicas e retorna uma resposta 400 (Bad Request).
     */
    @ExceptionHandler({EntityNotFoundException.class, IllegalStateException.class})
    public ResponseEntity<String> handleBusinessExceptions(RuntimeException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
    @DeleteMapping("/{cartid}/limpar")
    public ResponseEntity<Void> limparCarrinho(@PathVariable("cartid") Long cartid) {
        cartService.limparCarrinho(cartid);
        return ResponseEntity.noContent().build(); // This sends an HTTP 204 No Content response, which has no body.
    }
}
