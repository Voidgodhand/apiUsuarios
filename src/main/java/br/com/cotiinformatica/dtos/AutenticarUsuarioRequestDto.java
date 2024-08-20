package br.com.cotiinformatica.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AutenticarUsuarioRequestDto {

	@Email(message = "Informe um endereço de email válido.")
	@NotEmpty(message = "O preenchimento do email é obrigatório.")
	private String email;
	
	@Size(min = 8, message = "A senha informada deve ter pelo menos 8 caracteres.")
	@NotEmpty(message = "O preenchimento da senha é obrigatório.")
	private String senha;
}
