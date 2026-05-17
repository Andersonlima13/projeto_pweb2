package com.projetocorridas.projetocorridas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class LobbyController {

    @GetMapping("/")
    public String index(HttpServletRequest request) {
        if (request.getSession(false) == null) {
            return "redirect:/auth/login";
        }

        Object usuarioLogado = request.getSession(false).getAttribute("usuarioLogado");
        if (usuarioLogado == null) {
            return "redirect:/auth/login";
        }

        return "redirect:/lobby";
    }

    @GetMapping("/lobby")
    public String lobby() {
        return "lobby";
    }
}