package com.projetocorridas.projetocorridas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LobbyController {

    @GetMapping("/")
    public String index() {
        return "redirect:/auth/login";
    }

    @GetMapping("/lobby")
    public String lobby() {
        return "lobby";
    }
}