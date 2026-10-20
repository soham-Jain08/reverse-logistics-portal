package com.soham.reverselogistics.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Login, logout and URL protection.
 * Users are kept in memory with BCrypt-hashed passwords (demo accounts).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/error", "/css/**", "/js/**", "/images/**",
                        "/favicon.ico", "/actuator/health", "/actuator/health/**").permitAll()
                .requestMatchers("/returns/new").hasRole("CUSTOMER")
                .requestMatchers(HttpMethod.POST, "/returns").hasRole("CUSTOMER")
                .requestMatchers("/returns/export").hasAnyRole("MANAGER", "WAREHOUSE")
                .anyRequest().authenticated())
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/returns", true)
                .failureUrl("/login?error")
                .permitAll())
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails customer1 = User.withUsername("customer1")
                .password(encoder.encode("Customer@123")).roles("CUSTOMER").build();
        UserDetails customer2 = User.withUsername("customer2")
                .password(encoder.encode("Customer@123")).roles("CUSTOMER").build();
        UserDetails manager = User.withUsername("manager1")
                .password(encoder.encode("Manager@123")).roles("MANAGER").build();
        UserDetails warehouse = User.withUsername("warehouse1")
                .password(encoder.encode("Warehouse@123")).roles("WAREHOUSE").build();
        return new InMemoryUserDetailsManager(customer1, customer2, manager, warehouse);
    }
}
