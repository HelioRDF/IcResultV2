package br.com.icresult.email;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.HtmlEmail;
import org.omnifaces.util.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.icresult.html.CorpoHtmlGIT;
import br.com.icresult.html.CorpoHtmlInspecao;

/**
 * 
 * -Classe de configuração p/ envio de e-mail.
 * 
 * @author Helio Franca
 * @since 26/06/2018
 * @version 1.2
 * 
 */

public class EnviarEmail {

	// private String smtp_365 = "smtp.office365.com";
	private static final String SMTP_365 = "smtp-mail.outlook.com";
	private static final String SMTP_GMAIL = "smtp.gmail.com";
	private static final String APELIDO = "Inspeção de Código HK-RSI";
	private static Logger log = LoggerFactory.getLogger(EnviarEmail.class);

	/**
	 * Seta as propriedade de configuração de email de recuperação de senha
	 * 
	 * @param resultado - String
	 * @param titulo    - String
	 */
	public boolean emailHtmlRecuperarSenha(String resultado, String titulo, String chave, String origemEMail,
			String destinoEmail) {

		try {

			// Create the email message
			HtmlEmail email = new HtmlEmail();

			email.setDebug(true);
			email.setHostName(SMTP_365);
			email.setAuthenticator(new DefaultAuthenticator(origemEMail, chave));
			email.addTo(destinoEmail, "Você");
			email.setFrom(origemEMail, APELIDO);
			email.setSubject(titulo);
			email.setStartTLSEnabled(true);

			String textoHtml = "<h3>"+resultado+"</h3>";

			// set the html message
			email.setHtmlMsg(textoHtml);

			// set the alternative message
			email.setTextMsg("Seu E-mail não suporta mensagem no formato HTML (OFFICE ).");

			// send the email
			System.out.println("\nResposta:"+email.send()+"\n");
			
			Messages.addGlobalInfo("E-mail enviado");
			return true;

		} catch (Exception e) {
			Messages.addGlobalError("Erro ao enviar e-mail");
			return false;
		}

	}

	/**
	 * Seta as propriedade de configuração de email
	 * 
	 * @param resultado - String
	 * @param titulo    - String
	 */
	public void emailHtmlGIT(String resultado, String titulo, String chave, String origemEMail, String destinoEmail) {

		try {

			// resultado = "<html>The apache logo Office- </html>";

			// Create the email message
			HtmlEmail email = new HtmlEmail();

			email.setDebug(true);
			email.setHostName(SMTP_365);
			email.setAuthenticator(new DefaultAuthenticator(origemEMail, chave));
			email.addTo(destinoEmail, "HELIO RSI");
			// email.addTo(destino, "HELIO SANTANDER");
			// email.addTo(destino3, "MONITOR APP");
			email.setFrom(origemEMail, "Inspeção de Código HK-RSI");
			email.setSubject(titulo);
			email.setStartTLSEnabled(true);
			String textoHtml = CorpoHtmlGIT.bodyHTML(resultado);

			// set the html message
			email.setHtmlMsg(textoHtml);

			// set the alternative message
			email.setTextMsg("Seu E-mail não suporta mensagem no formato HTML. (emailHtmlGIT())");

		} catch (Exception e) {
			System.out.println("Email office-365 não foi enviado----------------------------");
		}

	}

	public void emailHtmlInspecao(String resultado, String titulo, String chave, String origemEMail,
			String destinoEmail) {

		try {

			// resultado = "<html>The apache logo Office- </html>";

			// Create the email message
			HtmlEmail email = new HtmlEmail();

			email.setDebug(true);
			email.setHostName(SMTP_365);
			email.setAuthenticator(new DefaultAuthenticator(origemEMail, chave));
			email.addTo(destinoEmail, "HELIO SANTANDER");
			email.setFrom(origemEMail, APELIDO);
			email.setSubject(titulo);
			email.setStartTLSEnabled(true);

			String textoHtml = CorpoHtmlInspecao.bodyHTML(resultado);

			// set the html message
			email.setHtmlMsg(textoHtml);

			// set the alternative message
			email.setTextMsg("Seu E-mail não suporta mensagem no formato HTML (OFFICE ).");

			// send the email
			email.send();
			Messages.addGlobalInfo("E-mail enviado.");

		} catch (Exception e) {
			System.out.println("Email office-365 não foi enviado----------(emailHtmlInspecao())------------------");
			Messages.addGlobalError("Erro ao enviar E-mail.");
		}

	}
	
