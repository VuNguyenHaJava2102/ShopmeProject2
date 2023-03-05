package com.shopme.admin.security;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/users/**", "/settings/**", "/countries/**", "/states/**").hasAuthority("Admin");
        http.authorizeRequests().antMatchers("/categories/**").hasAnyAuthority("Admin", "Editor");
        http.authorizeRequests().antMatchers("/brands/**").hasAnyAuthority("Admin", "Editor");
        http.authorizeRequests().antMatchers("/products", "/products/page/**", "/products/details/**").hasAnyAuthority("Admin", "Editor", "Salesperson", "Shipper");
        http.authorizeRequests().antMatchers("/products/edit/**", "/products/save").hasAnyAuthority("Admin", "Editor", "Salesperson");
        http.authorizeRequests().antMatchers("/products/**").hasAnyAuthority("Admin", "Editor");
        http.authorizeRequests().antMatchers("/shipping-rates/**").hasAnyAuthority("Admin", "Salesperson");
        http.authorizeRequests().antMatchers("/orders", "/orders/page/**", "/orders/details/**").hasAnyAuthority("Admin", "Salesperson", "Shipper");
        http.authorizeRequests().antMatchers("/orders-shipper/**").hasAuthority("Shipper");
        http.authorizeRequests().antMatchers("/orders/**").hasAnyAuthority("Admin", "Salesperson");
        http.authorizeRequests().antMatchers("/reports/**").hasAnyAuthority("Admin", "Salesperson");
        http.authorizeRequests().anyRequest().authenticated();

        http.formLogin().loginPage("/login").usernameParameter("email").permitAll();
        http.logout().permitAll();
        http.rememberMe().key("realjava");

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() throws Exception{
        return web -> web.ignoring().antMatchers("/images/**", "/webjars/**", "/js/**");
    }

}
