package Musicfy.MusicfyOrigin.Product.Service;

import Musicfy.MusicfyOrigin.Product.dto.CartDTO;
import Musicfy.MusicfyOrigin.Product.dto.CartItemResponseDTO;
import Musicfy.MusicfyOrigin.Product.dto.ItemCarrinhoDTO;
import Musicfy.MusicfyOrigin.Product.dto.ProductDTO;
import Musicfy.MusicfyOrigin.Product.model.*;
import Musicfy.MusicfyOrigin.Product.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Serviço unificado para gerenciar o Carrinho de Compras.
 * Contém métodos para:
 * 1. Usuários Logados (baseados em `firebaseUid`).
 * 2. Usuários Visitantes/Convidados (baseados em `cartId`).
 */
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

    // ===================================================================
    // == Métodos para Usuários LOGADOS (baseados em firebaseUid)      ==
    // ===================================================================

    /**
     * Adiciona ou atualiza um item no carrinho de um usuário logado.
     */
    @Transactional
    public CartDTO addItemToUserCart(String firebaseUid, Long productId, int quantity) {
        Usuario user = Optional.ofNullable(usuarioRepository.findByFirebaseUid(firebaseUid))
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com Firebase UID: " + firebaseUid));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Carrinho não encontrado para o usuário ID: " + user.getId()));

        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getProduct() != null && item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItem item = existingItemOpt.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
        } else {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado com ID: " + productId));
            CartItem newItem = new CartItem(product, quantity);
            cart.addItem(newItem);
        }

        Cart savedCart = cartRepository.save(cart);
        return convertToDTO(savedCart);
    }

    /**
     * Recupera o carrinho de um usuário logado pelo seu Firebase UID.
     */
    @Transactional(readOnly = true)
    public CartDTO getCartByFirebaseUid(String firebaseUid) {
        Usuario user = Optional.ofNullable(usuarioRepository.findByFirebaseUid(firebaseUid))
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com Firebase UID: " + firebaseUid));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Carrinho não encontrado para o usuário ID: " + user.getId()));

        return convertToDTO(cart);
    }

    /**
     * Método auxiliar para converter a entidade Cart em um DTO.
     */
    private CartDTO convertToDTO(Cart cart) {
        CartDTO cartDTO = new CartDTO();
        cartDTO.setId(cart.getId());
        cartDTO.setItems(cart.getItems().stream()
                .map(ItemCarrinhoDTO::new)
                .collect(Collectors.toList()));

        double total = cart.getItems().stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
        cartDTO.setTotal(total);

        return cartDTO;
    }

    // ===================================================================
    // == Métodos para VISITANTES (baseados em cartId)                 ==
    // ===================================================================

    @Transactional
    public CartItemResponseDTO criarCarrinhoComItem(Long productId, int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade tem que ser positiva");
        }

        Product produto = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado com ID: " + productId));
        Cart carrinho = new Cart(); // Cria um carrinho sem usuário associado
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
    public CartItemResponseDTO adicionarItemACarrinhoExistente(Long cartId, Long productId, int novaQuantidade) {
        Cart carrinho = cartRepository.findById(cartId)
                .orElseThrow(() -> new EntityNotFoundException("Carrinho não encontrado"));

        Optional<CartItem> itemExistenteOpt = carrinho.getItems().stream()
                .filter(item -> item.getProduct() != null && item.getProduct().getId().equals(productId))
                .findFirst();

        if (itemExistenteOpt.isPresent()) {
            CartItem itemExistente = itemExistenteOpt.get();
            itemExistente.setQuantity(itemExistente.getQuantity() + novaQuantidade);
        } else {
            Product produto = productRepository.findById(productId)
                    .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));
            CartItem novoItem = new CartItem(produto, novaQuantidade);
            carrinho.addItem(novoItem);
        }

        Cart carrinhoSalvo = cartRepository.save(carrinho);

        CartItem itemFinal = carrinhoSalvo.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Item final não encontrado após salvar"));

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
    public void incrementarQuantidade(Long cartId, Long productId) {
        CartItem item = cartItemRepository.findByCartIdAndProductId(cartId, productId)
                .orElseThrow(() -> new EntityNotFoundException("Item não encontrado no carrinho para incrementar. CartId: " + cartId + ", ProductId: " + productId));
        item.setQuantity(item.getQuantity() + 1);
        cartItemRepository.save(item);
    }

    @Transactional
    public void decrementarQuantidade(Long cartId, Long productId) {
        CartItem item = cartItemRepository.findByCartIdAndProductId(cartId, productId)
                .orElseThrow(() -> new EntityNotFoundException("Item não encontrado no carrinho para decrementar. CartId: " + cartId + ", ProductId: " + productId));
        if (item.getQuantity() <= 1) {
            // Se a quantidade for 1, o item é removido do carrinho.
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(item.getQuantity() - 1);
            cartItemRepository.save(item);
        }
    }

    @Transactional
    public void limparCarrinho(Long cartId) {
        Cart carrinho = cartRepository.findById(cartId)
                .orElseThrow(() -> new EntityNotFoundException("Nenhum carrinho encontrado com o ID: " + cartId));

        carrinho.getItems().clear();
        cartRepository.save(carrinho);
    }
}