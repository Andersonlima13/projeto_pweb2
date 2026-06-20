package com.projetocorridas.projetocorridas.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class SessaoInterceptor
        implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler)
            throws Exception {

        HttpSession session = request.getSession(false);

        if (session == null ||
                session.getAttribute("nomeUsuario") == null) {

            response.sendRedirect("/auth/login");
            return false;
        }

        return true;
    }
}