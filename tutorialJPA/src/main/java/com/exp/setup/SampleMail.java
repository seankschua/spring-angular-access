package com.exp.setup;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

public class SampleMail {

	public static void main(String[] args) throws MessagingException, IOException{
		
		MimeMessage mm = SendEmail.createEmail("sean.ksc@gmail.com", "me", "hello", "body");
		SendEmail.sendMessage(GmailQuickstart.getGmailService(), "me", mm);
	}
}
