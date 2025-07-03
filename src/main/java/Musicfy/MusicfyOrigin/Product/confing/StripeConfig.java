package Musicfy.MusicfyOrigin.Product.confing;

import com.stripe.Stripe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class StripeConfig {

    @Value("${stripe.secret.key}")
    private String stripeKey;

    @PostConstruct
    public void init() {
        System.out.println("=== VERIFICAÇÃO DA CHAVE STRIPE ===");
        System.out.println("Valor da chave: " +
                (stripeKey != null ? "***" + stripeKey.substring(stripeKey.length() - 4) : "NULO"));

        if (stripeKey == null || stripeKey.trim().isEmpty()) {
            System.err.println("""
                ########################################################
                ERRO CRÍTICO: Chave Stripe não configurada!
                Verifique se no application.properties existe:
                stripe.secret.key=sua_chave_aqui
                Local atual do properties: src/main/resources/application.properties
                ########################################################""");
            throw new RuntimeException("Configuração do Stripe faltando: stripe.secret.key");
        }

        try {
            Stripe.apiKey = stripeKey;
            System.out.println("✅ Stripe configurado com sucesso!");
        } catch (Exception e) {
            System.err.println("❌ Erro ao configurar Stripe: " + e.getMessage());
            throw new RuntimeException("Falha na configuração do Stripe", e);
        }
    }
}

