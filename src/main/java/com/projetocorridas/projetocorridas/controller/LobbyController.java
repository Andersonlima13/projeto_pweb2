package com.projetocorridas.projetocorridas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;

import java.util.UUID;

import com.projetocorridas.projetocorridas.dto.ParticipanteDto;
import com.projetocorridas.projetocorridas.dto.UsuarioAutenticadoDto;
import com.projetocorridas.projetocorridas.service.ParticipanteService;

@Controller
public class LobbyController {

    private final ParticipanteService participanteService;

    public LobbyController(ParticipanteService participanteService) {
        this.participanteService = participanteService;
    }

    @GetMapping("/")
    public String index(HttpServletRequest request) {
        if (request.getSession(false) == null) {
            return "redirect:/auth/login";
        }

        Object usuarioLogado = request.getSession(false).getAttribute("usuarioLogado");
        if (usuarioLogado == null) {
            return "redirect:/auth/login";
        }

        if (usuarioLogado instanceof UsuarioAutenticadoDto autenticado && !autenticado.isAdmin()) {
            return "redirect:/lobby/participante";
        }

        return "redirect:/lobby";
    }

    @GetMapping("/lobby")
    public String lobby() {
        return "lobby";
    }

    @GetMapping("/lobby/participante")
    public String lobbyParticipante(HttpServletRequest request, Model model) {
        Object sessionUsuario = request.getSession(false) == null ? null
                : request.getSession(false).getAttribute("usuarioLogado");

        if (!(sessionUsuario instanceof UsuarioAutenticadoDto autenticado)) {
            return "redirect:/auth/login";
        }

        if (autenticado.isAdmin()) {
            return "redirect:/lobby";
        }

        UUID participanteId = UUID.fromString(autenticado.getId());
        ParticipanteDto participante = participanteService.obter(participanteId);
        model.addAttribute("participante", participante);
        return "lobby/participante";
    }
}