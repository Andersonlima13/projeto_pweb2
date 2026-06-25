package com.projetocorridas.projetocorridas.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.projetocorridas.projetocorridas.model.Administrador;
import com.projetocorridas.projetocorridas.model.Participante;

public class AppUserDetails implements UserDetails {

    private final String id;
    private final String username;
    private final String password;
    private final List<GrantedAuthority> authorities;

    public AppUserDetails(String id, String username, String password, List<GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = List.copyOf(authorities);
    }

    public static AppUserDetails fromParticipante(Participante participante) {
        return new AppUserDetails(
                participante.getId().toString(),
                participante.getNome(),
                participante.getSenha(),
                List.of(participante.isAdmin()
                        ? new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_ADMIN")
                        : new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_PARTICIPANTE")));
    }

    public static AppUserDetails fromAdministrador(Administrador administrador) {
        return new AppUserDetails(
                administrador.getId().toString(),
                administrador.getEmail(),
                administrador.getSenha(),
                List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    public String getId() {
        return id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
