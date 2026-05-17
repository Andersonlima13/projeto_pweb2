package com.projetocorridas.projetocorridas.service;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.projetocorridas.projetocorridas.dto.UsuarioAutenticadoDto;

@Service
public class JwtService {

    private static final String ISSUER = "projetocorridas";

    private final Algorithm algorithm;
    private final long expirationSeconds;

    public JwtService(@Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-seconds:28800}") long expirationSeconds) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.expirationSeconds = expirationSeconds;
    }

    public String gerarToken(UsuarioAutenticadoDto usuario) {
        Instant expirationInstant = Instant.now().plusSeconds(expirationSeconds);

        return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(usuario.getNome())
                .withClaim("id", usuario.getId())
                .withClaim("nome", usuario.getNome())
                .withClaim("admin", usuario.isAdmin())
                .withExpiresAt(Date.from(expirationInstant))
                .sign(algorithm);
    }

    public Optional<UsuarioAutenticadoDto> validarToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build();

            DecodedJWT jwt = verifier.verify(token);
            return Optional.of(UsuarioAutenticadoDto.builder()
                    .id(jwt.getClaim("id").asString())
                    .nome(jwt.getClaim("nome").asString())
                    .admin(Boolean.TRUE.equals(jwt.getClaim("admin").asBoolean()))
                    .build());
        } catch (JWTVerificationException e) {
            return Optional.empty();
        }
    }

    public long getExpirationSeconds() {
        return expirationSeconds;
    }
}