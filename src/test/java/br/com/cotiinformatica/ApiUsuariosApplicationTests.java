package br.com.cotiinformatica;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;

import br.com.cotiinformatica.dtos.AutenticarUsuarioRequestDto;
import br.com.cotiinformatica.dtos.AutenticarUsuarioResponseDto;
import br.com.cotiinformatica.dtos.CriarUsuarioRequestDto;
import br.com.cotiinformatica.dtos.CriarUsuarioResponseDto;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ApiUsuariosApplicationTests {
	
	@Autowired
	private MockMvc mockMvc; //realizar as requisições para a API
	
	@Autowired
	private ObjectMapper objectMapper; //serializar dados em formato JSON
	
	//atributo para armazenar o email do usuário cadastrado
	private static String emailUsuario;

	@Test
	@Order(1)
	void criarUsuarioTest() throws Exception {
		
		Faker faker = new Faker();
		
		//preenchendo os dados da requisição
		CriarUsuarioRequestDto request = new CriarUsuarioRequestDto();
		request.setNome(faker.name().fullName());
		request.setEmail(faker.internet().emailAddress());
		request.setSenha("@Teste2024");
		
		//enviando a requisição para a API
		MvcResult result = mockMvc.perform(post("/api/usuarios/criar")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andReturn();
		
		//ler os dados obtidos da API
		String json = result.getResponse().getContentAsString();
		CriarUsuarioResponseDto response = objectMapper.readValue(json, CriarUsuarioResponseDto.class);
				
		//comparando os dados obtidos com os dados enviados
		assertNotNull(response.getId());
		assertEquals(response.getNome(), request.getNome());
		assertEquals(response.getEmail(), request.getEmail());
		assertNotNull(response.getDataHoraCadastro());
		
		//armazenando o email do usuário cadastrado
		this.emailUsuario = response.getEmail();
	}
	
	@Test
	@Order(2)
	void emailJaCadastradoTest() throws Exception {
		
		Faker faker = new Faker();
		
		//criando os dados do teste (requisição)
		CriarUsuarioRequestDto request = new CriarUsuarioRequestDto();
		request.setNome(faker.name().fullName());
		request.setEmail(emailUsuario);
		request.setSenha("@Teste2024");
		
		//enviando a requisição / dados para a API
		MvcResult result = mockMvc.perform(post("/api/usuarios/criar")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest())
				.andReturn();
		
		//capturando a resposta
		String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
		//verificando o conteúdo da resposta
		assertTrue(json.contains("O email informado já está cadastrado. Tente outro."));
	}
	
	@Test
	@Order(3)
	void autenticarUsuarioTest() throws Exception {
		
		AutenticarUsuarioRequestDto request = new AutenticarUsuarioRequestDto();
		
		request.setEmail(emailUsuario);
		request.setSenha("@Teste2024");
		
		MvcResult result = mockMvc.perform(post("/api/usuarios/autenticar")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andReturn();
		
		String json = result.getResponse().getContentAsString();
		AutenticarUsuarioResponseDto response = objectMapper.readValue(json, AutenticarUsuarioResponseDto.class);
		
		assertNotNull(response.getId());
		assertNotNull(response.getNome());
		assertEquals(response.getEmail(), request.getEmail());
		assertNotNull(response.getAccessToken());
		assertNotNull(response.getDataHoraAcesso());
	}
	
	@Test
	@Order(4)
	void acessoNegadoTest() throws Exception {
		
		Faker faker = new Faker();
		
		AutenticarUsuarioRequestDto request = new AutenticarUsuarioRequestDto();
		
		request.setEmail(faker.internet().emailAddress());
		request.setSenha("@Teste2024");
		
		MvcResult result = mockMvc.perform(post("/api/usuarios/autenticar")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isUnauthorized())
				.andReturn();
		
		String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
		assertTrue(json.contains("Acesso negado. Usuário não encontrado."));
	}
	
	@Test
	@Order(5)
	void validacaoDeCamposCriarUsuario() throws Exception {
				
		CriarUsuarioRequestDto request = new CriarUsuarioRequestDto();
		request.setNome(null); //vazio
		request.setEmail(null); //vazio
		request.setSenha(null); //vazio
		
		MvcResult result = mockMvc.perform(post("/api/usuarios/criar")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest())
				.andReturn();
		
		String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);

		assertTrue(json.contains("O preenchimento do nome é obrigatório."));
		assertTrue(json.contains("O preenchimento do email é obrigatório."));
		assertTrue(json.contains("O preenchimento da senha é obrigatório."));
		
		request.setNome("Teste"); 
		request.setEmail("Teste"); 
		request.setSenha("Teste"); 
		
		result = mockMvc.perform(post("/api/usuarios/criar")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest())
				.andReturn();
		
		json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);

		assertTrue(json.contains("Informe um endereço de email válido."));
		assertTrue(json.contains("Informe a senha com letras minúsculas, maiúsculas, números, símbolos e pelo menos 8 caracteres."));
		assertTrue(json.contains("Informe no mínimo 8 e no máximo 150 caracteres."));
	}
	
	@Test
	@Order(6)
	void validacaoDeCamposAutenticarUsuario() throws Exception {
				
		AutenticarUsuarioRequestDto request = new AutenticarUsuarioRequestDto();
		request.setEmail(null); //vazio
		request.setSenha(null); //vazio
		
		MvcResult result = mockMvc.perform(post("/api/usuarios/autenticar")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest())
				.andReturn();
		
		String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);

		assertTrue(json.contains("O preenchimento do email é obrigatório."));
		assertTrue(json.contains("O preenchimento da senha é obrigatório."));
		
		request.setEmail("Teste"); 
		request.setSenha("Teste"); 
		
		result = mockMvc.perform(post("/api/usuarios/autenticar")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest())
				.andReturn();
		
		json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);

		assertTrue(json.contains("Informe um endereço de email válido."));
		assertTrue(json.contains("A senha informada deve ter pelo menos 8 caracteres."));
	}
}
