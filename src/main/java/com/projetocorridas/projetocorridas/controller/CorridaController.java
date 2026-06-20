package com.projetocorridas.projetocorridas.controller;

import com.projetocorridas.projetocorridas.dto.CorridaDto;
import com.projetocorridas.projetocorridas.service.CorridaService;
import com.projetocorridas.projetocorridas.service.PerguntaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        mv.addObject("corridaDto", new CorridaDto());
        mv.addObject("openModal", false);
        return mv;
    }

    @GetMapping("/novo")
    public ModelAndView formularioCriar() {
        ModelAndView mv = new ModelAndView("corridas/formulario");
        mv.addObject("corridaDto", new CorridaDto());
        return mv;
    }

    @PostMapping
    public String criar(@ModelAttribute CorridaDto corridaDto, RedirectAttributes redirectAttributes) {
        try {
            corridaService.criar(corridaDto);
            redirectAttributes.addFlashAttribute("mensagem", "Corrida criada com sucesso.");
            return "redirect:/corridas";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
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
        return new ModelAndView("redirect:/corridas/" + id + "/perguntas");
    }

    @PostMapping("/{id}")
    public String alterar(@PathVariable UUID id, @ModelAttribute CorridaDto corridaDto) {
        try {
            corridaDto.setId(id);
            corridaService.alterar(corridaDto);
            return "redirect:/corridas/" + id;
        } catch (IllegalArgumentException e) {
            return "redirect:/corridas/" + id + "/perguntas";
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

    @PostMapping("/{id}/foto")
    public String salvarFoto(@PathVariable UUID id,
            @RequestParam("foto") org.springframework.web.multipart.MultipartFile foto,
            RedirectAttributes redirectAttributes) {
        try {
            corridaService.salvarFoto(id, foto);
            redirectAttributes.addFlashAttribute("mensagem", "Imagem de fundo atualizada com sucesso.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/corridas";
    }

    @GetMapping("/{id}/foto")
    public org.springframework.http.ResponseEntity<byte[]> obterFoto(@PathVariable UUID id) {
        com.projetocorridas.projetocorridas.model.Corrida corrida = corridaService.obterFotoEntity(id);

        if (corrida.getFoto() == null || corrida.getFoto().length == 0) {
            return org.springframework.http.ResponseEntity.notFound().build();
        }

        org.springframework.http.MediaType mediaType = corrida.getFotoTipo() != null
                ? org.springframework.http.MediaType.parseMediaType(corrida.getFotoTipo())
                : org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;

        return org.springframework.http.ResponseEntity.ok()
                .contentType(mediaType)
                .body(corrida.getFoto());
    }

    @PostMapping("/{id}/foto/remover")
    public String removerFoto(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            corridaService.removerFoto(id);
            redirectAttributes.addFlashAttribute("mensagem", "Imagem de fundo removida com sucesso.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/corridas";
    }

}
