package com.example.transaction_service.config.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.transaction_service.filter.JwtFilter;

@Configuration
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/transactions/**").permitAll() // ✅ Add this line
                .anyRequest().authenticated()
            )
            // Remove or comment out any JWT filter temporarily
            // .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
            ;
        
        return http.build();
    }
}


