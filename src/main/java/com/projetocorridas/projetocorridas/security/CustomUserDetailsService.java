package com.projetocorridas.projetocorridas.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.projetocorridas.projetocorridas.model.Administrador;
import com.projetocorridas.projetocorridas.model.Participante;
import com.projetocorridas.projetocorridas.repository.AdministradorRepository;
import com.projetocorridas.projetocorridas.repository.ParticipanteRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private ParticipanteRepository participanteRepository;

    @Autowired
    private AdministradorRepository administradorRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Participante> participante = participanteRepository.findByNome(username);
        if (participante.isPresent()) {
            return AppUserDetails.fromParticipante(participante.get());
        }

        Optional<Administrador> administrador = administradorRepository.findByEmail(username);
        if (administrador.isPresent()) {
            return AppUserDetails.fromAdministrador(administrador.get());
        }

        throw new UsernameNotFoundException("Usuário não encontrado: " + username);
    }
}