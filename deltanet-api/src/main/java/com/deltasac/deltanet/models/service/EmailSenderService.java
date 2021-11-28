package com.deltasac.deltanet.models.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {
	
	@Autowired
	private JavaMailSender mailSender;
	
	public void sendEmail(String toEmail,
			              String subject,
			              String body) {
		System.out.println("sendEmail");
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom("deltanet@delta.com.pe");
		message.setTo(toEmail);
		message.setText(body);
		message.setSubject(subject);
		
		mailSender.send(message);
	}

}
