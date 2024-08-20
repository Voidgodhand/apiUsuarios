package br.com.cotiinformatica.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.internet.MimeMessage;

@Component
public class MailSenderComponent {

	@Value("${spring.mail.username}")
	private String userName;
	
	@Autowired
	private JavaMailSender javaMailSender;
	
	/*
	 * Método para realizar o envio de emails
	 */
	public void sendMessage(String to, String subject, String body) throws Exception {
		
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
		
		helper.setFrom(userName); //remetente do email
		helper.setTo(to); //destinatário do email
		helper.setSubject(subject); //assunto do email 
		helper.setText(body); //corpo / texto do email
		
		javaMailSender.send(mimeMessage);
	}
}
