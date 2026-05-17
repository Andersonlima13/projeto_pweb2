package com.projetocorridas.projetocorridas.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.projetocorridas.projetocorridas.dto.UsuarioAutenticadoDto;
import com.projetocorridas.projetocorridas.service.JwtService;

@Component
public class JwtCookieFilter extends OncePerRequestFilter {

    private static final String COOKIE_NAME = "PROJETO_CORRIDAS_AUTH";

    private final JwtService jwtService;

    public JwtCookieFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestPath = request.getRequestURI().substring(request.getContextPath().length());

        if (isPublicPath(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        Optional<String> jwtCookie = getCookieValue(request, COOKIE_NAME);
        if (jwtCookie.isEmpty()) {
            redirectToLogin(request, response);
            return;
        }

        Optional<UsuarioAutenticadoDto> usuario = jwtService.validarToken(jwtCookie.get());
        if (usuario.isEmpty()) {
            clearCookie(response, request);
            redirectToLogin(request, response);
            return;
        }

        request.setAttribute("usuarioLogado", usuario.get());
        request.getSession(true).setAttribute("usuarioLogado", usuario.get());

        filterChain.doFilter(request, response);
    }

    private boolean isPublicPath(String requestPath) {
        return requestPath.startsWith("/auth/")
                || requestPath.startsWith("/css/")
                || requestPath.startsWith("/js/")
                || requestPath.startsWith("/images/")
                || requestPath.startsWith("/webjars/")
                || requestPath.equals("/error");
    }

    private Optional<String> getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }

        return Arrays.stream(cookies)
                .filter(cookie -> cookieName.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

    private void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(request.getContextPath() + "/auth/login");
    }

    private void clearCookie(HttpServletResponse response, HttpServletRequest request) {
        Cookie cookie = new Cookie(COOKIE_NAME, "");
        cookie.setHttpOnly(true);
        cookie.setPath(request.getContextPath().isEmpty() ? "/" : request.getContextPath());
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}