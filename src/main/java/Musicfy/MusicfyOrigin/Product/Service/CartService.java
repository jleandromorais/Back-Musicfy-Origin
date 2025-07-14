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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    // ================================
    // Para usuários logados
    // ================================

    @Transactional
    public CartDTO addItemToUserCart(String firebaseUid, Long productId, int quantity) {
        Usuario user = Optional.ofNullable(usuarioRepository.findByFirebaseUid(firebaseUid))
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com Firebase UID: " + firebaseUid));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return newCart;
                });

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado com ID: " + productId));

        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getProduct() != null && item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItem item = existingItemOpt.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item); // Atualiza o item
        } else {
            CartItem newItem = new CartItem();
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setCart(cart); // Importante para relacionamento bidirecional
            cart.getItems().add(newItem); // Adiciona o item à coleção do carrinho
        }

        Cart savedCart = cartRepository.save(cart);
        return convertToDTO(savedCart);
    }

    @Transactional(readOnly = true)
    public CartDTO getCartByFirebaseUid(String firebaseUid) {
        Usuario user = Optional.ofNullable(usuarioRepository.findByFirebaseUid(firebaseUid))
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com Firebase UID: " + firebaseUid));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Carrinho não encontrado para o usuário ID: " + user.getId()));

        return convertToDTO(cart);
    }

    @Transactional
    public CartDTO mergeTemporaryCart(Long userId, List<ItemCarrinhoDTO> tempItems) {
        Usuario user = usuarioRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + userId));

        Cart userCart = cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return newCart;
                });

        for (ItemCarrinhoDTO tempItem : tempItems) {
            Product product = productRepository.findById(tempItem.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado com ID: " + tempItem.getProductId()));

            Optional<CartItem> existingItemOpt = userCart.getItems().stream()
                    .filter(item -> item.getProduct() != null && item.getProduct().getId().equals(tempItem.getProductId()))
                    .findFirst();

            if (existingItemOpt.isPresent()) {
                CartItem itemToUpdate = existingItemOpt.get();
                itemToUpdate.setQuantity(itemToUpdate.getQuantity() + tempItem.getQuantidade());
                cartItemRepository.save(itemToUpdate);
            } else {
                CartItem newItem = new CartItem();
                newItem.setProduct(product);
                newItem.setQuantity(tempItem.getQuantidade());
                newItem.setCart(userCart);
                userCart.getItems().add(newItem);
            }
        }

        Cart savedCart = cartRepository.save(userCart);
        return convertToDTO(savedCart);
    }

    // ================================
    // Para visitantes/convidados
    // ================================

    @Transactional
    public CartItemResponseDTO criarCarrinhoComItem(Long productId, int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade tem que ser positiva");
        }

        Product produto = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado com ID: " + productId));
        Cart carrinho = new Cart();
        CartItem novoItem = new CartItem();
        novoItem.setProduct(produto);
        novoItem.setQuantity(quantidade);
        novoItem.setCart(carrinho);
        carrinho.getItems().add(novoItem);

        Cart carrinhoSalvo = cartRepository.save(carrinho);

        CartItem itemSalvo = carrinhoSalvo.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Item não encontrado após salvar o carrinho."));

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
            cartItemRepository.save(itemExistente);
        } else {
            Product produto = productRepository.findById(productId)
                    .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));
            CartItem novoItem = new CartItem();
            novoItem.setProduct(produto);
            novoItem.setQuantity(novaQuantidade);
            novoItem.setCart(carrinho);
            carrinho.getItems().add(novoItem);
            cartRepository.save(carrinho);
        }

        CartItem itemFinal = carrinho.getItems().stream()
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
                carrinho.getId(),
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

    @Transactional
    public void removerItemDoCarrinho(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new EntityNotFoundException("Carrinho não encontrado: " + cartId));

        boolean removed = cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));

        if (!removed) {
            throw new EntityNotFoundException("Produto não encontrado no carrinho: " + productId);
        }
        cartRepository.save(cart);
    }

    private CartDTO convertToDTO(Cart cart) {
        CartDTO cartDTO = new CartDTO();
        cartDTO.setId(cart.getId());
        cartDTO.setItems(cart.getItems().stream()
                .map(ItemCarrinhoDTO::new)
                .collect(Collectors.toList()));

        double total = cart.getItems().stream()
                .mapToDouble(item -> item.getProduct() != null ? item.getProduct().getPrice() * item.getQuantity() : 0.0)
                .sum();
        cartDTO.setTotal(total);

        return cartDTO;
    }
}