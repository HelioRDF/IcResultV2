package br.com.icresult.html;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * - Classe para estruturar corpo de e-mail HTML
 * 
 * @author helio.franca
 * @version V2.1.3
 * @since 08-08-2018
 */
public class CorpoHtmlInspecao {
	
	private static Logger LOG = LoggerFactory.getLogger(CorpoHtmlInspecao.class);

	/**
	 * Cria o corpo HTML p/ Email
	 * 
	 * @param resultado - String
	 * @param lider     - String
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

		msgs.append("<body style=\"background:#fff;font-family:Courier, monospace; font-size:11px; \">");

		msgs.append("<div>");
		msgs.append("<div>Prezados,</div>");
		msgs.append(
				"<div>Segue abaixo o resultado da inspeção de código referente às RFCs que foram implantados em HK.</div>");
		msgs.append("<br></br>");
		msgs.append(
				"<div>Em adição ao trabalho de inspeção de código do ambiente de Desenvolvimento, estamos realizando uma inspeção prévia no ambiente de Homologação (HK), </div>");
		msgs.append("<div>com base nos repositórios de fontes:</div>");
		msgs.append("<div>- RTC – workspace HK.</div>");
		msgs.append("<div>- GIT – branch Release.</div>");
		msgs.append("<br></br>");
		msgs.append(
				"<div>Com isto, é possível obter a visualização dos apontamentos gerados pela ferramenta SonarQube, com as regras definidas por Arquitetura.</div>");
		msgs.append(
				"<div> Os links e detalhes das inspeções estão disponíveis no Confluence:<a href=\"http://confluence.produbanbr.corp/login.action?os_destination=%2Fpages%2Fviewpage.action%3FpageId%3D32245596&permissionViolation=true\"> CLIQUE AQUI!</a></div>");
		msgs.append("<br></br>");
		msgs.append(
				"<div>A indicação é que os apontamentos blocker sejam eliminados o quanto antes, pois podem causar danos às aplicações em Produção.</div>");
		msgs.append(
				"Neste primeiro momento estamos apenas <span style='color:red;'><b>alertando</b></span>, mas em breve (previsão de 1 mês) passaremos a <b>bloquear</b> os projetos que apresentarem apontamentos <b><i>blocker</i></b> em HK.</div>");

		msgs.append("<br></br>");
		msgs.append("<table border='1'>");
		msgs.append("<tr style=\"background-color:#2f75b5; color:#fff; font-size:13px;\">");

		msgs.append("<td>&ensp; Gestor de Testes  &ensp;</td>");
		msgs.append("<td>&ensp; Cód.RFC   &ensp;</td>");
		msgs.append("<td>&ensp; Sigla  &ensp;</td>");
		msgs.append("<td>&ensp; Gestor Sigla  &ensp;</td>"); // Gestor de Entrega do painel Sonar
		msgs.append("<td>&ensp; Dt Inspeção &ensp;</td>"); // Data Captura Sonar
		msgs.append("<td>&ensp; Dt.Alt.Cód   &ensp;</td>");
		msgs.append("<td>&ensp; Dt.Pro   &ensp;</td>");
		msgs.append("<td>&ensp; Linhas.Cód  &ensp;</td>");
		msgs.append("<td>&ensp; Blocker  &ensp;</td>");
		msgs.append("<td>&ensp; Critical  &ensp;</td>");
		msgs.append("<td>&ensp; Resultado   &ensp;</td></tr>");
		msgs.append(resultado);
		msgs.append("</table>");
		msgs.append(
				"<br></br> <br></br> <br></br>----------------------------------------------------------------------------------------------------------------------------<br></br> ");
		// Critérios
		msgs.append("<div><b>Critérios de Resultado:</b></div>");
		msgs.append("<div>- <span  style='color:green;'>Liberado:</span> Blocker = 0 </div>");
		msgs.append("<div>- <span  style='color:orange;'>Alerta:</span> Blocker > 0 </div>");
		// Blocker
		msgs.append("<div style='margin-top:10px'><b><span  style='color:red;'>Blocker:</span></b></div>");
		msgs.append(
				"<div>- <span> Descrição: Bug com alta probabilidade de impactar o comportamento do aplicativo</span>  </div>");
		msgs.append("<div>- <span> Exemplo: Vazamento de memória, conexão JDBC não Fechada</span>  </div>");
		msgs.append("<div>- <span> Ação: O Código deve ser imediatamente corrigido  </div>");
		// Crítico
		msgs.append("<div style='margin-top:10px'><b><span  style='color:#9c4141'>Crítico:</span></b></div>");
		msgs.append(
				"<div>- <span> Descrição: Erro com baixa probabilidade de impactar o  aplicativo, ou falha de segurança</span>  </div>");
		msgs.append("<div>- <span> Exemplo: Bloco catch Vazio, Injeção SQL</span>  </div>");
		msgs.append("<div>- <span> Ação: O Código deve ser revisto imediatamente   </div>");
		// Dt.Alt.Cód
		msgs.append("<div style='margin-top:10px'><b><span>Dt.Alt.Cód:</span></b></div>");
		msgs.append(
				"<div>- <span> Descrição: Data da última alteração do código fonte no repositório (RTC ou GIT).</span>  </div>");
		// Dt.Pro
		msgs.append("<div style='margin-top:10px'><b><span>Dt.Pro:</span></b></div>");
		msgs.append("<div>- <span> Descrição: Data prevista de implantação da RFC.</span>  </div><br></br><br></br>");
		msgs.append("</div>  " + "</body>");
		msgs.append("</html>");
		return msgs.toString();
	}

	public static String bodyHTMLEntreDiariaDev(String resultado) {
		// embed the image and get the content id

		LocalDateTime dataHoraAgora = LocalDateTime.now();
		DateTimeFormatter formatador = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.valueOf("dd/MM/yyyy"))
				.withLocale(new Locale("pt", "br"));
		String dataHoraAgoraTxt = dataHoraAgora.format(formatador); // 08/04/2014

		StringBuffer msgs = new StringBuffer();
		msgs.append("<!DOCTYPE html>");
		msgs.append("<html>");
		msgs.append("<head>");
		msgs.append("<meta charset=\"utf-8\">");
		msgs.append("</head>");

		msgs.append("<body style=\"background:#fff;font-family:Courier, monospace; font-size:11px; \">");

		msgs.append("<div>");
		msgs.append("<div>Prezados,</div>");
		msgs.append("<br></br>");
		msgs.append("<div>Segue abaixo inspeção para bloqueio em DEV.</div>");
		msgs.append("<div>Data: ");
		msgs.append(dataHoraAgoraTxt + "</div>");
		msgs.append("<br></br>");

		if (resultado.contains("Sem siglas bloqueadas")) {
			msgs.append(resultado);
		} else {
			msgs.append("<table border='1'>");
			msgs.append("<tr style=\"background-color:#2f75b5; color:#fff; font-size:13px;\">");
			msgs.append("<td>&ensp; Sigla  &ensp;</td>");
			msgs.append("<td>&ensp; Gestor  &ensp;</td>");
			msgs.append("<td>&ensp; Resultado   &ensp;</td>");
			msgs.append("<td>&ensp; Nota  &ensp;</td>");
			msgs.append("<td>&ensp; Linhas.Cod  &ensp;</td>");
			msgs.append("<td>&ensp; Blocker  &ensp;</td>");
			msgs.append("<td>&ensp; Blocker.Ref  &ensp;</td>");
			msgs.append("<td>&ensp; Projeto  &ensp;</td>");
			msgs.append("<td>&ensp; URL  &ensp;</td>");
			msgs.append("<td>&ensp; Data.Captura  &ensp;</td>");
			msgs.append("</tr>");

			msgs.append(resultado);
			msgs.append("</table>");
		}
		msgs.append("</div>  " + "</body>");
		msgs.append("</html>");
		return msgs.toString();
	}

	public static String bodyHTMLEntreDiariaDevBloqueioPorCritical(String resultado) {
		// embed the image and get the content id

		StringBuffer msgs = new StringBuffer();
		try {
			
			LocalDateTime dataHoraAgora = LocalDateTime.now();
			DateTimeFormatter formatador = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
			String dataHoraAgoraTxt = dataHoraAgora.format(formatador); // 08/04/2014

			msgs.append("<!DOCTYPE html>");
			msgs.append("<html>");
			msgs.append("<head>");
			msgs.append("<meta charset=\"utf-8\">");
			msgs.append("</head>");

			msgs.append("<body style=\"background:#fff;font-family:Courier, monospace; font-size:11px; \">");

			msgs.append("<div>");
			msgs.append("<div>Prezados,</div>");
			msgs.append("<br></br>");
			msgs.append("<div>Segue abaixo inspeção para bloqueio em DEV.</div>");
			msgs.append("<div>Data: ");
			msgs.append(dataHoraAgoraTxt + "</div>");
			msgs.append("<br></br>");

			if (resultado.contains("Sem siglas bloqueadas")) {
				msgs.append(resultado);
			} else {
				msgs.append("<table border='1'>");
				msgs.append("<tr style=\"background-color:#2f75b5; color:#fff; font-size:13px;\">");
				msgs.append("<td>&ensp; Sigla  &ensp;</td>");
				msgs.append("<td>&ensp; Gestor Nvl. 1 &ensp;</td>");
				msgs.append("<td>&ensp; Gestor Nvl. 2 &ensp;</td>");
				msgs.append("<td>&ensp; Resultado   &ensp;</td>");
				msgs.append("<td>&ensp; Nota  &ensp;</td>");
				msgs.append("<td>&ensp; Linhas.Cod  &ensp;</td>");
				msgs.append("<td>&ensp; Critical  &ensp;</td>");
				msgs.append("<td>&ensp; Critical.Ref  &ensp;</td>");
				msgs.append("<td style='background-color:#f7ad45'>&ensp; Difer.Critical &ensp;</td>");
				msgs.append("<td>&ensp; Blocker  &ensp;</td>");
				msgs.append("<td>&ensp; Blocker.Ref  &ensp;</td>");
				msgs.append("<td style='background-color:red'>&ensp; Difer.Blocker &ensp;</td>");
				msgs.append("<td>&ensp; Projeto  &ensp;</td>");
				msgs.append("<td>&ensp; URL  &ensp;</td>");
				msgs.append("<td>&ensp; Data.Captura  &ensp;</td>");
				msgs.append("</tr>");

				msgs.append(resultado);
				msgs.append("</table>");
			}
			msgs.append("</div>  " + "</body>");
			msgs.append("</html>");
		} catch (Exception e) {
			LOG.error(e.getLocalizedMessage());
		}
		return msgs.toString();
	}
}