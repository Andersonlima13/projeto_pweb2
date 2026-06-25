package com.projetocorridas.projetocorridas;

import com.projetocorridas.projetocorridas.model.Administrador;
import com.projetocorridas.projetocorridas.model.Alternativa;
import com.projetocorridas.projetocorridas.model.Corrida;
import com.projetocorridas.projetocorridas.model.EstadoCorrida;
import com.projetocorridas.projetocorridas.model.Pergunta;
import com.projetocorridas.projetocorridas.model.Participante;
import com.projetocorridas.projetocorridas.repository.AdministradorRepository;
import com.projetocorridas.projetocorridas.repository.AlternativaRepository;
import com.projetocorridas.projetocorridas.repository.CorridaRepository;
import com.projetocorridas.projetocorridas.repository.PerguntaRepository;
import com.projetocorridas.projetocorridas.repository.ParticipanteRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class ProjetocorridasApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjetocorridasApplication.class, args);
	}

	@Bean
	CommandLineRunner popularBanco(CorridaRepository corridaRepository,
			PerguntaRepository perguntaRepository,
			AlternativaRepository alternativaRepository,
			ParticipanteRepository participanteRepository,
			AdministradorRepository administradorRepository,
			PasswordEncoder passwordEncoder) {
		return args -> {
			garantirAdministrador(administradorRepository, passwordEncoder);

			Corrida corridaFinalizada = garantirCorrida(corridaRepository,
					"Java Basico Finalizado",
					"Trilha finalizada com perguntas basicas de Java para demonstracao.",
					EstadoCorrida.REALIZADA);
			Corrida corridaFinalizada2 = garantirCorrida(corridaRepository,
					"Programacao Orientada a Objetos",
					"Trilha finalizada com perguntas basicas sobre programacao e POO.",
					EstadoCorrida.REALIZADA);
			Corrida corridaAndamento = garantirCorrida(corridaRepository,
					"Spring Boot Iniciante",
					"Trilha em andamento com perguntas basicas sobre Spring e Spring Boot.",
					EstadoCorrida.EM_ANDAMENTO);
			Corrida corridaAndamento2 = garantirCorrida(corridaRepository,
					"Backend com Java",
					"Trilha em andamento para validar listagens e ranking com foco em backend.",
					EstadoCorrida.EM_ANDAMENTO);

			garantirPerguntaComAlternativas(perguntaRepository, alternativaRepository, corridaFinalizada,
					"Qual palavra-chave cria uma heranca em Java?", 2L, 60,
					List.of(
							new AlternativaSeed("implements", false),
							new AlternativaSeed("extends", true),
							new AlternativaSeed("static", false),
							new AlternativaSeed("final", false)));
			garantirPerguntaComAlternativas(perguntaRepository, alternativaRepository, corridaFinalizada2,
					"Qual estrutura nao permite valores duplicados em Java?", 3L, 45,
					List.of(
							new AlternativaSeed("List", false),
							new AlternativaSeed("Array", false),
							new AlternativaSeed("Set", true),
							new AlternativaSeed("String", false)));
			garantirPerguntaComAlternativas(perguntaRepository, alternativaRepository, corridaAndamento,
					"Qual anotacao marca uma classe como componente do Spring?", 3L, 50,
					List.of(
							new AlternativaSeed("@Entity", false),
							new AlternativaSeed("@Table", false),
							new AlternativaSeed("@Component", true),
							new AlternativaSeed("@Id", false)));
			garantirPerguntaComAlternativas(perguntaRepository, alternativaRepository, corridaAndamento2,
					"O Spring Boot facilita principalmente o que?", 1L, 40,
					List.of(
							new AlternativaSeed("A configuracao inicial e a auto-configuracao", true),
							new AlternativaSeed("A remocao de dependencias do projeto", false),
							new AlternativaSeed("A escrita de SQL manual obrigatoria", false),
							new AlternativaSeed("A substituicao do Java", false)));

			garantirParticipante(participanteRepository, passwordEncoder, "Ana Lima", "123456", 120, corridaFinalizada);
			garantirParticipante(participanteRepository, passwordEncoder, "Bruno Souza", "123456", 95,
					corridaFinalizada);
			garantirParticipante(participanteRepository, passwordEncoder, "Carla Mendes", "123456", 80,
					corridaFinalizada2);
			garantirParticipante(participanteRepository, passwordEncoder, "Diego Alves", "123456", 60,
					corridaAndamento);
			garantirParticipante(participanteRepository, passwordEncoder, "Elena Costa", "123456", 40,
					corridaAndamento);
			garantirParticipante(participanteRepository, passwordEncoder, "Fabio Rocha", "123456", 20,
					corridaAndamento2);
		};
	}

	private Administrador garantirAdministrador(AdministradorRepository administradorRepository,
			PasswordEncoder passwordEncoder) {
		return administradorRepository.findByEmail("admin@demo.com")
				.orElseGet(() -> administradorRepository.save(new Administrador(null,
						"Administrador Demo",
						"admin@demo.com",
						passwordEncoder.encode("admin123"),
						true)));
	}

	private Corrida garantirCorrida(CorridaRepository corridaRepository, String titulo, String descricao,
			EstadoCorrida estadoCorrida) {
		Optional<Corrida> corridaExistente = corridaRepository.findByTitulo(titulo);
		if (corridaExistente.isPresent()) {
			return corridaExistente.get();
		}
		Corrida corrida = new Corrida();
		corrida.setTitulo(titulo);
		corrida.setDescricao(descricao);
		corrida.setEstadoCorrida(estadoCorrida);
		return corridaRepository.save(corrida);
	}

	private Participante garantirParticipante(ParticipanteRepository participanteRepository,
			PasswordEncoder passwordEncoder, String nome, String senha,
			int pontos, Corrida corrida) {
		return participanteRepository.findByNome(nome)
				.orElseGet(() -> {
					Participante participante = new Participante();
					participante.setNome(nome);
					participante.setSenha(passwordEncoder.encode(senha));
					participante.setAdmin(false);
					participante.setPontos(pontos);
					participante.getCorridas().add(corrida);
					return participanteRepository.save(participante);
				});
	}

	private Pergunta garantirPerguntaComAlternativas(PerguntaRepository perguntaRepository,
			AlternativaRepository alternativaRepository,
			Corrida corrida,
			String enunciado,
			long respostaCorreta,
			int tempo,
			List<AlternativaSeed> alternativas) {
		Optional<Pergunta> perguntaExistente = perguntaRepository.findByCorridaIdAndEnunciado(corrida.getId(),
				enunciado);
		Pergunta pergunta = perguntaExistente.orElseGet(() -> {
			Pergunta novaPergunta = new Pergunta();
			novaPergunta.setCorridaId(corrida.getId());
			novaPergunta.setEnunciado(enunciado);
			novaPergunta.setRespostaCorreta(Math.toIntExact(respostaCorreta));
			novaPergunta.setTempo(tempo);
			return perguntaRepository.save(novaPergunta);
		});
		if (alternativaRepository.findByPerguntaId(pergunta.getId()).isEmpty()) {
			List<Alternativa> alternativasParaSalvar = alternativas.stream()
					.map(alternativaSeed -> {
						Alternativa alternativa = new Alternativa();
						alternativa.setPergunta(pergunta);
						alternativa.setDescricao(alternativaSeed.descricao());
						alternativa.setCorreta(alternativaSeed.correta());
						return alternativa;
					})
					.toList();
			alternativaRepository.saveAll(alternativasParaSalvar);
		}
		return pergunta;
	}

	private record AlternativaSeed(String descricao, boolean correta) {
	}
}