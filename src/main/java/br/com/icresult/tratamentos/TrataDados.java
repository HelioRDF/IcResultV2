package br.com.icresult.tratamentos;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 
 * @author helio.franca
 * @version v2.4.0
 * @since 31-10-2019
 * 
 */
public class TrataDados {

	/**
	 * Recebe uma data do tipo String no formato "2018-09-28 03:00:00.000" e retorna
	 * uma String formatada "28-09-2018"
	 * 
	 * @param dataTexto - String para ser convertida.
	 * @return
	 */
	public static String tratarDataTextoTraco(String dataTexto) {

		if (dataTexto.length() >= 10) {
			dataTexto = dataTexto.substring(0, 11);
			String varData[] = dataTexto.split("-");
			dataTexto = varData[2].trim() + "-" + varData[1].trim() + "-" + varData[0].trim();

		} else {
			dataTexto = "N/A";
		}

		return dataTexto;
	}

	public static String tratarDataHoraTextoTraco(Date dataCaptura) {

		LocalDate retorno = dataCaptura.toInstant().atZone(ZoneId.of("Etc/GMT+3")).toLocalDate();

		DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		return retorno.format(formatador);
	}

// ------------------------------------------------------------------------
	/**
	 * Retorna uma coluna TD do tipo Link para tabela HTML
	 * 
	 * @param informacao - String de texto com os dados da Coluna.
	 * @return Retorna um Objeto String
	 */
	public static String incluirHtmlTdLink(String informacao) {
		String linha = "<td style='font-size:11px;' > &ensp;<a href=" + informacao + ">Link Sonar  </a> &ensp; </td>";

		return linha;
	}

	// ------------------------------------------------------------------------
	/**
	 * Retorna uma coluna TD para tabela HTML
	 * 
	 * @param informacao - String de texto com os dados da Coluna.
	 * @return Retorna um Objeto String
	 */
	public static String incluirHtmlTd(String informacao) {
		String linha = "<td style='font-size:11px;' > &ensp;" + informacao + " &ensp; </td>";
		return linha;
	}

	// ------------------------------------------------------------------------
	/**
	 * Retorna uma coluna TD para tabela HTML sem espaços nas estremidades
	 * 
	 * @param informacao - String de texto com os dados da Coluna.
	 * @return Retorna um Objeto String
	 */
	public static String incluirHtmlTdSemEspacosComTextoCentralizado(String informacao) {
		String linha = "<td style='font-size:11px;text-align: center' >" + informacao + "</td>";
		return linha;
	}

	// ------------------------------------------------------------------------
	/**
	 * Retorna uma coluna TD para tabela HTML com CSS
	 * 
	 * @param informacao  - String de texto com os dados da Coluna.
	 * @param txtStyleCss - String com argumento css
	 * @param simbolo     - String com simbolos que representa os dados, exemplo
	 *                    '%', '$'
	 * @return Retorna um Objeto String
	 */
	public static String incluirHtmlTdStyle(String informacao, String txtStyleCss, String simbolo) {
		String linha = "<td " + txtStyleCss + " > &ensp;" + informacao + simbolo + " &ensp; </td>";
		return linha;
	}

	/**
	 * Adiciona uma linha td HTML com título da tabela
	 * 
	 * @return - Retorna um StringBuffer
	 */
	public static StringBuffer tituloColunaHtml() {

		StringBuffer msgs = new StringBuffer();
		msgs.append(
				"<tr><td 	style='background-color:#2f75b5; font-size:13px; color:white'>&ensp; Gestor de Testes  &ensp;</td>");
		msgs.append("<td 	style='background-color:#2f75b5; font-size:13px;color:white'>&ensp; Cód.RFC   &ensp;</td>");
		msgs.append("<td 	style='background-color:#2f75b5; font-size:13px;color:white'>&ensp; Sigla  &ensp;</td>");
		msgs.append(
				"<td 	style='background-color:#2f75b5; font-size:13px;color:white'>&ensp; Gestor Sigla  &ensp;</td>"); // Gestor
																															// de
																															// Entrega
																															// do
																															// painel
																															// Sonar
		msgs.append(
				"<td 	style='background-color:#2f75b5; font-size:13px;color:white'>&ensp; Dt Inspeção &ensp;</td>"); // Data
																														// Captura
																														// Sonar
		msgs.append(
				"<td 	style='background-color:#2f75b5; font-size:13px;color:white'>&ensp; Dt.Alt.Cód   &ensp;</td>");
		msgs.append("<td 	style='background-color:#2f75b5; font-size:13px;color:white'>&ensp; Dt.Pro   &ensp;</td>");
		msgs.append(
				"<td 	style='background-color:#2f75b5; font-size:13px;color:white'>&ensp; Linhas.Cód  &ensp;</td>");
		msgs.append("<td 	style='background-color:#2f75b5; font-size:13px;color:white'>&ensp; Blocker  &ensp;</td>");
		msgs.append("<td 	style='background-color:#2f75b5; font-size:13px;color:white'>&ensp; Critical  &ensp;</td>");
		msgs.append(
				"<td 	style='background-color:#2f75b5; font-size:13px;color:white'>&ensp; Resultado   &ensp;</td></tr>");

		return msgs;
	}

	/**
	 * Adiciona uma linha td HTML
	 * 
	 * @return - Retorna um StringBuffer
	 */
	public static StringBuffer linhaColunaHtml() {

		StringBuffer msgs = new StringBuffer();
		msgs.append("<tr><td style='background-color:white; font-size:13px;text-align:center;''>---</td>");
		msgs.append("<td style='background-color:white; font-size:13px; text-align:center;'>---</td>");
		msgs.append("<td style='background-color:white; font-size:13px; text-align:center;'>---</td>");
		msgs.append("<td style='background-color:white; font-size:13px; text-align:center;'>---</td>");
		msgs.append("<td style='background-color:white; font-size:13px; text-align:center;'>---</td>");
		msgs.append("<td style='background-color:white; font-size:13px; text-align:center;'>---</td>");
		msgs.append("<td style='background-color:white; font-size:13px; text-align:center;'>---</td>");
		msgs.append("<td style='background-color:white; font-size:13px; text-align:center;'>---</td>");
		msgs.append("<td style='background-color:white; font-size:13px; text-align:center;'>---</td>");
		msgs.append("<td style='background-color:white; font-size:13px; text-align:center;'>---</td>");
		msgs.append("<td style='background-color:white; font-size:13px; text-align:center;'>---</td> </tr>");

		return msgs;
	}

}