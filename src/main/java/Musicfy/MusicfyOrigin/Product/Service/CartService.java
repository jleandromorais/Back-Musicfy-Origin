package Musicfy.MusicfyOrigin.Product.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import Musicfy.MusicfyOrigin.Product.dto.CartItemResponseDTO;
import Musicfy.MusicfyOrigin.Product.dto.ProductDTO;
import Musicfy.MusicfyOrigin.Product.model.*;
import Musicfy.MusicfyOrigin.Product.repository.*;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UsuarioRepository usuarioRepository;

    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public CartItemResponseDTO criarCarrinhoComItem(Long productId, int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade tem que ser positiva");
        }

        Product produto = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado com ID: " + productId));
        Cart carrinho = new Cart();
        CartItem novoItem = new CartItem(produto, quantidade);
        carrinho.addItem(novoItem);

        Cart carrinhoSalvo = cartRepository.save(carrinho);
        CartItem itemSalvo = carrinhoSalvo.getItems().get(0);

        Product produtoSalvo = itemSalvo.getProduct();

        ProductDTO productDTO = new ProductDTO(
                produtoSalvo.getId(),
                produtoSalvo.getName(),
                produtoSalvo.getSubtitle(),
                produtoSalvo.getPrice(),
                produtoSalvo.getImgPath(),
                produtoSalvo.getFeatures()
        );

        return new CartItemResponseDTO(
                itemSalvo.getId(),
                carrinhoSalvo.getId(),
                productDTO,
                itemSalvo.getQuantity()
        );
    }

    @Transactional
    public CartItemResponseDTO adicionarItemACarrinhoExistente(Long cartId, Long productId, int novaquantidade) {
        Cart carrinho = cartRepository.findById(cartId)
                .orElseThrow(() -> new EntityNotFoundException("Carrinho não encontrado"));

        Optional<CartItem> itemExistenteo = carrinho.getItems().stream()
                .filter(item -> item.getProduct() != null && item.getProduct().getId().equals(productId))
                .findFirst();

        if (itemExistenteo.isPresent()) {
            CartItem itemExistente = itemExistenteo.get();
            itemExistente.setQuantity(itemExistente.getQuantity() + novaquantidade);
        } else {
            Product produto = productRepository.findById(productId)
                    .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));
            CartItem novoItem = new CartItem(produto, novaquantidade);
            carrinho.addItem(novoItem);
        }

        Cart carrinhoSalvo = cartRepository.save(carrinho);

        CartItem itemFinal = carrinhoSalvo.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow();

        Product produtoFinal = itemFinal.getProduct();
        ProductDTO productDTO = new ProductDTO(
                produtoFinal.getId(),
                produtoFinal.getName(),
                produtoFinal.getSubtitle(),
                produtoFinal.getPrice(),
                produtoFinal.getImgPath(),
                produtoFinal.getFeatures()
        );

        return new CartItemResponseDTO(
                itemFinal.getId(),
                carrinhoSalvo.getId(),
                productDTO,
                itemFinal.getQuantity()
        );
    }

    @Transactional
    public String removerItemPorProductId(Long cartId, Long productId) {
        int deleted = cartItemRepository.deleteByCartIdAndProductId(cartId, productId);
        if (deleted == 0) {
            throw new EntityNotFoundException("Item não encontrado no carrinho");
        }
        return "Item removido com sucesso";
    }

    @Transactional
    public void incrementarQuantidade(Long cartId, Long productId) {
        CartItem item = cartItemRepository.findByCartIdAndProductId(cartId, productId)
                .orElseThrow(() -> new EntityNotFoundException("Item não encontrado"));
        item.setQuantity(item.getQuantity() + 1);
        cartItemRepository.save(item);
    }

    @Transactional
    public void decrementarQuantidade(Long cartId, Long productId) {
        CartItem item = cartItemRepository.findByCartIdAndProductId(cartId, productId)
                .orElseThrow(() -> new EntityNotFoundException("Item não encontrado"));
        if (item.getQuantity() <= 1) {
            throw new IllegalStateException("Quantidade mínima alcançada");
        }
        item.setQuantity(item.getQuantity() - 1);
        cartItemRepository.save(item);
    }

    @Transactional
    public void limparCarrinho(Long cartid) {
        Cart carrinho = cartRepository.findById(cartid)
                .orElseThrow(() -> new EntityNotFoundException("Nenhum carrinho encontrado com esse ID"));

        carrinho.getItems().clear();
        cartRepository.save(carrinho);
    }
}
