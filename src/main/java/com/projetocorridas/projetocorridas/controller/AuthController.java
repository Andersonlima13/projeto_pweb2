package com.projetocorridas.projetocorridas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.projetocorridas.projetocorridas.dto.AdministradorLoginDto;
import com.projetocorridas.projetocorridas.dto.ParticipanteLoginDto;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("participanteLoginDto", new ParticipanteLoginDto());
        model.addAttribute("administradorLoginDto", new AdministradorLoginDto());
        return "login";
    }

    @PostMapping("/login/participante")
    public String loginParticipante(@ModelAttribute("participanteLoginDto") ParticipanteLoginDto dto,
            HttpServletRequest request, HttpServletResponse response,
            RedirectAttributes redirectAttributes) {

        boolean ok = autenticar(dto.getNome(), dto.getSenha(), request, response);
        if (ok) {
            return "redirect:/lobby/participante";
        }
        redirectAttributes.addFlashAttribute("erro", "Nome ou senha inválidos para participante");
        return "redirect:/auth/login";
    }

    @PostMapping("/corridas")
    public String loginAdministrador(@ModelAttribute("administradorLoginDto") AdministradorLoginDto dto,
            HttpServletRequest request, HttpServletResponse response,
            RedirectAttributes redirectAttributes) {

        boolean ok = autenticar(dto.getEmail(), dto.getSenha(), request, response);
        if (ok) {
            return "redirect:/lobby";
        }
        redirectAttributes.addFlashAttribute("erro", "Email ou senha inválidos para administrador");
        return "redirect:/auth/login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        SecurityContextHolder.clearContext();
        if (request.getSession(false) != null) {
            request.getSession(false).invalidate();
        }
        redirectAttributes.addFlashAttribute("mensagem", "Você saiu.");
        return "redirect:/auth/login";
    }

    private boolean autenticar(String username, String senha,
            HttpServletRequest request, HttpServletResponse response) {
        try {
            Authentication authRequest = new UsernamePasswordAuthenticationToken(username, senha);
            Authentication authResult = authenticationManager.authenticate(authRequest);

            SecurityContextHolder.getContext().setAuthentication(authResult);
            securityContextRepository.saveContext(SecurityContextHolder.getContext(), request, response);

            return true;
        } catch (BadCredentialsException e) {
            return false;
        }
    }
}