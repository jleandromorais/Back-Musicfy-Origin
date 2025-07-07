package Musicfy.MusicfyOrigin.Product.Controller;

import Musicfy.MusicfyOrigin.Product.dto.ItemCarrinhoRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrinho")
public class CartController {

    @PostMapping("/criar")
    public ResponseEntity<String> criarCarrinho(@RequestBody ItemCarrinhoRequestDTO request) {
        System.out.println("✅ Criando carrinho...");
        // lógica para criar o carrinho
        System.out.println("✅ Carrinho criado com sucesso - ok");
        return ResponseEntity.ok("ok");
    }

    @PostMapping("/{cartId}/adicionar")
    public ResponseEntity<String> adicionarItem(@PathVariable Long cartId, @RequestBody ItemCarrinhoRequestDTO request) {
        System.out.println("✅ Adicionando item no carrinho: " + cartId);
        // lógica para adicionar item
        System.out.println("✅ Item adicionado com sucesso ao carrinho " + cartId + " - ok");
        return ResponseEntity.ok("ok");
    }

    @DeleteMapping("/{cartId}/remover/{productId}")
    public ResponseEntity<String> removerItem(@PathVariable Long cartId, @PathVariable Long productId) {
        System.out.println("✅ Removendo produto " + productId + " do carrinho " + cartId);
        // lógica para remover item
        System.out.println("✅ Produto removido com sucesso do carrinho " + cartId + " - ok");
        return ResponseEntity.ok("ok");
    }

    @PatchMapping("/{cartId}/incrementar/{productId}")
    public ResponseEntity<Void> incrementarQuantidade(@PathVariable Long cartId, @PathVariable Long productId) {
        System.out.println("✅ Incrementando quantidade do produto " + productId + " no carrinho " + cartId);
        // lógica para incrementar quantidade
        System.out.println("✅ Quantidade incrementada com sucesso - ok");
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{cartId}/decrementar/{productId}")
    public ResponseEntity<Void> decrementarQuantidade(@PathVariable Long cartId, @PathVariable Long productId) {
        System.out.println("✅ Decrementando quantidade do produto " + productId + " no carrinho " + cartId);
        // lógica para decrementar quantidade
        System.out.println("✅ Quantidade decrementada com sucesso - ok");
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{cartid}/limpar")
    public ResponseEntity<Void> limparCarrinho(@PathVariable Long cartid) {
        System.out.println("✅ Limpando carrinho id " + cartid);
        // lógica para limpar carrinho
        System.out.println("✅ Carrinho limpo com sucesso - ok");
        return ResponseEntity.ok().build();
    }
}