package br.com.cotiinformatica.dtos;

import lombok.Data;

@Data
public class MensagemUsuarioDto {

	private String emailUsuario;
	private String assunto;
	private String texto;
}
