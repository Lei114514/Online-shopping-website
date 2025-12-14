package com.onlineshop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 * 配置靜態資源處理
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 配置靜態資源處理器
     * 確保所有靜態資源都能正確訪問
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 注意：/images/products/** 由 ImageController 處理，不在這裡配置
        
        // 配置 CSS 資源
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/")
                .setCachePeriod(3600);
        
        // 配置 JavaScript 資源
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/")
                .setCachePeriod(3600);
        
        // 配置字體資源（如果有）
        registry.addResourceHandler("/fonts/**")
                .addResourceLocations("classpath:/static/fonts/")
                .setCachePeriod(3600);
        
        // 配置 vendor 資源（Bootstrap, Font Awesome 等）
        registry.addResourceHandler("/vendor/**")
                .addResourceLocations("classpath:/static/vendor/")
                .setCachePeriod(86400); // 24小時緩存
        
        // 配置其他靜態資源
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600);
    }
}