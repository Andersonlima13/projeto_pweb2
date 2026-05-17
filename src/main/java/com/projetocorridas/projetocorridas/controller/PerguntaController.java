package com.projetocorridas.projetocorridas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.projetocorridas.projetocorridas.dto.AlternativaDto;
import com.projetocorridas.projetocorridas.dto.PerguntaDto;
import com.projetocorridas.projetocorridas.dto.CorridaDto;
import com.projetocorridas.projetocorridas.service.AlternativaService;
import com.projetocorridas.projetocorridas.service.CorridaService;
import com.projetocorridas.projetocorridas.service.PerguntaService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/corridas/{corridaId}/perguntas")
public class PerguntaController {

    @Autowired
    private PerguntaService perguntaService;

    @Autowired
    private CorridaService corridaService;

    @Autowired
    private AlternativaService alternativaService;

    @GetMapping
    public ModelAndView listar(@PathVariable UUID corridaId) {
        ModelAndView mv = new ModelAndView("perguntas/listar");
        try {
            CorridaDto corrida = corridaService.obter(corridaId);
            List<PerguntaDto> perguntas = perguntaService.listarPorCorrida(corridaId);
            perguntas.forEach(this::carregarAlternativas);
            long totalAlternativas = perguntas.stream()
                    .mapToLong(pergunta -> pergunta.getAlternativas() == null ? 0 : pergunta.getAlternativas().size())
                    .sum();
            long totalAlternativasCorretas = perguntas.stream()
                    .mapToLong(PerguntaDto::getRespostaCorreta)
                    .sum();
            mv.addObject("corrida", corrida);
            mv.addObject("perguntas", perguntas);
            mv.addObject("totalPerguntas", perguntas.size());
            mv.addObject("totalAlternativas", totalAlternativas);
            mv.addObject("totalAlternativasCorretas", totalAlternativasCorretas);
        } catch (IllegalArgumentException e) {
            mv.setViewName("redirect:/corridas");
        }
        return mv;
    }

