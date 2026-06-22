package com.projetocorridas.projetocorridas.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;

import com.projetocorridas.projetocorridas.dto.AdministradorLoginDto;
import com.projetocorridas.projetocorridas.dto.ParticipanteLoginDto;
import com.projetocorridas.projetocorridas.dto.UsuarioAutenticadoDto;
import com.projetocorridas.projetocorridas.service.AuthService;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("participanteLoginDto", new ParticipanteLoginDto());
        model.addAttribute("administradorLoginDto", new AdministradorLoginDto());
        return "login";
    }

    @PostMapping("/login/participante")
    public String loginParticipante(@ModelAttribute("participanteLoginDto") ParticipanteLoginDto dto,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        Optional<UsuarioAutenticadoDto> usuario = authService.autenticarParticipante(dto);
        if (usuario.isPresent()) {
            salvarUsuarioNaSessao(request, usuario.get());
            return "redirect:/lobby/participante";
        }
        redirectAttributes.addFlashAttribute("erro", "Nome ou senha inválidos para participante");
        return "redirect:/auth/login";
    }

    @PostMapping("/corridas")
    public String loginAdministrador(@ModelAttribute("administradorLoginDto") AdministradorLoginDto dto,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        Optional<UsuarioAutenticadoDto> usuario = authService.autenticarAdministrador(dto);
        if (usuario.isPresent()) {
            salvarUsuarioNaSessao(request, usuario.get());
            return "redirect:/lobby";
        }
        redirectAttributes.addFlashAttribute("erro", "Email ou senha inválidos para administrador");
        return "redirect:/auth/login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        if (request.getSession(false) != null) {
            request.getSession(false).invalidate();
        }
        redirectAttributes.addFlashAttribute("mensagem", "Você saiu.");
        return "redirect:/auth/login";
    }

    private void salvarUsuarioNaSessao(HttpServletRequest request, UsuarioAutenticadoDto usuario) {
        HttpSession session = request.getSession(true);
        session.setAttribute("usuarioLogado", usuario);
    }
}