package com.projetocorridas.projetocorridas.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.projetocorridas.projetocorridas.dto.UsuarioAutenticadoDto;

@Component
public class AdminAccessInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String requestPath = request.getRequestURI().substring(request.getContextPath().length());

        if (isPublicPath(requestPath) || isLobbyPath(requestPath)) {
            return true;
        }

        UsuarioAutenticadoDto usuario = obterUsuarioLogado(request);
        if (usuario != null && usuario.isAdmin()) {
            return true;
        }

        FlashMap flashMap = RequestContextUtils.getOutputFlashMap(request);
        flashMap.put("erro", "Acesso restrito a administradores.");
        flashMap.put("restricao", "Somente administradores podem acessar páginas e operações de cadastro.");
        flashMap.setTargetRequestPath(request.getContextPath() + "/lobby");

        RequestContextUtils.getFlashMapManager(request).saveOutputFlashMap(flashMap, request, response);
        response.sendRedirect(request.getContextPath() + "/lobby");
        return false;
    }

    private UsuarioAutenticadoDto obterUsuarioLogado(HttpServletRequest request) {
        Object usuario = request.getAttribute("usuarioLogado");
        if (usuario instanceof UsuarioAutenticadoDto autenticado) {
            return autenticado;
        }

        Object sessionUsuario = request.getSession(false) == null ? null
                : request.getSession(false).getAttribute("usuarioLogado");
        if (sessionUsuario instanceof UsuarioAutenticadoDto autenticado) {
            return autenticado;
        }

        return null;
    }

    private boolean isPublicPath(String requestPath) {
        return requestPath.startsWith("/auth/")
                || requestPath.startsWith("/css/")
                || requestPath.startsWith("/js/")
                || requestPath.startsWith("/images/")
                || requestPath.startsWith("/webjars/")
                || requestPath.equals("/error");
    }

    private boolean isLobbyPath(String requestPath) {
        return "/lobby".equals(requestPath)
                || "/lobby/participante".equals(requestPath)
                || "/lobby/participante/corridas".equals(requestPath)
                || "/".equals(requestPath);
    }
}