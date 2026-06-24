package com.projetocorridas.projetocorridas.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.projetocorridas.projetocorridas.security.AdminAccessInterceptor;
import com.projetocorridas.projetocorridas.security.AuthRequiredInterceptor;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AdminAccessInterceptor adminAccessInterceptor;

    @Autowired
    private AuthRequiredInterceptor authRequiredInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(adminAccessInterceptor)
                .addPathPatterns(
                        "/lobby",
                        "/corridas",
                        "/corridas/**");

        registry.addInterceptor(authRequiredInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/auth/**",
                        "/lobby",
                        "/corridas", "/corridas/**");
    }
}