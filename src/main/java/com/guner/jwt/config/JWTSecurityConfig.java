package com.guner.jwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class JWTSecurityConfig {

    /*
    https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html#oauth2resourceserver-jwt-sansboot
    Overriding or Replacing Boot Auto Configuration
    There are two @Beans that Spring Boot generates on Resource Server’s behalf.

    The first is a SecurityFilterChain that configures the app as a resource server.
    When including spring-security-oauth2-jose, this SecurityFilterChain looks like:
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(authorize -> authorize
            .anyRequest().authenticated()
        )
        .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()));
    return http.build();
    }
    If the application doesn’t expose a SecurityFilterChain bean, then Spring Boot will expose the above default one.
    */

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(
                authorize -> authorize
                        .requestMatchers("/foos/*").hasAuthority("SCOPE_read")
                        .requestMatchers("/foos").hasAuthority("SCOPE_write")
                        .requestMatchers("/authorized/**").permitAll()
                        .anyRequest().authenticated())
                //.csrf(AbstractHttpConfigurer::disable)

                // default kullanım.
                //.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
                // authorization için bu kod ve yamlda bu tanım gerekli : spring.security.oauth2.resource-server.jwt.issuer-uri: http://localhost:8000
                // veya JwtDecoderBean tanımlı olmalı

                // jwks url ile kullanım
                // yamlda hiç tanıma gerek yok. jwks url'inden bilgiyi alıyor
                // https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html#oauth2resourceserver-jwt-decoder-dsl

                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwkSetUri("http://localhost:8000/oauth2/jwks")
                        ))


                // custom decoder kullanımı
                /*
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(myCustomDecoder())
                        )
                );
                */
                ;
        return http.build();
    }

    // .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults())); ile beraber kullanılır.
    /*
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withIssuerLocation("http://localhost:8000").build();
    }
     */

    // .oauth2ResourceServer...... .decoder(myCustomDecoder() ile beraber kullanılır
    /*
    @Bean
    public JwtDecoder myCustomDecoder() {
        return NimbusJwtDecoder.withIssuerLocation("http://localhost:8000").build();
    }

     */
}