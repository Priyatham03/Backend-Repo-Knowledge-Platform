package com.knowledge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            // FIX: use cors().configurationSource() from the CorsConfig bean,
            // NOT cors.disable() — disabling cors blocks preflight OPTIONS entirely
            .cors(cors -> cors.configure(http))
            .authorizeHttpRequests(authz -> authz
                // NOTE: context-path (/api) is NOT included in these matchers.
                // Spring Security sees paths AFTER the context-path.
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/articles").permitAll()
                .requestMatchers("/articles/**").permitAll()
                .requestMatchers("/ai/**").permitAll()   // ← ADD THIS for the AI proxy
                .anyRequest().permitAll()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}