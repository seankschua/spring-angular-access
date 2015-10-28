package com.exp.models;

import java.util.Properties;

import javax.crypto.SecretKey;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

import io.jsonwebtoken.impl.crypto.MacProvider;

@Component
public class Universe {
	
	private SecretKey key;
	private String test;
	private Gson gson;
	
	public Universe(){
		this.key = MacProvider.generateKey();
		this.test = "test works";
		this.gson = new Gson();
	}

	public SecretKey getKey() {
		return key;
	}

	public void setKey(SecretKey key) {
		this.key = key;
	}

	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}

	public Gson getGson() {
		return gson;
	}

	public void setGson(Gson gson) {
		this.gson = gson;
	}
	
	@Async
	public void sendMailSSL(Logger log, String rec, String subject, String body){
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");

		Session session = Session.getDefaultInstance(props,
			new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(Secret.GMAIL_USER,Secret.GMAIL_PASS);
				}
			});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(Secret.GMAIL_ADDRESS));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(rec));
			message.setSubject(subject);
			message.setText(body);

			Transport.send(message);

			log.info("sendMailSSL(): " + rec + "~" + subject);

		} catch (MessagingException e) {
			log.error("sendMailSSL(): " + e.getMessage() + "~" + rec + "~" + subject);
			e.printStackTrace();
			
		}
	}
	
}