    @PostMapping("/editar")
    public String editarCorrida(@PathVariable UUID corridaId,
            @RequestParam String titulo,
            @RequestParam String descricao,
            RedirectAttributes redirectAttributes) {
        try {
            String tituloLimpo = titulo == null ? null : titulo.trim();
            String descricaoLimpa = descricao == null ? null : descricao.trim();
            CorridaDto corridaDto = CorridaDto.builder()
                    .id(corridaId)
                    .titulo(tituloLimpo)
                    .descricao(descricaoLimpa)
                    .build();
            corridaService.alterar(corridaDto);
            redirectAttributes.addFlashAttribute("mensagem", "Nome da corrida atualizado.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/corridas/" + corridaId + "/perguntas";
    }

    @PostMapping("/excluir")
    public String excluirCorrida(@PathVariable UUID corridaId, RedirectAttributes redirectAttributes) {
        try {
            corridaService.apagar(corridaId);
            redirectAttributes.addFlashAttribute("mensagem", "Corrida excluida com sucesso.");
            return "redirect:/corridas";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:/corridas/" + corridaId + "/perguntas";
        }
    }

    @GetMapping("/novo")
    public ModelAndView formularioCriar(@PathVariable UUID corridaId) {
        ModelAndView mv = new ModelAndView("perguntas/formulario");
        try {
            CorridaDto corrida = corridaService.obter(corridaId);
            PerguntaDto perguntaDto = new PerguntaDto();
            perguntaDto.setCorridaId(corridaId);
            perguntaDto.setRespostaCorreta(0);
            perguntaDto.setTempo(30);
            prepararAlternativasFormulario(perguntaDto);
            mv.addObject("corrida", corrida);
            mv.addObject("perguntaDto", perguntaDto);
            mv.addObject("acao", "Criar");
        } catch (IllegalArgumentException e) {
            mv.setViewName("redirect:/corridas");
        }
        return mv;
    }

    @PostMapping("/salvar")
    public String salvar(@PathVariable UUID corridaId, @ModelAttribute PerguntaDto perguntaDto,
            RedirectAttributes redirectAttributes) {
        try {
            perguntaDto.setCorridaId(corridaId);
            List<AlternativaDto> alternativas = alternativasValidas(perguntaDto.getAlternativas());
            perguntaDto.setRespostaCorreta(contarAlternativasCorretas(alternativas));
            perguntaDto.setTempo(normalizarTempo(perguntaDto.getTempo()));

            if (perguntaDto.getId() != null) {
                perguntaService.alterar(perguntaDto);
                alternativaService.substituirPorPergunta(perguntaDto.getId(), alternativas);
            } else {
                PerguntaDto perguntaCriada = perguntaService.criar(perguntaDto);
                alternativaService.substituirPorPergunta(perguntaCriada.getId(), alternativas);
            }

            redirectAttributes.addFlashAttribute("mensagem", "Pergunta criada com sucesso.");
            return "redirect:/corridas/" + corridaId + "/perguntas";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            if (perguntaDto.getId() != null) {
                return "redirect:/corridas/" + corridaId + "/perguntas/editar/" + perguntaDto.getId();
            }
            return "redirect:/corridas/" + corridaId + "/perguntas/novo";
        }
    }

    @PostMapping
    public String criar(@PathVariable UUID corridaId, @ModelAttribute PerguntaDto perguntaDto,
            RedirectAttributes redirectAttributes) {
        return salvar(corridaId, perguntaDto, redirectAttributes);
    }

    @GetMapping("/{perguntaId}")
    public ModelAndView obter(@PathVariable UUID corridaId, @PathVariable UUID perguntaId) {
        ModelAndView mv = new ModelAndView("perguntas/detalhes");
        try {
            CorridaDto corrida = corridaService.obter(corridaId);
            PerguntaDto pergunta = perguntaService.obter(corridaId, perguntaId);
            pergunta.setAlternativas(alternativaService.listarPorPergunta(perguntaId));
            mv.addObject("corrida", corrida);
            mv.addObject("pergunta", pergunta);
        } catch (IllegalArgumentException e) {
            mv.setViewName("redirect:/corridas/" + corridaId + "/perguntas");
        }
        return mv;
    }

    @GetMapping({ "/editar/{perguntaId}", "/{perguntaId}/editar" })
    public ModelAndView formularioEditar(@PathVariable UUID corridaId, @PathVariable UUID perguntaId) {
        ModelAndView mv = new ModelAndView("perguntas/formulario");
        try {
            CorridaDto corrida = corridaService.obter(corridaId);
            PerguntaDto pergunta = perguntaService.obter(corridaId, perguntaId);
            pergunta.setAlternativas(alternativaService.listarPorPergunta(perguntaId));
            prepararAlternativasFormulario(pergunta);
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
            @ModelAttribute PerguntaDto perguntaDto, RedirectAttributes redirectAttributes) {
        perguntaDto.setId(perguntaId);
        return salvar(corridaId, perguntaDto, redirectAttributes);
    }

    @GetMapping("/apagar/{perguntaId}")
    public String apagarPorGet(@PathVariable UUID corridaId, @PathVariable UUID perguntaId) {
        return apagar(corridaId, perguntaId);
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

    private void carregarAlternativas(PerguntaDto pergunta) {
        pergunta.setAlternativas(alternativaService.listarPorPergunta(pergunta.getId()));
    }

    private void prepararAlternativasFormulario(PerguntaDto perguntaDto) {
        if (perguntaDto.getAlternativas() == null) {
            perguntaDto.setAlternativas(new ArrayList<>());
        }

        if (perguntaDto.getAlternativas().isEmpty()) {
            perguntaDto.getAlternativas().add(new AlternativaDto());
        }
    }

    private List<AlternativaDto> alternativasValidas(List<AlternativaDto> alternativas) {
        List<AlternativaDto> alternativasValidas = new ArrayList<>();

        if (alternativas == null) {
            return alternativasValidas;
        }

        for (AlternativaDto alternativa : alternativas) {
            if (alternativa == null || alternativa.getDescricao() == null
                    || alternativa.getDescricao().trim().isEmpty()) {
                continue;
            }

            alternativasValidas.add(AlternativaDto.builder()
                    .id(alternativa.getId())
                    .perguntaId(alternativa.getPerguntaId())
                    .descricao(alternativa.getDescricao().trim())
                    .correta(Boolean.TRUE.equals(alternativa.getCorreta()))
                    .build());
        }

        return alternativasValidas;
    }

    private long contarAlternativasCorretas(List<AlternativaDto> alternativas) {
        return alternativas.stream()
                .filter(alternativa -> Boolean.TRUE.equals(alternativa.getCorreta()))
                .count();
    }

    private Integer normalizarTempo(Integer tempo) {
        if (tempo == null) {
            return 30;
        }
        return Math.max(tempo, 30);
    }
}
