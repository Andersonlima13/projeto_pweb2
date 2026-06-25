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
        System.out.println("[DEBUG] loadUserByUsername chamado com username='" + username + "'");

        Optional<Participante> participante = participanteRepository.findByNome(username);
        System.out.println("[DEBUG] participante encontrado? " + participante.isPresent());

        if (participante.isPresent()) {
            Participante p = participante.get();
            System.out.println("[DEBUG] participante.id=" + p.getId()
                    + " nome=" + p.getNome()
                    + " senhaHash=" + p.getSenha()
                    + " admin=" + p.isAdmin());
            UserDetails ud = AppUserDetails.fromParticipante(p);
            System.out.println("[DEBUG] UserDetails gerado: username=" + ud.getUsername()
                    + " authorities=" + ud.getAuthorities()
                    + " enabled=" + ud.isEnabled()
                    + " accountNonLocked=" + ud.isAccountNonLocked()
                    + " accountNonExpired=" + ud.isAccountNonExpired()
                    + " credentialsNonExpired=" + ud.isCredentialsNonExpired());
            return ud;
        }

        Optional<Administrador> administrador = administradorRepository.findByEmail(username);
        System.out.println("[DEBUG] administrador encontrado? " + administrador.isPresent());

        if (administrador.isPresent()) {
            return AppUserDetails.fromAdministrador(administrador.get());
        }

        System.out.println("[DEBUG] NINGUÉM encontrado, lançando UsernameNotFoundException");
        throw new UsernameNotFoundException("Usuário não encontrado: " + username);
    }
}