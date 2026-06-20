package com.projetocorridas.projetocorridas.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.projetocorridas.projetocorridas.interceptor.SessaoInterceptor;

@Configuration
public class WebConfig
        implements WebMvcConfigurer {

    private final SessaoInterceptor interceptor;

    public WebConfig(
            SessaoInterceptor interceptor) {

        this.interceptor = interceptor;
    }

    @Override
    public void addInterceptors(
            InterceptorRegistry registry) {

        registry.addInterceptor(interceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/auth/**",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/webjars/**");
    }
}