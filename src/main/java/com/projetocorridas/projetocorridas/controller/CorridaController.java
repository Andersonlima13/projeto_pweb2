package com.projetocorridas.projetocorridas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.projetocorridas.projetocorridas.dto.CorridaDto;
import com.projetocorridas.projetocorridas.service.CorridaService;
import com.projetocorridas.projetocorridas.service.PerguntaService;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/corridas")
public class CorridaController {

    @Autowired
    private CorridaService corridaService;

    @Autowired
    private PerguntaService perguntaService;

    @GetMapping
    public ModelAndView listar() {
        ModelAndView mv = new ModelAndView("corridas/listar");
        List<CorridaDto> corridas = corridaService.listarTodas();
        mv.addObject("corridas", corridas);
        return mv;
    }

    @GetMapping("/novo")
    public ModelAndView formularioCriar() {
        ModelAndView mv = new ModelAndView("corridas/formulario");
        mv.addObject("corridaDto", new CorridaDto());
        mv.addObject("acao", "Criar");
        return mv;
    }

    @PostMapping
    public String criar(@ModelAttribute CorridaDto corridaDto) {
        try {
            corridaService.criar(corridaDto);
            return "redirect:/corridas";
        } catch (IllegalArgumentException e) {
            return "redirect:/corridas/novo";
        }
    }

    @GetMapping("/{id}")
    public ModelAndView obter(@PathVariable UUID id) {
        ModelAndView mv = new ModelAndView("corridas/detalhes");
        try {
            CorridaDto corrida = corridaService.obter(id);
            corrida.setPerguntas(perguntaService.listarPorCorrida(id));
            mv.addObject("corrida", corrida);
        } catch (IllegalArgumentException e) {
            mv.setViewName("redirect:/corridas");
        }
        return mv;
    }

    @GetMapping("/{id}/editar")
    public ModelAndView formularioEditar(@PathVariable UUID id) {
        ModelAndView mv = new ModelAndView("corridas/formulario");
        try {
            CorridaDto corrida = corridaService.obter(id);
            mv.addObject("corridaDto", corrida);
            mv.addObject("acao", "Editar");
        } catch (IllegalArgumentException e) {
            mv.setViewName("redirect:/corridas");
        }
        return mv;
    }

    @PostMapping("/{id}")
    public String alterar(@PathVariable UUID id, @ModelAttribute CorridaDto corridaDto) {
        try {
            corridaDto.setId(id);
            corridaService.alterar(corridaDto);
            return "redirect:/corridas/" + id;
        } catch (IllegalArgumentException e) {
            return "redirect:/corridas/" + id + "/editar";
        }
    }

    @PostMapping("/{id}/deletar")
    public String apagar(@PathVariable UUID id) {
        try {
            corridaService.apagar(id);
            return "redirect:/corridas";
        } catch (IllegalArgumentException e) {
            return "redirect:/corridas/" + id;
        }
    }
}
