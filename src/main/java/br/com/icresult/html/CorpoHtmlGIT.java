package br.com.icresult.html;

/**
 * - Classe para estruturar corpo de e-mail HTML
 * @author Helio Franca
 * @since 26/06/2018
 * @version 1.2
 */
public class CorpoHtmlGIT {

	/**
	 * Cria o corpo HTML p/ Email
	 * @param resultado - String
	 * @param lider - String
	 * @return - String - String
	 */
	public static String bodyHTML(String resultado) {
		// embed the image and get the content id

		StringBuffer msgs = new StringBuffer();
		msgs.append("<!DOCTYPE html>");
		msgs.append("<html>");
		msgs.append("<head>");
		msgs.append("<meta charset=\"utf-8\">");
		msgs.append("</head>");	

		msgs.append("<body style=\"background:#fff;font-family:Courier, monospace; \">");
		msgs.append("<h4> <img\r\n" + 
				"		// src='http://intranet.rsinet.com.br/sites/default/files/rsi_logo_0.png'\r\n" + 
				"		// width='213' height='59' align='BASELINE' />&ensp;  Monitor automático GIT. </h4> ");
		msgs.append("<div>");
		
		msgs.append(" <br></br> <span> Prezado Gestor(a) ");
		msgs.append(",</span> <br></br>");
		msgs.append("<span>Segue abaixo relação de siglas com atualizações no repositório GIT. </span>  ");

		msgs.append("<br></br>");
		msgs.append("<table border='1'>");
		msgs.append("<tr style=\"background-color:#771111; color:#fff; \"><td>&ensp; ALTERAÇÃO &ensp;  &ensp;</td>");
		msgs.append("<td>&ensp; SIGLA &ensp;  &ensp;</td>");
		msgs.append("<td>&ensp; &ensp; SISTEMA &ensp;  &ensp;</td>");
		msgs.append("<td>&ensp; &ensp; Pacote &ensp;  &ensp;</td>");
		msgs.append("<td>&ensp; COMMIT &ensp;  &ensp; &ensp;</td>");
		msgs.append("<td>&ensp; COMMIT_ANT &ensp;  &ensp;</td></tr>");
		msgs.append(resultado);
		msgs.append("</table>");

		msgs.append("</div>  " + "</body>");

		msgs.append("</html>");

		return msgs.toString();

	}

}
