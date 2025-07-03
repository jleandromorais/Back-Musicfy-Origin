package Musicfy.MusicfyOrigin.Product.Controller;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import Musicfy.MusicfyOrigin.Product.Service.StripeService;
import Musicfy.MusicfyOrigin.Product.dto.CheckoutRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/checkout")
@CrossOrigin(origins = "http://localhost:5173")
public class CheckoutController {

    private final StripeService stripeService;

    public CheckoutController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping("/create-session")
    public ResponseEntity<?> createSession(@RequestBody CheckoutRequestDTO checkoutRequest) {
        try {
            Session session = stripeService.createCheckoutSession(checkoutRequest.getItems());
            Map<String, String> response = new HashMap<>();
            response.put("url", session.getUrl());
            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Falha ao criar sessão Stripe");
            errorResponse.put("details", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}