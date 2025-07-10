package Musicfy.MusicfyOrigin.Product.Service;

import Musicfy.MusicfyOrigin.Product.model.Endereco;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import Musicfy.MusicfyOrigin.Product.dto.ItemCarrinhoDTO;
import Musicfy.MusicfyOrigin.Product.model.*; // Importa todos os modelos
import Musicfy.MusicfyOrigin.Product.repository.*; // Importa todos os repositórios
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StripeService {

    public static final Logger logger = LoggerFactory.getLogger(StripeService.class);

    @Value("${stripe.secret.key}")
    private String stripeApiKey;

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final UsuarioRepository usuarioRepository;
    private final EnderecoRepository enderecoRepository;
    private final ObjectMapper objectMapper; // Para serializar/deserializar JSON

    public StripeService(CartRepository cartRepository, ProductRepository productRepository,
                         OrderRepository orderRepository, UsuarioRepository usuarioRepository,
                         EnderecoRepository enderecoRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.usuarioRepository = usuarioRepository;
        this.enderecoRepository = enderecoRepository;
        this.objectMapper = new ObjectMapper();
    }

    @PostConstruct
    public void init() {
        if (stripeApiKey == null || stripeApiKey.trim().isEmpty()) {
            logger.error("""
                #########################################################
                ERRO CRÍTICO: Chave Stripe não configurada!
                Por favor, defina no application.properties:
                stripe.secret.key=sua_chave_stripe_aqui
                #########################################################""");
            throw new IllegalStateException("Chave Stripe não configurada.");
        }
        Stripe.apiKey = stripeApiKey;
        logger.info("Stripe SDK configurado com sucesso.");
    }

    @Transactional
    public Session createCheckoutSession(Long cartId, Long userId, Long enderecoId, List<ItemCarrinhoDTO> items) throws StripeException, IOException {
        String successUrl = "https://musicfy-two.vercel.app/success?session_id={CHECKOUT_SESSION_ID}";
        String cancelUrl = "https://musicfy-two.vercel.app/cancel";

        // NOVO: Monta uma nova lista de DTOs com productId incluído
        List<ItemCarrinhoDTO> enrichedItems = items.stream()
                .map(item -> {
                    Product produto = productRepository.findByName(item.getNomeProduto())
                            .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado ao criar checkout: " + item.getNomeProduto()));

                    ItemCarrinhoDTO dto = new ItemCarrinhoDTO();
                    dto.setProductId(produto.getId()); // ESSENCIAL!
                    dto.setNomeProduto(produto.getName());
                    dto.setPrecoUnitario(produto.getPrice());
                    dto.setQuantidade(item.getQuantidade());
                    return dto;
                }).collect(Collectors.toList());

        // Agora gera os lineItems para Stripe com base nessa lista
        List<SessionCreateParams.LineItem> lineItems = enrichedItems.stream()
                .map(item -> {
                    return SessionCreateParams.LineItem.builder()
                            .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency("brl")
                                    .setUnitAmount((long) (item.getPrecoUnitario() * 100))
                                    .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                            .setName(item.getNomeProduto())
                                            .build())
                                    .build())
                            .setQuantity((long) item.getQuantidade())
                            .build();
                }).collect(Collectors.toList());

        // Serializa a lista para armazenar nos metadados
        String itemsJson = objectMapper.writeValueAsString(enrichedItems);

        SessionCreateParams params = SessionCreateParams.builder()
                .addAllLineItem(lineItems)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .putMetadata("cartId", String.valueOf(cartId))
                .putMetadata("userId", String.valueOf(userId))
                .putMetadata("enderecoId", String.valueOf(enderecoId))
                .putMetadata("itemsJson", itemsJson)
                .build();

        return Session.create(params);
    }

    @Transactional
    public Order fulfillOrder(Session session) throws IOException {
        logger.info("Iniciando processamento de pedido para a sessão Stripe ID: {}", session.getId());

        // Recupera os metadados
        Long cartId = Long.valueOf(session.getMetadata().get("cartId"));
        Long userId = Long.valueOf(session.getMetadata().get("userId"));
        Long enderecoId = Long.valueOf(session.getMetadata().get("enderecoId"));
        String itemsJson = session.getMetadata().get("itemsJson");

        // Valida se as informações essenciais estão presentes
        if (itemsJson == null || itemsJson.isEmpty()) {
            logger.error("Falha ao processar pedido: 'itemsJson' não encontrado nos metadados da sessão {}", session.getId());
            throw new IllegalStateException("Metadados de itens ausentes na sessão de checkout.");
        }

        Usuario user = usuarioRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado para o ID: " + userId));
        Endereco endereco = enderecoRepository.findById(enderecoId)
                .orElseThrow(() -> new EntityNotFoundException("Endereço não encontrado para o ID: " + enderecoId));
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new EntityNotFoundException("Carrinho não encontrado para o ID: " + cartId));

        // Deserializa os itens do JSON
        List<ItemCarrinhoDTO> itemsFromCheckout = objectMapper.readValue(itemsJson, new TypeReference<List<ItemCarrinhoDTO>>() {});

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PROCESSING);
        order.setDeliveryAddress(endereco);

        for (ItemCarrinhoDTO itemDTO : itemsFromCheckout) {
            // Busca o produto pelo ID, NÃO pelo nome
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado: ID " + itemDTO.getProductId()));

            OrderItem orderItem = new OrderItem(
                    order,
                    product,
                    itemDTO.getQuantidade(),
                    itemDTO.getPrecoUnitario()
            );
            order.addOrderItem(orderItem);
        }

        // Usa o total da sessão Stripe para o preço total do pedido
        double totalOrderPrice = (double) session.getAmountTotal() / 100.0;
        order.setTotalPrice(totalOrderPrice);

        Order savedOrder = orderRepository.save(order);
        logger.info("Pedido ID {} salvo com sucesso para o usuário ID {}.", savedOrder.getId(), userId);

        // Limpa o carrinho após o pedido
        cart.getItems().clear();
        cartRepository.save(cart);
        logger.info("Carrinho ID {} limpo com sucesso.", cartId);

        return savedOrder;
    }
}
