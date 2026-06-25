package com.projetocorridas.projetocorridas.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.projetocorridas.projetocorridas.dto.AdministradorLoginDto;
import com.projetocorridas.projetocorridas.dto.ParticipanteLoginDto;
import com.projetocorridas.projetocorridas.dto.UsuarioAutenticadoDto;
import com.projetocorridas.projetocorridas.model.Administrador;
import com.projetocorridas.projetocorridas.model.Participante;
import com.projetocorridas.projetocorridas.repository.AdministradorRepository;
import com.projetocorridas.projetocorridas.repository.ParticipanteRepository;

@Service
public class AuthService {

    @Autowired
    private ParticipanteRepository participanteRepository;

    @Autowired
    private AdministradorRepository administradorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<UsuarioAutenticadoDto> autenticarParticipante(ParticipanteLoginDto dto) {
        return participanteRepository.findByNome(dto.getNome())
                .filter(participante -> passwordEncoder.matches(dto.getSenha(), participante.getSenha()))
                .map(this::mapearParticipante);
    }

    public Optional<UsuarioAutenticadoDto> autenticarAdministrador(AdministradorLoginDto dto) {
        return administradorRepository.findByEmail(dto.getEmail())
                .filter(administrador -> passwordEncoder.matches(dto.getSenha(), administrador.getSenha()))
                .map(this::mapearAdministrador);
    }

    private UsuarioAutenticadoDto mapearParticipante(Participante participante) {
        return UsuarioAutenticadoDto.builder()
                .id(participante.getId() == null ? null : participante.getId().toString())
                .nome(participante.getNome())
                .admin(participante.isAdmin())
                .build();
    }

    private UsuarioAutenticadoDto mapearAdministrador(Administrador administrador) {
        return UsuarioAutenticadoDto.builder()
                .id(administrador.getId() == null ? null : administrador.getId().toString())
                .nome(administrador.getNome())
                .admin(administrador.isAdmin())
                .build();
    }
}
