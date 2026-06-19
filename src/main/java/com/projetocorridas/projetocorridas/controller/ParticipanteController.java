package com.projetocorridas.projetocorridas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.projetocorridas.projetocorridas.dto.CorridaDto;
import com.projetocorridas.projetocorridas.dto.ParticipanteDto;
import com.projetocorridas.projetocorridas.service.CorridaService;
import com.projetocorridas.projetocorridas.service.ParticipanteService;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/participantes")
public class ParticipanteController {

    @Autowired
    private ParticipanteService participanteService;

    @Autowired
    private CorridaService corridaService;

    @GetMapping
    public ModelAndView listar() {
        ModelAndView mv = new ModelAndView("participantes/listar");
        List<ParticipanteDto> participantes = participanteService.listarTodos();
        mv.addObject("participantes", participantes);
        return mv;
    }

    // Formulário para novo participante
    @GetMapping("/novo")
    public ModelAndView formularioNovo() {
        ModelAndView mv = new ModelAndView("participantes/formulario");
        mv.addObject("participanteDto", new ParticipanteDto());
        mv.addObject("corridasAssociadas", Collections.emptyList());
        mv.addObject("corridasDisponiveis", corridaService.listarTodas());
        return mv;
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute ParticipanteDto participanteDto) {
        if (participanteDto.getId() != null) {
            participanteService.alterar(participanteDto);
        } else {
            participanteService.criar(participanteDto);
        }
        return "redirect:/participantes";
    }

    @GetMapping("/editar/{id}")
    public ModelAndView editar(@PathVariable UUID id) {
        ModelAndView mv = new ModelAndView("participantes/formulario");
        ParticipanteDto participante = participanteService.obter(id);
        participante.setSenha(null);
        List<CorridaDto> corridas = corridaService.listarTodas();
        Set<UUID> corridaIdsAssociadas = participante.getCorridaIds().stream().collect(Collectors.toSet());
        List<CorridaDto> corridasAssociadas = corridas.stream()
                .filter(corrida -> corridaIdsAssociadas.contains(corrida.getId()))
                .collect(Collectors.toList());
        List<CorridaDto> corridasDisponiveis = corridas.stream()
                .filter(corrida -> !corridaIdsAssociadas.contains(corrida.getId()))
                .collect(Collectors.toList());

        mv.addObject("participanteDto", participante);
        mv.addObject("corridasAssociadas", corridasAssociadas);
        mv.addObject("corridasDisponiveis", corridasDisponiveis);
        return mv;
    }

    // Apagar participante
    @GetMapping("/apagar/{id}")
    public String apagar(@PathVariable UUID id) {
        participanteService.apagar(id);
        return "redirect:/participantes";
    }
}
