package Musicfy.MusicfyOrigin.Product.Controller;

import com.stripe.exception.EventDataObjectDeserializationException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import Musicfy.MusicfyOrigin.Product.Service.StripeService;
import Musicfy.MusicfyOrigin.Product.dto.CheckoutRequestDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static Musicfy.MusicfyOrigin.Product.Service.StripeService.logger;

@RestController
@RequestMapping("/api/checkout")
@CrossOrigin(origins = "http://localhost:5173") // Ajuste conforme sua origem de front-end
public class CheckoutController {

    private final StripeService stripeService;

    @Value("${stripe.webhook.secret}")
    private String stripeWebhookSecret;

    public CheckoutController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping("/create-session")
    public ResponseEntity<?> createSession(@RequestBody CheckoutRequestDTO checkoutRequest) {
        try {
            if (checkoutRequest.getCartId() == null || checkoutRequest.getUserId() == null || checkoutRequest.getEnderecoId() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Faltando cartId, userId, ou enderecoId na requisição."));
            }

            Session session = stripeService.createCheckoutSession(
                    checkoutRequest.getCartId(),
                    checkoutRequest.getUserId(),
                    checkoutRequest.getEnderecoId(),
                    checkoutRequest.getItems()
            );

            Map<String, String> response = new HashMap<>();
            response.put("url", session.getUrl());
            return ResponseEntity.ok(response);

        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Falha ao criar sessão Stripe",
                            "details", e.getMessage()
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Erro interno ao processar checkout",
                            "details", e.getMessage()
                    ));
        }
    }


    // Este é o único método que você vai substituir no arquivo CheckoutController.java
    // Cole este código de volta no seu CheckoutController.java
    // No ficheiro: CheckoutController.java

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        Event event;
        String endpointSecret = this.stripeWebhookSecret;

        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
            System.out.println("Webhook do Stripe recebido e validado! Tipo: " + event.getType());
        } catch (SignatureVerificationException e) {
            System.err.println("!!! ERRO: Falha na verificação da assinatura do Webhook!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Assinatura inválida");
        }

        // Lida com o evento 'checkout.session.completed'
        if ("checkout.session.completed".equals(event.getType())) {
            Session session;
            EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();

            // **ESTA É A CORREÇÃO** - Usamos um método mais direto para obter o objeto
            try {
                session = (Session) dataObjectDeserializer.deserializeUnsafe();
            } catch (Exception e) {
                System.err.println("!!! ERRO CRÍTICO ao deserializar o objeto Session: " + e.getMessage());
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Falha ao processar o objeto da sessão.");
            }

            if (session == null) {
                System.err.println("!!! ERRO CRÍTICO: O objeto Session é nulo.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Objeto da sessão nulo.");
            }

            System.out.println(">>> Processando pedido para a Sessão Stripe ID: " + session.getId());
            try {
                // Chama o serviço para criar o pedido no banco de dados
                stripeService.fulfillOrder(session);
                System.out.println("✅ SUCESSO: Pedido processado e salvo no banco de dados!");
            } catch (Exception e) {
                System.err.println("!!! ERRO CRÍTICO ao salvar o pedido no banco de dados: " + e.getMessage());
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno ao salvar pedido.");
            }
        } else {
            // Ignora outros tipos de eventos que não nos interessam para este fluxo
            System.out.println("Evento recebido mas não processado: " + event.getType());
        }

        return ResponseEntity.ok("Recebido");
    }
    }