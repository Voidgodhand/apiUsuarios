package br.com.cotiinformatica.components;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.cotiinformatica.dtos.MensagemUsuarioDto;

@Component
public class RabbitMQProducerComponent {

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private Queue queue;

	/*
	 * Método para gravarmos um conteudo na fila
	 */
	public void sendMessage(MensagemUsuarioDto dto) throws Exception {

		// serializando os dados em formato JSON
		String json = objectMapper.writeValueAsString(dto);
		// gravando o conteudo na fila
		rabbitTemplate.convertAndSend(queue.getName(), json);
	}
}
