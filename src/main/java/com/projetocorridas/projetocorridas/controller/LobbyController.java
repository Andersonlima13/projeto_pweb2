package com.projetocorridas.projetocorridas.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;

import com.projetocorridas.projetocorridas.dto.CorridaDto;
import com.projetocorridas.projetocorridas.dto.ParticipanteDto;
import com.projetocorridas.projetocorridas.dto.UsuarioAutenticadoDto;
import com.projetocorridas.projetocorridas.service.CorridaService;
import com.projetocorridas.projetocorridas.service.ParticipanteService;

@Controller
public class LobbyController {

    private final ParticipanteService participanteService;
    private final CorridaService corridaService;

    public LobbyController(ParticipanteService participanteService, CorridaService corridaService) {
        this.participanteService = participanteService;
        this.corridaService = corridaService;
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

    @GetMapping("/lobby/participante/corridas")
    public String corridasParticipante(HttpServletRequest request, Model model) {
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
        List<CorridaDto> corridas = new ArrayList<>();

        if (participante.getCorridaId() != null) {
            corridas.add(corridaService.obter(participante.getCorridaId()));
        }

        model.addAttribute("participante", participante);
        model.addAttribute("corridas", corridas);
        return "lobby/participante-corridas";
    }
}