package br.com.cotiinformatica.services;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.cotiinformatica.components.JwtTokenComponent;
import br.com.cotiinformatica.components.RabbitMQProducerComponent;
import br.com.cotiinformatica.components.SHA256Component;
import br.com.cotiinformatica.dtos.AutenticarUsuarioRequestDto;
import br.com.cotiinformatica.dtos.AutenticarUsuarioResponseDto;
import br.com.cotiinformatica.dtos.CriarUsuarioRequestDto;
import br.com.cotiinformatica.dtos.CriarUsuarioResponseDto;
import br.com.cotiinformatica.dtos.MensagemUsuarioDto;
import br.com.cotiinformatica.dtos.ObterDadosUsuarioResponseDto;
import br.com.cotiinformatica.entities.Usuario;
import br.com.cotiinformatica.repositories.UsuarioRepository;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private SHA256Component sha256Component;

	@Autowired
	private JwtTokenComponent jwtTokenComponent;

	@Value("${jwt.expiration}")
	private Integer expiration;

	/*
	 * Serviço para realizar o cadastro do usuário
	 */
	public CriarUsuarioResponseDto criar(CriarUsuarioRequestDto request) throws Exception {

		Usuario usuario = new Usuario();

		// capturando os dados do usuário
		usuario.setId(UUID.randomUUID());
		usuario.setNome(request.getNome());
		usuario.setEmail(request.getEmail());
		usuario.setSenha(sha256Component.hash(request.getSenha()));

		// verificar se o usuário já está cadastrado, através do email
		if (usuarioRepository.findByEmail(usuario.getEmail()) != null) {
			throw new IllegalArgumentException("O email informado já está cadastrado. Tente outro.");
		}

		// gravando o usuário no banco de dados
		usuarioRepository.save(usuario);

		// escrevendo a mensagem que deverá ser enviada para a fila
		MensagemUsuarioDto mensagem = new MensagemUsuarioDto();
		mensagem.setEmailUsuario(usuario.getEmail());
		mensagem.setAssunto("Conta de usuário criado com sucesso - COTI Informática");
		mensagem.setTexto("Parabéns, " + usuario.getNome()
				+ ", sua conta de usuário foi criada com sucesso.\n\nAtt\nEquipe COTI.");

		// retornando os dados do usuario cadastrado
		CriarUsuarioResponseDto response = new CriarUsuarioResponseDto();
		response.setId(usuario.getId());
		response.setNome(usuario.getNome());
		response.setEmail(usuario.getEmail());
		response.setDataHoraCadastro(new Date());

		return response;
	}

	/*
	 * Serviço para realizar a autenticação do usuário
	 */
	public AutenticarUsuarioResponseDto autenticar(AutenticarUsuarioRequestDto request) throws Exception {

		// buscar o usuário no banco de dados através do email e da senha
		Usuario usuario = usuarioRepository.findByEmailAndSenha(request.getEmail(),
				sha256Component.hash(request.getSenha()));

		// verificar se o usuário não foi encontrado
		if (usuario == null) {
			throw new IllegalAccessException("Acesso negado. Usuário não encontrado.");
		}

		Date dataAtual = new Date();

		// retornar os dados do usuário autenticado
		AutenticarUsuarioResponseDto response = new AutenticarUsuarioResponseDto();
		response.setId(usuario.getId());
		response.setNome(usuario.getNome());
		response.setEmail(usuario.getEmail());
		response.setDataHoraAcesso(dataAtual);
		response.setDataHoraExpiracao(new Date(dataAtual.getTime() + expiration));
		response.setAccessToken(jwtTokenComponent.generateToken(usuario));

		return response;
	}

	/*
	 * Serviço para consultar e retornar os dados do usuário através do email
	 */
	public ObterDadosUsuarioResponseDto obterDados(String email) throws Exception {

		// consultar o usuário no banco de dados através do email
		Usuario usuario = usuarioRepository.findByEmail(email);

		// verificar se o usuário não foi encontrado
		if (usuario == null) {
			throw new IllegalAccessException("Acesso negado. Usuário não encontrado.");
		}

		// copiando os dados do usuário para o DTO
		ObterDadosUsuarioResponseDto response = new ObterDadosUsuarioResponseDto();
		response.setId(usuario.getId());
		response.setNome(usuario.getNome());
		response.setEmail(usuario.getEmail());

		// retornando os dados
		return response;
	}
}
