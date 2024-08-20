package br.com.cotiinformatica.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.cotiinformatica.dtos.MensagemUsuarioDto;

@Component
public class RabbitMQConsumerComponent {

	@Autowired
	private MailSenderComponent mailSenderComponent;
	
	@Autowired
	private ObjectMapper objectMapper;
		
	/*
	 * Método para ler e processar a fila 'mensagens' do RabbitMQ
	 */
	//@RabbitListener(queues = { "mensagens" }) //nome da fila
	public void proccessMessage(@Payload String message) throws Exception {
		
		//ler cada mensagem contida na fila e deserializar o seu conteúdo
		//DESERIALIZAR => transformar o conteudo de JSON para Objeto Java
		MensagemUsuarioDto dto = objectMapper.readValue(message, MensagemUsuarioDto.class);
		
		//capturando os dados da mensagem..
		String to = dto.getEmailUsuario();
		String subject = dto.getAssunto();
		String body = dto.getTexto();
		
		//enviar a mensagem por email
		mailSenderComponent.sendMessage(to, subject, body);
	}
}








