package com.projetocorridas.projetocorridas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.projetocorridas.projetocorridas.dto.ParticipanteDto;
import com.projetocorridas.projetocorridas.model.Participante;
import com.projetocorridas.projetocorridas.service.AuthService;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("participanteDto", new ParticipanteDto());
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("participanteDto") ParticipanteDto dto, HttpSession session,
            RedirectAttributes redirectAttributes) {
        Optional<Participante> participante = authService.autenticar(dto);
        if (participante.isPresent()) {
            session.setAttribute("usuarioLogado", participante.get());
            return "redirect:/lobby";
        }
        redirectAttributes.addFlashAttribute("erro", "Nome ou senha inválidos");
        return "redirect:/auth/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/auth/login";
    }
}