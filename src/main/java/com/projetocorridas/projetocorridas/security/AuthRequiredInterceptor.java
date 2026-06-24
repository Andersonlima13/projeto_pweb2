package com.projetocorridas.projetocorridas.security;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.projetocorridas.projetocorridas.dto.UsuarioAutenticadoDto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthRequiredInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        UsuarioAutenticadoDto usuario = obterUsuarioLogado(request);
        if (usuario != null) {
            return true;
        }

        negarAcesso(request, response, "Acesso restrito a usuários autenticados",
                "Faça login para continuar");
        return false;
    }

    private UsuarioAutenticadoDto obterUsuarioLogado(HttpServletRequest request) {
        Object sessionUsuario = request.getSession(false) == null ? null
                : request.getSession(false).getAttribute("usuarioLogado");
        if (sessionUsuario instanceof UsuarioAutenticadoDto autenticado) {
            return autenticado;
        }
        return null;
    }

    private void negarAcesso(HttpServletRequest request, HttpServletResponse response,
            String erro, String restricao) throws Exception {
        FlashMap flashMap = new FlashMap();
        flashMap.put("erro", erro);
        flashMap.put("restricao", restricao);
        flashMap.setTargetRequestPath(request.getContextPath() + "/auth/login");

        RequestContextUtils.getFlashMapManager(request).saveOutputFlashMap(flashMap, request, response);
        response.sendRedirect(request.getContextPath() + "/auth/login");
    }
}