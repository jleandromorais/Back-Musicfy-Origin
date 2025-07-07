package Musicfy.MusicfyOrigin.Product.Service;

import Musicfy.MusicfyOrigin.Product.model.Endereco;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import Musicfy.MusicfyOrigin.Product.dto.ItemCarrinhoDTO;
import Musicfy.MusicfyOrigin.Product.model.*; // Importa todos os modelos
import Musicfy.MusicfyOrigin.Product.repository.*; // Importa todos os repositórios
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StripeService {

    @Value("${stripe.secret.key}")
    private String stripeApiKey;

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UsuarioRepository usuarioRepository; // Assumindo que você tem isso para encontrar usuários
    private final EnderecoRepository enderecoRepository; // Assumindo que você tem isso para encontrar endereços

    public StripeService(CartRepository cartRepository, ProductRepository productRepository,
                         OrderRepository orderRepository, OrderItemRepository orderItemRepository,
                         UsuarioRepository usuarioRepository, EnderecoRepository enderecoRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.usuarioRepository = usuarioRepository;
        this.enderecoRepository = enderecoRepository;
    }

    @PostConstruct
    public void init() {
        if (stripeApiKey == null || stripeApiKey.trim().isEmpty()) {
            throw new IllegalStateException("""
                #########################################################
                ERRO: Chave Stripe não configurada!
                Por favor, defina no application.properties:
                stripe.secret.key=sua_chave_stripe_aqui
                #########################################################""");
        }

        System.out.println("Configurando Stripe com chave: ***" +
                stripeApiKey.substring(stripeApiKey.length() - 4));
        Stripe.apiKey = stripeApiKey;
    }

    @Transactional
    public Session createCheckoutSession(Long cartId, Long userId, Long enderecoId, List<ItemCarrinhoDTO> items) throws StripeException {
        try {
            String successUrl = "http://localhost:5173/success?session_id={CHECKOUT_SESSION_ID}";
            String cancelUrl = "http://localhost:5173/cancel";

            List<SessionCreateParams.LineItem> lineItems = items.stream()
                    .map(item -> {
                        if (item.getPrecoUnitario() <= 0 || item.getQuantidade() <= 0) {
                            throw new IllegalArgumentException("Preço ou quantidade inválidos para o item: " + item.getNomeProduto());
                        }

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
                    })
                    .collect(Collectors.toList());

            SessionCreateParams params = SessionCreateParams.builder()
                    .addAllLineItem(lineItems)
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(successUrl)
                    .setCancelUrl(cancelUrl)
                    // Passa metadados personalizados para a sessão para recuperá-los mais tarde
                    .putMetadata("cartId", String.valueOf(cartId))
                    .putMetadata("userId", String.valueOf(userId))
                    .putMetadata("enderecoId", String.valueOf(enderecoId))
                    .build();

            return Session.create(params);

        } catch (StripeException e) {
            System.err.println("ERRO STRIPE - Status: " + e.getStatusCode());
            System.err.println("Tipo: " + e.getStripeError().getType());
            System.err.println("Código: " + e.getStripeError().getCode());
            System.err.println("Mensagem: " + e.getStripeError().getMessage());
            throw new RuntimeException("Falha ao criar sessão de pagamento", e);
        }
    }

    @Transactional
    public Order fulfillOrder(String sessionId) throws StripeException {
        Session session = Session.retrieve(sessionId);

        // Recupera os metadados passados durante a criação da sessão
        Long cartId = Long.valueOf(session.getMetadata().get("cartId"));
        Long userId = Long.valueOf(session.getMetadata().get("userId"));
        Long enderecoId = Long.valueOf(session.getMetadata().get("enderecoId"));

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Carrinho não encontrado para o ID: " + cartId));
        Usuario user = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado para o ID: " + userId));
        Endereco endereco = enderecoRepository.findById(enderecoId)
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado para o ID: " + enderecoId));

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING); // Status inicial
        order.setDeliveryAddress(endereco);

        double totalOrderPrice = 0.0;

        for (CartItem cartItem : cart.getItems()) {
            Product product = productRepository.findById(cartItem.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado para o item do carrinho: " + cartItem.getProduct().getId()));

            OrderItem orderItem = new OrderItem(
                    order,
                    product,
                    cartItem.getQuantity(),
                    product.getPrice() // Usa o preço atual do produto como preço unitário na compra
            );
            order.addOrderItem(orderItem);
            totalOrderPrice += (product.getPrice() * cartItem.getQuantity());
        }
        order.setTotalPrice(totalOrderPrice);

        Order savedOrder = orderRepository.save(order);

        // Limpa o carrinho após a conclusão do pedido
        cart.getItems().clear();
        cartRepository.save(cart);

        return savedOrder;
    }
}