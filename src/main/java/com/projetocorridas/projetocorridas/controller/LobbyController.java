package com.projetocorridas.projetocorridas.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;

import com.projetocorridas.projetocorridas.dto.CorridaDto;
import com.projetocorridas.projetocorridas.dto.AlternativaDto;
import com.projetocorridas.projetocorridas.dto.PerguntaDto;
import com.projetocorridas.projetocorridas.dto.ParticipanteDto;
import com.projetocorridas.projetocorridas.dto.UsuarioAutenticadoDto;
import com.projetocorridas.projetocorridas.service.AlternativaService;
import com.projetocorridas.projetocorridas.service.PerguntaService;
import com.projetocorridas.projetocorridas.service.CorridaService;
import com.projetocorridas.projetocorridas.service.ParticipanteService;
import com.projetocorridas.projetocorridas.model.EstadoCorrida;

@Controller
public class LobbyController {

    private final ParticipanteService participanteService;
    private final CorridaService corridaService;
    private final PerguntaService perguntaService;
    private final AlternativaService alternativaService;

    public LobbyController(ParticipanteService participanteService, CorridaService corridaService,
            PerguntaService perguntaService, AlternativaService alternativaService) {
        this.participanteService = participanteService;
        this.corridaService = corridaService;
        this.perguntaService = perguntaService;
        this.alternativaService = alternativaService;
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

    @GetMapping("/lobby/participante/ranking")
    public String ranking(HttpServletRequest request, Model model) {
        if (obterUsuarioLogado(request) == null) {
            return "redirect:/auth/login";
        }

        model.addAttribute("ranking", participanteService.listarRanking());
        model.addAttribute("usuarioLogado", obterUsuarioLogado(request));
        return "lobby/ranking";
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
        ParticipanteDto participante = obterParticipanteLogado(request);
        if (participante == null) {
            return "redirect:/auth/login";
        }

        List<CorridaDto> corridas = new ArrayList<>();
        if (participante.getCorridaId() != null) {
            corridas.add(corridaService.obter(participante.getCorridaId()));
        }

        model.addAttribute("participante", participante);
        model.addAttribute("corridas", corridas);
        return "lobby/participante-corridas";
    }

    @GetMapping("/lobby/participante/corridas/{corridaId}")
    public String corridaParticipante(HttpServletRequest request,
            @PathVariable UUID corridaId,
            @RequestParam(name = "indice", defaultValue = "0") int indice,
            Model model) {
        ParticipanteDto participante = obterParticipanteLogado(request);
        if (participante == null) {
            return "redirect:/auth/login";
        }

        if (participante.getCorridaId() == null || !participante.getCorridaId().equals(corridaId)) {
            return "redirect:/lobby/participante/corridas";
        }

        CorridaDto corrida = corridaService.obter(corridaId);
        if (corrida.getEstadoCorrida() == EstadoCorrida.REALIZADA) {
            return "redirect:/lobby/participante/corridas";
        }

        List<PerguntaDto> perguntas = perguntaService.listarPorCorrida(corridaId);

        if (perguntas.isEmpty()) {
            model.addAttribute("participante", participante);
            model.addAttribute("corrida", corrida);
            model.addAttribute("perguntas", perguntas);
            return "lobby/participante-corrida";
        }

        int indiceNormalizado = Math.max(0, Math.min(indice, perguntas.size() - 1));
        PerguntaDto perguntaAtual = perguntas.get(indiceNormalizado);
        List<AlternativaDto> alternativas = alternativaService.listarPorPergunta(perguntaAtual.getId());
        perguntaAtual.setAlternativas(alternativas);

        model.addAttribute("participante", participante);
        model.addAttribute("corrida", corrida);
        model.addAttribute("perguntas", perguntas);
        model.addAttribute("perguntaAtual", perguntaAtual);
        model.addAttribute("indiceAtual", indiceNormalizado);
        model.addAttribute("totalPerguntas", perguntas.size());
        model.addAttribute("temAnterior", indiceNormalizado > 0);
        model.addAttribute("temProxima", indiceNormalizado < perguntas.size() - 1);
        return "lobby/participante-corrida";
    }

    @PostMapping("/lobby/participante/corridas/{corridaId}")
    public String responderPergunta(HttpServletRequest request,
            @PathVariable UUID corridaId,
            @RequestParam UUID perguntaId,
            @RequestParam UUID alternativaId,
            @RequestParam(name = "indice", defaultValue = "0") int indice,
            RedirectAttributes redirectAttributes) {
        ParticipanteDto participante = obterParticipanteLogado(request);
        if (participante == null) {
            return "redirect:/auth/login";
        }

        if (participante.getCorridaId() == null || !participante.getCorridaId().equals(corridaId)) {
            return "redirect:/lobby/participante/corridas";
        }

        List<PerguntaDto> perguntas = perguntaService.listarPorCorrida(corridaId);
        if (perguntas.isEmpty() || indice < 0 || indice >= perguntas.size()) {
            return "redirect:/lobby/participante/corridas";
        }

        PerguntaDto perguntaAtual = perguntas.get(indice);
        if (!perguntaAtual.getId().equals(perguntaId)) {
            return "redirect:/lobby/participante/corridas/" + corridaId + "?indice=" + indice;
        }

        AlternativaDto alternativaSelecionada = alternativaService.obter(perguntaId, alternativaId);
        boolean acertou = Boolean.TRUE.equals(alternativaSelecionada.getCorreta());

        if (acertou) {
            participanteService.incrementarPontos(participante.getId(), 20);
            redirectAttributes.addFlashAttribute("mensagem", "Resposta correta.");
        } else {
            redirectAttributes.addFlashAttribute("erro", "Resposta errada.");
        }

        int proximoIndice = indice + 1;
        if (proximoIndice >= perguntas.size()) {
            corridaService.finalizar(corridaId);
            redirectAttributes.addFlashAttribute("mensagem", "CORRIDA FINALIZADA");
            return "redirect:/lobby/participante/corridas";
        }

        return "redirect:/lobby/participante/corridas/" + corridaId + "?indice=" + proximoIndice;
    }

    @PostMapping("/lobby/participante/corridas/{corridaId}/desistir")
    public String desistirCorrida(HttpServletRequest request,
            @PathVariable UUID corridaId,
            RedirectAttributes redirectAttributes) {
        ParticipanteDto participante = obterParticipanteLogado(request);
        if (participante == null) {
            return "redirect:/auth/login";
        }

        if (participante.getCorridaId() == null || !participante.getCorridaId().equals(corridaId)) {
            return "redirect:/lobby/participante/corridas";
        }

        corridaService.finalizar(corridaId);
        redirectAttributes.addFlashAttribute("mensagem", "CORRIDA FINALIZADA");
        return "redirect:/lobby/participante/corridas";
    }

    private ParticipanteDto obterParticipanteLogado(HttpServletRequest request) {
        Object sessionUsuario = request.getSession(false) == null ? null
                : request.getSession(false).getAttribute("usuarioLogado");

        if (!(sessionUsuario instanceof UsuarioAutenticadoDto autenticado) || autenticado.isAdmin()) {
            return null;
        }

        UUID participanteId = UUID.fromString(autenticado.getId());
        return participanteService.obter(participanteId);
    }

    private UsuarioAutenticadoDto obterUsuarioLogado(HttpServletRequest request) {
        Object sessionUsuario = request.getSession(false) == null ? null
                : request.getSession(false).getAttribute("usuarioLogado");

        if (sessionUsuario instanceof UsuarioAutenticadoDto autenticado) {
            return autenticado;
        }

        return null;
    }
}