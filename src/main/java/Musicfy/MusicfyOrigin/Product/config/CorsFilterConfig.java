package Musicfy.MusicfyOrigin.Product.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsFilterConfig {
//ssoa
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Configurações específicas
        config.setAllowCredentials(true);

        // Domínios permitidos
        config.addAllowedOrigin("http://localhost:5173"); // Para desenvolvimento local
        config.addAllowedOrigin("https://musicfy-two.vercel.app"); // Seu domínio principal
        config.addAllowedOrigin("https://musicfy-f5p3mu52u-jleandromorais-projects.vercel.app"); // Outro domínio de deploy

        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        source.registerCorsConfiguration("/**", config);

        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }
}