package Musicfy.MusicfyOrigin.Product.Service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import Musicfy.MusicfyOrigin.Product.dto.ItemCarrinhoDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StripeService {

    @Value("${stripe.secret.key}")  // Corrigido para usar stripe.secret.key
    private String stripeApiKey;

    @PostConstruct
    public void init() {
        // Verificação de segurança da chave
        if (stripeApiKey == null || stripeApiKey.trim().isEmpty()) {
            throw new IllegalStateException("""
                #########################################################
                ERRO: Chave Stripe não configurada!
                Por favor, defina no application.properties:
                stripe.secret.key=sua_chave_stripe_aqui
                #########################################################""");
        }

        // Configuração do Stripe com log de verificação
        System.out.println("Configurando Stripe com chave: ***" +
                stripeApiKey.substring(stripeApiKey.length() - 4));
        Stripe.apiKey = stripeApiKey;
    }

    public Session createCheckoutSession(List<ItemCarrinhoDTO> items) throws StripeException {
        try {
            String successUrl = "http://localhost:5173/success";
            String cancelUrl = "http://localhost:5173/cancel";

            List<SessionCreateParams.LineItem> lineItems = items.stream()
                    .map(item -> {
                        // Validação dos itens
                        if (item.getPrecoUnitario() <= 0 || item.getQuantidade() <= 0) {
                            throw new IllegalArgumentException("Preço ou quantidade inválidos para o item: " + item.getNomeProduto());
                        }

                        return SessionCreateParams.LineItem.builder()
                                .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                        .setCurrency("brl")
                                        .setUnitAmount((long) (item.getPrecoUnitario() * 100)) // Converte para centavos
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
                    .build();
            //ola ajudazzjjzjz
//ola
            return Session.create(params);

        } catch (StripeException e) {
            System.err.println("ERRO STRIPE - Status: " + e.getStatusCode());
            System.err.println("Tipo: " + e.getStripeError().getType());
            System.err.println("Código: " + e.getStripeError().getCode());
            System.err.println("Mensagem: " + e.getStripeError().getMessage());
            throw new RuntimeException("Falha ao criar sessão de pagamento", e);
            //ola
        }
    }
}