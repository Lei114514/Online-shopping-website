package com.onlineshop.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * 安全配置類
 * 配置Spring Security相關設置
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    /**
     * 配置密碼編碼器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * 配置安全過濾鏈
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                // 公開訪問的URL
                .requestMatchers(
                    "/", "/home", "/products", "/products/**", "/register", "/login", "/css/**", "/js/**", "/images/**"
                ).permitAll()
                
                // 顧客角色可訪問的URL
                .requestMatchers(
                    "/cart/**", "/orders/**", "/profile/**"
                ).hasRole("CUSTOMER")
                
                // 銷售角色可訪問的URL
                .requestMatchers(
                    "/sales/**", "/admin/products/**", "/admin/orders/**"
                ).hasRole("SALES")
                
                // 管理員角色可訪問的URL
                .requestMatchers(
                    "/admin/**", "/users/**"
                ).hasRole("ADMIN")
                
                // 其他請求需要認證
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/home", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
            )
            .exceptionHandling(exception -> exception
                .accessDeniedPage("/access-denied")
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**") // API端點禁用CSRF
            );
        
        return http.build();
    }
    
    /**
     * 配置認證管理器
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder());
    }
}
