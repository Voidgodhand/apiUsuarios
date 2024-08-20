package br.com.cotiinformatica.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.cotiinformatica.components.JwtTokenComponent;
import br.com.cotiinformatica.dtos.AutenticarUsuarioRequestDto;
import br.com.cotiinformatica.dtos.AutenticarUsuarioResponseDto;
import br.com.cotiinformatica.dtos.CriarUsuarioRequestDto;
import br.com.cotiinformatica.dtos.CriarUsuarioResponseDto;
import br.com.cotiinformatica.dtos.ObterDadosUsuarioResponseDto;
import br.com.cotiinformatica.services.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private JwtTokenComponent jwtTokenComponent;
	
	@PostMapping("criar")
	public CriarUsuarioResponseDto criar(@RequestBody @Valid CriarUsuarioRequestDto request) throws Exception {
		return usuarioService.criar(request);
	}

	@PostMapping("autenticar")
	public AutenticarUsuarioResponseDto autenticar(@RequestBody @Valid AutenticarUsuarioRequestDto request) throws Exception {
		return usuarioService.autenticar(request);
	}
	
	@GetMapping("obter-dados")
	public ObterDadosUsuarioResponseDto obterDados(HttpServletRequest request) throws Exception {

		//capturando o valor do token enviado na requisição para a API
		String token = request.getHeader("Authorization").replace("Bearer", "").trim();
		//extraindo o email do usuário contido no token
		String email = jwtTokenComponent.getEmailFromToken(token);
		
		//retornando os dados do usuário conforme o email
		return usuarioService.obterDados(email);
	}
}










