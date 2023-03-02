package com.shopme.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
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
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers(
                 "/cart","/address-book/**",
                            "/check-out", "/place-order-cod",
                            "/orders/**").authenticated();
        http.authorizeRequests().anyRequest().permitAll();

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
