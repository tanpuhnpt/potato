package com.ktpm.potatoapi.common.config.security;

import com.ktpm.potatoapi.user.service.AuthService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SecurityConfig {
    AuthService authService;
    PasswordEncoder passwordEncoder;
    JwtFilter jwtFilter;
    CustomAuthEntryPoint customAuthEntryPoint;
    CustomAccessDeniedHandler customAccessDeniedHandler;
    String[] OPENAPI_ENDPOINTS = {"/v3/api-docs/**", "/swagger-ui/**"};
    String[] PUBLIC_ENDPOINTS = {
            "/auth/**",
            "/merchant/register", "/merchant/upload-transaction-proof",
            "/cuisine-types", "/merchant/cuisine-types",
            "/merchants/{merchantId}/categories", "/merchants", "/merchants/{id}",
            "/menu-items/{menuItemId}", "/merchants/{merchantId}/menu-items",
            "/merchants", "/merchants/{id}",
            "/admin/drones/{id}/update-location", "/admin/drones/{id}"
    };
    String[] CUSTOMER_ENDPOINTS = {
            "/cart",
            "/check-out", "/my-orders", "/orders/{orderId}",
            "/rating"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(OPENAPI_ENDPOINTS).permitAll()
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers(CUSTOMER_ENDPOINTS).hasAuthority("CUSTOMER")
                        .requestMatchers("/merchant/**", "/account/change-password").hasAuthority("MERCHANT_ADMIN")
                        .requestMatchers("/admin/**").hasAuthority("SYSTEM_ADMIN")
                        .anyRequest().authenticated()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(customAuthEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    // Provider to authenticate users by using userService
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(authService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
}
