package com.projetocorridas.projetocorridas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.projetocorridas.projetocorridas.dto.CorridaDto;
import com.projetocorridas.projetocorridas.dto.PerguntaDto;
import com.projetocorridas.projetocorridas.service.CorridaService;
import com.projetocorridas.projetocorridas.service.PerguntaService;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/corridas/{corridaId}/perguntas")
public class PerguntaController {

    @Autowired
    private PerguntaService perguntaService;

    @Autowired
    private CorridaService corridaService;

    @GetMapping
    public ModelAndView listar(@PathVariable UUID corridaId) {
        ModelAndView mv = new ModelAndView("perguntas/listar");
        try {
            CorridaDto corrida = corridaService.obter(corridaId);
            List<PerguntaDto> perguntas = perguntaService.listarPorCorrida(corridaId);
            mv.addObject("corrida", corrida);
            mv.addObject("perguntas", perguntas);
        } catch (IllegalArgumentException e) {
            mv.setViewName("redirect:/corridas");
        }
        return mv;
    }

    // Exibir formulário para criar nova pergunta
    @GetMapping("/novo")
    public ModelAndView formularioCriar(@PathVariable UUID corridaId) {
        ModelAndView mv = new ModelAndView("perguntas/formulario");
        try {
            CorridaDto corrida = corridaService.obter(corridaId);
            PerguntaDto perguntaDto = new PerguntaDto();
            perguntaDto.setCorridaId(corridaId);
            mv.addObject("corrida", corrida);
            mv.addObject("perguntaDto", perguntaDto);
            mv.addObject("acao", "Criar");
        } catch (IllegalArgumentException e) {
            mv.setViewName("redirect:/corridas");
        }
        return mv;
    }

    @PostMapping
    public String criar(@PathVariable UUID corridaId, @ModelAttribute PerguntaDto perguntaDto) {
        try {
            perguntaDto.setCorridaId(corridaId);
            perguntaService.criar(perguntaDto);
            return "redirect:/corridas/" + corridaId + "/perguntas";
        } catch (IllegalArgumentException e) {
            return "redirect:/corridas/" + corridaId + "/perguntas/novo";
        }
    }

    @GetMapping("/{perguntaId}")
    public ModelAndView obter(@PathVariable UUID corridaId, @PathVariable UUID perguntaId) {
        ModelAndView mv = new ModelAndView("perguntas/detalhes");
        try {
            CorridaDto corrida = corridaService.obter(corridaId);
            PerguntaDto pergunta = perguntaService.obter(corridaId, perguntaId);
            mv.addObject("corrida", corrida);
            mv.addObject("pergunta", pergunta);
        } catch (IllegalArgumentException e) {
            mv.setViewName("redirect:/corridas/" + corridaId + "/perguntas");
        }
        return mv;
    }

    @GetMapping("/{perguntaId}/editar")
    public ModelAndView formularioEditar(@PathVariable UUID corridaId, @PathVariable UUID perguntaId) {
        ModelAndView mv = new ModelAndView("perguntas/formulario");
        try {
            CorridaDto corrida = corridaService.obter(corridaId);
            PerguntaDto pergunta = perguntaService.obter(corridaId, perguntaId);
            mv.addObject("corrida", corrida);
            mv.addObject("perguntaDto", pergunta);
            mv.addObject("acao", "Editar");
        } catch (IllegalArgumentException e) {
            mv.setViewName("redirect:/corridas/" + corridaId + "/perguntas");
        }
        return mv;
    }

    @PostMapping("/{perguntaId}")
    public String alterar(@PathVariable UUID corridaId, @PathVariable UUID perguntaId,
            @ModelAttribute PerguntaDto perguntaDto) {
        try {
            perguntaDto.setId(perguntaId);
            perguntaDto.setCorridaId(corridaId);
            perguntaService.alterar(perguntaDto);
            return "redirect:/corridas/" + corridaId + "/perguntas/" + perguntaId;
        } catch (IllegalArgumentException e) {
            return "redirect:/corridas/" + corridaId + "/perguntas/" + perguntaId + "/editar";
        }
    }

    @PostMapping("/{perguntaId}/deletar")
    public String apagar(@PathVariable UUID corridaId, @PathVariable UUID perguntaId) {
        try {
            perguntaService.apagar(corridaId, perguntaId);
            return "redirect:/corridas/" + corridaId + "/perguntas";
        } catch (IllegalArgumentException e) {
            return "redirect:/corridas/" + corridaId + "/perguntas/" + perguntaId;
        }
    }
}
