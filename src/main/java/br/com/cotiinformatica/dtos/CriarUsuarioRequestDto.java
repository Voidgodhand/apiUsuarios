package br.com.cotiinformatica.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CriarUsuarioRequestDto {

	@Size(min = 8, max = 150, message = "Informe no mínimo 8 e no máximo 150 caracteres.")
	@NotEmpty(message = "O preenchimento do nome é obrigatório.")
	private String nome;
	
	@Email(message = "Informe um endereço de email válido.")
	@NotEmpty(message = "O preenchimento do email é obrigatório.")
	private String email;
	
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$", 
			message = "Informe a senha com letras minúsculas, maiúsculas, números, símbolos e pelo menos 8 caracteres.")
	@NotEmpty(message = "O preenchimento da senha é obrigatório.")
	private String senha;
}
