package com.onlineshop.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

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
                // 公開訪問的URL - 包含所有靜態資源
                .requestMatchers(
                    "/", "/home", "/products", "/products/**", "/register", "/login",
                    "/css/**", "/js/**", "/images/**", "/fonts/**", "/vendor/**", "/static/**", "/api/images/**",
                    "/favicon.ico", "/error"
                ).permitAll()
                
                // 郵件確認收貨連結 - 公開訪問
                .requestMatchers("/orders/confirm/**").permitAll()
                
                // 帳號注銷 - 所有已登錄用戶都可訪問
                .requestMatchers("/users/deactivate").authenticated()
                
                // 顧客角色可訪問的URL
                .requestMatchers(
                    "/cart/**", "/orders/**", "/profile/**"
                ).hasAnyRole("CUSTOMER", "SALES")
                
                // 商家/銷售角色可訪問的URL
                .requestMatchers(
                    "/merchant/**"
                ).hasAnyRole("SALES", "ADMIN")
                
                // 管理員角色可訪問的URL（除了注銷端點）
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
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .exceptionHandling(exception -> exception
                .accessDeniedPage("/access-denied")
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**", "/images/**") // API端點和圖片請求禁用CSRF
            )
            .headers(headers -> headers
                // 配置內容安全策略（僅使用本地資源）
                .contentSecurityPolicy(csp -> csp
                    .policyDirectives("default-src 'self'; " +
                        "script-src 'self' 'unsafe-inline'; " +
                        "style-src 'self' 'unsafe-inline'; " +
                        "font-src 'self' data:; " +
                        "img-src 'self' data:; " +
                        "connect-src 'self'")
                )
                // 允許在 iframe 中顯示（如果需要）
                .frameOptions(frame -> frame.sameOrigin())
                // 配置 Referrer Policy
                .referrerPolicy(referrer -> referrer
                    .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                )
            );
        
        return http.build();
    }
}
