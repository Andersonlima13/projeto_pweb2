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

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.projetocorridas.projetocorridas.dto.AdministradorLoginDto;
import com.projetocorridas.projetocorridas.dto.ParticipanteLoginDto;
import com.projetocorridas.projetocorridas.dto.UsuarioAutenticadoDto;
import com.projetocorridas.projetocorridas.service.AuthService;
import com.projetocorridas.projetocorridas.service.JwtService;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtService jwtService;

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("participanteLoginDto", new ParticipanteLoginDto());
        model.addAttribute("administradorLoginDto", new AdministradorLoginDto());
        return "login";
    }

    @PostMapping("/login/participante")
    public String loginParticipante(@ModelAttribute("participanteLoginDto") ParticipanteLoginDto dto,
            HttpServletRequest request,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes) {
        Optional<UsuarioAutenticadoDto> usuario = authService.autenticarParticipante(dto);
        if (usuario.isPresent()) {
            adicionarCookieJwt(request, response, usuario.get());
            return "redirect:/lobby/participante";
        }
        redirectAttributes.addFlashAttribute("erro", "Nome ou senha inválidos para participante");
        return "redirect:/auth/login";
    }

    @PostMapping("/corridas")
    public String loginAdministrador(@ModelAttribute("administradorLoginDto") AdministradorLoginDto dto,
            HttpServletRequest request,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes) {
        Optional<UsuarioAutenticadoDto> usuario = authService.autenticarAdministrador(dto);
        if (usuario.isPresent()) {
            adicionarCookieJwt(request, response, usuario.get());
            return "redirect:/lobby";
        }
        redirectAttributes.addFlashAttribute("erro", "Email ou senha inválidos para administrador");
        return "redirect:/auth/login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response,
            RedirectAttributes redirectAttributes) {
        if (request.getSession(false) != null) {
            request.getSession(false).invalidate();
        }

        Cookie cookie = new Cookie("PROJETO_CORRIDAS_AUTH", "");
        cookie.setHttpOnly(true);
        cookie.setPath(request.getContextPath().isEmpty() ? "/" : request.getContextPath());
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        redirectAttributes.addFlashAttribute("mensagem", "Você saiu.");
        return "redirect:/auth/login";
    }

    private void adicionarCookieJwt(HttpServletRequest request, HttpServletResponse response,
            UsuarioAutenticadoDto usuario) {
        String token = jwtService.gerarToken(usuario);

        Cookie cookie = new Cookie("PROJETO_CORRIDAS_AUTH", token);
        cookie.setHttpOnly(true);
        cookie.setPath(request.getContextPath().isEmpty() ? "/" : request.getContextPath());
        cookie.setMaxAge((int) jwtService.getExpirationSeconds());
        response.addCookie(cookie);
    }
}