package org.brunocunha.taskerbox.impl.email;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import org.alfredlibrary.utilitarios.correios.RegistroRastreamento;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.brunocunha.taskerbox.core.DefaultTaskerboxAction;
import org.brunocunha.taskerbox.core.http.ssl.SSLAuthenticator;
import org.brunocunha.taskerbox.core.utils.TaskerboxDateUtils;
import org.brunocunha.taskerbox.core.utils.TaskerboxFileUtils;
import org.brunocunha.taskerbox.impl.toaster.StringToasterAction;
import org.brunocunha.taskerbox.impl.tracking.CorreiosChannel;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import twitter4j.Status;

import com.buscape.developer.result.type.Offer;
import com.sun.syndication.feed.synd.SyndEntry;

/**
 * Action that shows Feeds in a Toaster Popup
 * 
 * @author Bruno Candido Volpato da Cunha
 * 
 */
@Log4j
public class EmailAction extends DefaultTaskerboxAction<Object> {

	@Email
	@NotEmpty
	@Getter @Setter
	private String email;

	@NotEmpty
	@Getter @Setter
	private String smtpHost;

	@Getter @Setter
	@Min(1) @Max(65535)
	private int smtpPort;

	@Getter @Setter
	private String smtpFrom;

	@Getter @Setter
	private String smtpUser;

	@Getter @Setter
	private String smtpPassword;

	@Getter @Setter
	private boolean enableTLS;

	@Getter @Setter
	private boolean enableSSL;

	@Getter @Setter
	private boolean showToaster = true;

	@Getter @Setter
	private File logFile;

	@Getter @Setter
	private String emailTitle = "Email de Taskerbox";
	
	/*
	 * public static void main(String[] args) { Properties prop = new
	 * Properties(); prop.put("title", "titulo"); prop.put("content",
	 * "titulo explanado");
	 * 
	 * System.out.println(callTemplate("synd.html", prop)); }
	 */

	@Override
	public void action(final Object entry) {

		if (entry instanceof Status) {

			handleTweet((Status) entry);

		} else if (entry instanceof RegistroRastreamento) {

			EmailValueVO vo = new EmailValueVO();
			vo.setTitle("Rastreamento " + getChannel().getProperty("tracking") + " - " + getChannel().getProperty("descricao"));
			vo.setBody(CorreiosChannel
					.formatTracking((RegistroRastreamento) entry, getChannel().getProperty("tracking"),
							getChannel().getProperty("descricao")));
			
			handleEmailValue(vo);
			

		} else if (entry instanceof SyndEntry) {

			handleRss((SyndEntry) entry);

		} else if (entry instanceof EmailValueVO) {

			handleEmailValue((EmailValueVO) entry);

		} else if (entry instanceof Offer) {
			Offer offer = (Offer) entry;
			
			EmailValueVO vo = new EmailValueVO();
			vo.setTitle(offer.getSeller().getSellerName() + ": " + offer.getOfferName() + " - " + offer.getPrice().getValue());
			vo.setBody(offer.getLinks().getLinks().get(0).getUrl());
			
			handleEmailValue(vo);

		} else {
			
			logWarn(log, "Email Unknown Action!! " + entry.getClass() + " --- " + entry.toString());

			EmailValueVO vo = new EmailValueVO();
			vo.setTitle(emailTitle);
			vo.setBody(entry.toString());
			
			handleEmailValue(vo);

		}

	}

	private void handleRss(SyndEntry entry) {
		log.debug("RSS2Email --> " + entry.getTitle());

		Properties templateProps = new Properties();
		templateProps.put("entry", entry);

		TaskerboxFileUtils.saveTempFile("rssEntry", entry.toString());

		try {
			send("[" + getChannel().getId() + "] " + entry.getTitle(), callTemplate("email/synd.html", templateProps));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static String callTemplate(String template, Properties props) {

		Properties veProps = new Properties();
		veProps.setProperty(Velocity.RESOURCE_LOADER, "classpath");
		veProps.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());

		// inicializando o velocity
		VelocityEngine ve = new VelocityEngine(veProps);
		ve.init();

		// criando o contexto que liga o java ao template
		VelocityContext context = new VelocityContext();

		log.debug("Using template " + template);
		Template t = ve.getTemplate(template);

		for (Object prop : props.keySet()) {
			context.put(prop.toString(), props.get(prop));
		}

		StringWriter writer = new StringWriter();
		// mistura o contexto com o template
		t.merge(context, writer);

		writer.flush();

		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return writer.getBuffer().toString();
	}

	private void handleEmailValue(EmailValueVO email) {

		try {
			send(email.getTitle(), email.getBody());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private void handleTweet(Status status) {
		log.debug("Tweet2Email --> " + status.getUser().getScreenName() + ": " + status.getText());

		Properties templateProps = new Properties();
		templateProps.put("status", status);

		TaskerboxFileUtils.saveTempFile("status", status.toString());

		try {
			send("Tweet de  @" + status.getUser().getScreenName(), callTemplate("email/tweet.html", templateProps));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	public void send(String title, String content) throws AddressException, MessagingException, KeyManagementException,
			NoSuchAlgorithmException {

		Properties props = System.getProperties();

		SSLAuthenticator sslAuthenticator = null;
		if (enableSSL) {
			log.debug("Using SSL to Connection with " + smtpHost + ":" + smtpPort);

			sslAuthenticator = new SSLAuthenticator(smtpUser, smtpPassword);

			SSLContext ctx = SSLContext.getInstance("TLS");
			ctx.init(new KeyManager[0], new TrustManager[] { new SSLAuthenticator.DefaultTrustManager() },
					new SecureRandom());
			SSLContext.setDefault(ctx);

			props.setProperty("mail.smtp.submitter", sslAuthenticator.getPasswordAuthentication().getUserName());
			props.setProperty("mail.smtp.auth", "true");
		}

		props.setProperty("mail.smtp.host", smtpHost);
		props.setProperty("mail.smtp.port", String.valueOf(smtpPort));
		props.setProperty("mail.smtp.starttls", String.valueOf(enableTLS));
		props.setProperty("mail.smtp.starttls.enable", "true");

		Session session;

		if (smtpPassword != null && !smtpPassword.equals("")) {
			props.setProperty("mail.smtp.auth", "true");

			Authenticator auth = new Authenticator() {
				public PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(smtpUser, smtpPassword);
				}
			};

			if (enableSSL) {
				log.debug("Opening SSL Session...");

				session = Session.getInstance(props, sslAuthenticator);
			} else {
				session = Session.getInstance(props, auth);
			}
		} else {
			session = Session.getInstance(props);
		}


		
		if (logFile != null) {
			try {
				FileWriter out = new FileWriter(logFile, true);
				out.write("=========================================\r\n");
				out.write(TaskerboxDateUtils.getTimestamp() + " Assunto: " + title + "\r\n");
				out.write(content + "\r\n");
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		String msg = "Enviando e-mail para "+ email + " (Canal: [canal]): " + title;
		if (getChannel() != null) {
			msg = msg.replace("[canal]", getChannel().getId());
		}
		logInfo(log, "Sending email: " + msg);
		
		if (showToaster) {
			new StringToasterAction().action(msg);
		}
		
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(smtpFrom));
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
		message.setSubject(title);
		message.setContent(content, "text/html");

		Transport.send(message);

	}

	
}
