package Musicfy.MusicfyOrigin.Product.Controller;

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

@RestController
@RequestMapping("/api/checkout")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "https://musicfy-two.vercel.app",
        "https://musicfy-558s99apl-jleandromorais-projects.vercel.app"
})
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
                        .body(Map.of("error", "Faltando cartId, userId ou enderecoId na requisição."));
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
                    .body(Map.of("error", "Erro ao criar sessão Stripe", "details", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erro interno ao processar o checkout", "details", e.getMessage()));
        }
    }


    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) {
        System.out.println("🚀 Webhook recebido! Payload tamanho: " + payload.length());

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, stripeWebhookSecret);
            System.out.println("✅ Webhook validado, tipo do evento: " + event.getType());
        } catch (SignatureVerificationException e) {
            System.err.println("❌ Assinatura inválida: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Assinatura inválida");
        }

        if ("checkout.session.completed".equals(event.getType())) {
            EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
            Session session;
            try {
                session = (Session) deserializer.deserializeUnsafe();
            } catch (Exception e) {
                System.err.println("❌ Erro ao deserializar Session: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao deserializar Session.");
            }

            if (session == null) {
                System.err.println("❌ Session é nula");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Session nula.");
            }

            System.out.println("📦 Processando pedido da sessão: " + session.getId());

            try {
                stripeService.fulfillOrder(session);
                System.out.println("✅ Pedido processado com sucesso!");
            } catch (Exception e) {
                System.err.println("❌ Erro ao salvar pedido: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao salvar pedido.");
            }
        } else {
            System.out.println("ℹ️ Evento ignorado: " + event.getType());
        }

        return ResponseEntity.ok("Webhook recebido com sucesso.");
    }
}