	public void emailHtmlInspecaoDiariaBloqueioPorCritical(String resultado, String titulo, String chave, String origemEMail,
			String destinoEmail) {

		try {

			// resultado = "<html>The apache logo Office- </html>";

			// Create the email message
			HtmlEmail email = new HtmlEmail();

			email.setDebug(true);
			email.setHostName(SMTP_365);
			email.setAuthenticator(new DefaultAuthenticator(origemEMail, chave));
			email.addTo(destinoEmail, "HELIO SANTANDER");
			email.setFrom(origemEMail, APELIDO);
			email.setSubject(titulo);
			email.setStartTLSEnabled(true);

			String textoHtml = CorpoHtmlInspecao.bodyHTMLEntreDiariaDevBloqueioPorCritical(resultado);

			// set the html message
			email.setHtmlMsg(textoHtml);

			// set the alternative message
			email.setTextMsg("Seu E-mail não suporta mensagem no formato HTML (OFFICE ).");

			// send the email
			email.send();
			Messages.addGlobalInfo("E-mail enviado.");

		} catch (Exception e) {
			System.out.println("Email office-365 não foi enviado----------(emailHtmlInspecao())------------------");
			Messages.addGlobalError("Erro ao enviar E-mail.");
		}

	}

	public void emailHtmlInspecaoDiaria(String resultado, String titulo, String chave, String origemEMail,
			String destinoEmail) {

		try {

			// resultado = "<html>The apache logo Office- </html>";

			// Create the email message
			HtmlEmail email = new HtmlEmail();

			email.setDebug(true);
			email.setHostName(SMTP_365);
			email.setAuthenticator(new DefaultAuthenticator(origemEMail, chave));
			email.addTo(destinoEmail, "HELIO SANTANDER");
			email.setFrom(origemEMail, APELIDO);
			email.setSubject(titulo);
			email.setStartTLSEnabled(true);

			String textoHtml = CorpoHtmlInspecao.bodyHTMLEntreDiariaDev(resultado);

			// set the html message
			email.setHtmlMsg(textoHtml);

			// set the alternative message
			email.setTextMsg("Seu E-mail não suporta mensagem no formato HTML (OFFICE ).");

			// send the email
			email.send();
			Messages.addGlobalInfo("E-mail enviado.");

		} catch (Exception e) {
			System.out.println("Email office-365 não foi enviado----------(emailHtmlInspecao())------------------");
			Messages.addGlobalError("Erro ao enviar E-mail.");
		}

	}

	public void emailHtmlGmail(String resultadoHtml, String titulo, String origemGmail, String destinoEmail) {

		try {

			// resultadoHtml = "<html>The apache logo Gmail - </html>"; // Não recomendado
			// pelo sonar
			// Create the email message
			HtmlEmail email = new HtmlEmail();

			email.setDebug(true);
			email.setHostName(SMTP_GMAIL);
			email.setSslSmtpPort("465");
			email.setSmtpPort(587);
			email.setAuthenticator(new DefaultAuthenticator(origemGmail, "senha"));
			email.addTo(destinoEmail, "Helio Franca");
			email.setFrom(origemGmail, APELIDO);
			email.setSubject(titulo);
			email.setStartTLSEnabled(true);

			String textoHtml = CorpoHtmlGIT.bodyHTML(resultadoHtml);

			// set the html message
			email.setHtmlMsg(textoHtml);

			// set the alternative message
			email.setTextMsg("Seu E-mail não suporta mensagem no formato HTML. (GMAIL)");

			// send the email
			email.send();

		} catch (Exception e) {
			System.out.println("Email Gmail não foi enviado----------------------------");
		}
		// Cria o e-mail
	}
	
	/**
	 * Envia e-mail para inspeções genéricas
	 */
	public static void enviarInspecoesGenericas(Email email) {
		try {

			HtmlEmail htmlEmail = new HtmlEmail();

			htmlEmail.setDebug(true);
			htmlEmail.setHostName(SMTP_365);
			htmlEmail.setAuthenticator(new DefaultAuthenticator(email.getEmailOrigem(), email.getChave()));
			htmlEmail.addTo(email.getEmailDestino(), "HELIO SANTANDER");
			htmlEmail.setFrom(email.getEmailOrigem(), APELIDO);
			htmlEmail.setSubject(email.getTitulo());
			htmlEmail.setStartTLSEnabled(true);

			String textoHtml = email.getResultadoHtml();

			// set the html message
			htmlEmail.setHtmlMsg(textoHtml);

			// set the alternative message
			htmlEmail.setTextMsg("Seu E-mail não suporta mensagem no formato HTML.");

			// send the email
			htmlEmail.send();
			Messages.addGlobalInfo("E-mail enviado.");
			
		} catch (Exception e) {
			log.error("Erro ao enviar E-mail "+ e.getStackTrace());
			Messages.addGlobalError("Erro ao enviar E-mail.");
		}
	}

}
