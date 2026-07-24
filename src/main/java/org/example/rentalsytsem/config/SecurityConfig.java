package org.example.rentalsytsem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf.disable()) // Disable CSRF protection
      .authorizeHttpRequests(auth -> auth
        .anyRequest().permitAll() // Allow ALL requests without authentication
      )
      .formLogin(form -> form.disable()) // Disable form login
      .logout(logout -> logout.disable()) // Disable logout
      .httpBasic(httpBasic -> httpBasic.disable()); // Disable HTTP Basic
      // Keep session management enabled for custom authentication
    return http.build();
  }
}
