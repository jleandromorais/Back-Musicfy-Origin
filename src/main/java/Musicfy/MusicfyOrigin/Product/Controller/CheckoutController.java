package Musicfy.MusicfyOrigin.Product.Controller;

import com.stripe.exception.EventDataObjectDeserializationException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
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
@CrossOrigin(origins = "http://localhost:5173")
public class CheckoutController {

    private final StripeService stripeService;

    @Value("${stripe.webhook.secret}") // Adicione isso ao seu application.properties
    private String stripeWebhookSecret;

    public CheckoutController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping("/create-session")
    public ResponseEntity<?> createSession(@RequestBody CheckoutRequestDTO checkoutRequest) {
        try {
            // Garante que cartId, userId e enderecoId sejam fornecidos na requisição
            if (checkoutRequest.getCartId() == null || checkoutRequest.getUserId() == null || checkoutRequest.getEnderecoId() == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Faltando cartId, userId, ou enderecoId na requisição."));
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
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Falha ao criar sessão Stripe");
            errorResponse.put("details", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erro interno ao processar checkout");
            errorResponse.put("details", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Endpoint de Webhook para lidar com eventos do Stripe
    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) throws EventDataObjectDeserializationException {
        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, stripeWebhookSecret);
        } catch (SignatureVerificationException e) {
            // Assinatura inválida
            System.err.println("Erro do Webhook: Assinatura Stripe inválida: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }

        // Lida com o evento
        switch (event.getType()) {
            case "checkout.session.completed":
                Session session = (Session) event.getDataObjectDeserializer().deserializeUnsafe();
                System.out.println("Sessão de Checkout Concluída para o ID da sessão: " + session.getId());
                try {
                    stripeService.fulfillOrder(session.getId());
                    System.out.println("Pedido concluído com sucesso para o ID da sessão: " + session.getId());
                } catch (StripeException e) {
                    System.err.println("Erro ao concluir o pedido para o ID da sessão " + session.getId() + ": " + e.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fulfilling order");
                }
                break;
            // Adicione outros tipos de evento conforme necessário
            default:
                System.out.println("Tipo de evento não tratado: " + event.getType());
                break;
        }

        return ResponseEntity.ok("Received");
    }
}