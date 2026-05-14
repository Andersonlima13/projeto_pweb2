package com.projetocorridas.projetocorridas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.projetocorridas.projetocorridas.dto.AlternativaDto;
import com.projetocorridas.projetocorridas.dto.CorridaDto;
import com.projetocorridas.projetocorridas.dto.PerguntaDto;
import com.projetocorridas.projetocorridas.service.AlternativaService;
import com.projetocorridas.projetocorridas.service.CorridaService;
import com.projetocorridas.projetocorridas.service.PerguntaService;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/corridas/{corridaId}/perguntas/{perguntaId}/alternativas")
public class AlternativaController {

    @Autowired
    private AlternativaService alternativaService;

    @Autowired
    private PerguntaService perguntaService;

    @Autowired
    private CorridaService corridaService;

    @GetMapping
    public ModelAndView listar(@PathVariable UUID corridaId,
            @PathVariable UUID perguntaId) {
        ModelAndView mv = new ModelAndView("alternativas/listar");
        try {
            CorridaDto corrida = corridaService.obter(corridaId);
            PerguntaDto perguntaDto = perguntaService.obter(corridaId, perguntaId);
            List<AlternativaDto> alternativas = alternativaService.listarPorPergunta(perguntaId);
            mv.addObject("corrida", corrida);
            mv.addObject("pergunta", perguntaDto);
            mv.addObject("alternativas", alternativas);
        } catch (IllegalArgumentException e) {
            mv.setViewName("redirect:/corridas/" + corridaId + "/perguntas");
        }
        return mv;
    }

    @GetMapping("/novo")
    public ModelAndView formularioCriar(@PathVariable UUID corridaId,
            @PathVariable UUID perguntaId) {
        ModelAndView mv = new ModelAndView("alternativas/formulario");
        try {
            CorridaDto corrida = corridaService.obter(corridaId);
            PerguntaDto perguntaDto = perguntaService.obter(corridaId, perguntaId);
            AlternativaDto alternativaDto = new AlternativaDto();
            alternativaDto.setPerguntaId(perguntaId);
            alternativaDto.setIsCorreta(false);
            mv.addObject("corrida", corrida);
            mv.addObject("pergunta", perguntaDto);
            mv.addObject("alternativaDto", alternativaDto);
            mv.addObject("acao", "Criar");
        } catch (IllegalArgumentException e) {
            mv.setViewName("redirect:/corridas/" + corridaId + "/perguntas/"
                    + perguntaId + "/alternativas");
        }
        return mv;
    }

    @PostMapping("/salvar")
    public String salvar(@PathVariable UUID corridaId,
            @PathVariable UUID perguntaId,
            @ModelAttribute AlternativaDto alternativaDto) {
        try {
            alternativaDto.setPerguntaId(perguntaId);
            if (alternativaDto.getId() != null) {
                alternativaService.alterar(perguntaId, alternativaDto);
            } else {
                alternativaService.criar(alternativaDto);
            }

            atualizarQuantidadeRespostasCorretas(corridaId, perguntaId);
            return "redirect:/corridas/" + corridaId + "/perguntas/"
                    + perguntaId + "/alternativas";
        } catch (IllegalArgumentException e) {
            if (alternativaDto.getId() != null) {
                return "redirect:/corridas/" + corridaId + "/perguntas/"
                        + perguntaId + "/alternativas/editar/" + alternativaDto.getId();
            }
            return "redirect:/corridas/" + corridaId + "/perguntas/" + perguntaId + "/alternativas/novo";
        }
    }

    @PostMapping
    public String criar(@PathVariable UUID corridaId,
            @PathVariable UUID perguntaId,
            @ModelAttribute AlternativaDto alternativaDto) {
        return salvar(corridaId, perguntaId, alternativaDto);
    }

    @GetMapping({ "/editar/{alternativaId}", "/{alternativaId}/editar" })
    public ModelAndView formularioEditar(@PathVariable UUID corridaId,
            @PathVariable UUID perguntaId,
            @PathVariable UUID alternativaId) {
        ModelAndView mv = new ModelAndView("alternativas/formulario");
        try {
            CorridaDto corrida = corridaService.obter(corridaId);
            PerguntaDto perguntaDto = perguntaService.obter(corridaId, perguntaId);
            AlternativaDto alternativa = alternativaService.obter(perguntaId, alternativaId);
            mv.addObject("corrida", corrida);
            mv.addObject("pergunta", perguntaDto);
            mv.addObject("alternativaDto", alternativa);
            mv.addObject("acao", "Editar");
        } catch (IllegalArgumentException e) {
            mv.setViewName("redirect:/corridas/" + corridaId + "/perguntas/"
                    + perguntaId + "/alternativas");
        }
        return mv;
    }

    @PostMapping("/{alternativaId}")
    public String alterar(@PathVariable UUID corridaId,
            @PathVariable UUID perguntaId,
            @PathVariable UUID alternativaId,
            @ModelAttribute AlternativaDto alternativaDto) {
        alternativaDto.setId(alternativaId);
        return salvar(corridaId, perguntaId, alternativaDto);
    }

    @GetMapping("/apagar/{alternativaId}")
    public String apagarPorGet(@PathVariable UUID corridaId,
            @PathVariable UUID perguntaId,
            @PathVariable UUID alternativaId) {
        return apagar(corridaId, perguntaId, alternativaId);
    }

    @PostMapping("/{alternativaId}/deletar")
    public String apagar(@PathVariable UUID corridaId,
            @PathVariable UUID perguntaId,
            @PathVariable UUID alternativaId) {
        try {
            alternativaService.apagar(perguntaId, alternativaId);
            atualizarQuantidadeRespostasCorretas(corridaId, perguntaId);
            return "redirect:/corridas/" + corridaId + "/perguntas/"
                    + perguntaId + "/alternativas";
        } catch (IllegalArgumentException e) {
            return "redirect:/corridas/" + corridaId + "/perguntas/"
                    + perguntaId + "/alternativas";
        }
    }

    private void atualizarQuantidadeRespostasCorretas(UUID corridaId, UUID perguntaId) {
        PerguntaDto pergunta = perguntaService.obter(corridaId, perguntaId);
        long respostasCorretas = alternativaService.listarPorPergunta(perguntaId).stream()
                .filter(alternativa -> Boolean.TRUE.equals(alternativa.getIsCorreta()))
                .count();
        pergunta.setRespostaCorreta(respostasCorretas);
        perguntaService.alterar(pergunta);
    }
}
