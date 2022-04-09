package br.com.icresult.email;

import java.text.DecimalFormat;

import br.com.icresult.dao.complementos.ControleSiglasDAO;
import br.com.icresult.domain.complementos.Analise_Dev_Diario;
import br.com.icresult.tratamentos.TrataDados;

/**
 * Classe criada p/ gerar linhas html(td), para inclusão em uma tabela HTML.
 *
 * @author helio.franca
 * @version V2.4.0
 * @since 27-09-2018
 *
 */
public class EntregaDiariaDevListBloqueioCritical implements EntregaDiariaDevList {

	/**
	 * 
	 * @param obj - Recebe um objeto
	 * 
	 * @return - Retorna uma linha para tabela HTML
	 * 
	 */
	public StringBuilder alertaInspecao(Analise_Dev_Diario obj) {

		StringBuilder linhasTabela = new StringBuilder();
		StringBuilder estiloH2 = new StringBuilder();
		ControleSiglasDAO controleSiglasDAO = new ControleSiglasDAO();

		String finalTD = "&ensp; </td>";

		estiloH2.append("margin:5px;");
		estiloH2.append("color:green;");
		estiloH2.append("font:12px;");
		estiloH2.append("padding:5px;");

		StringBuilder estiloH3 = new StringBuilder();
		estiloH3.append("padding:auto;");
		estiloH3.append("margin-left:10px;");
		estiloH3.append("color:red;");
		estiloH3.append("font:12px;");
		estiloH3.append("padding-left:10px;");
		StringBuilder corResultado;

		String resultado = obj.getResultado().equalsIgnoreCase("Alerta") ? "Bloqueado" : obj.getResultado();

		if (resultado.equalsIgnoreCase("Liberado")) {
			corResultado = new StringBuilder("style='color:#12d812;font-size:11px;text-align:center'");
		} else {
			if (resultado.equalsIgnoreCase("Alerta")) {
				corResultado = new StringBuilder("style='color:#fc6705;font-size:11px;text-align:center'");
			} else {
				// Inspeção bloqueada.
				corResultado = new StringBuilder("style='color:red;font-size:11px;text-align:center'");
			}
		}
		linhasTabela.append(" <tr>");
		linhasTabela.append(TrataDados.incluirHtmlTdSemEspacosComTextoCentralizado(obj.getSigla())); // Sigla
		linhasTabela.append(TrataDados.incluirHtmlTdSemEspacosComTextoCentralizado(
				controleSiglasDAO.buscaGestorNivel1PorSigla(obj.getSigla()))); // Gestor
		// N1
		linhasTabela.append(TrataDados.incluirHtmlTdSemEspacosComTextoCentralizado(
				controleSiglasDAO.buscaGestorEntregaENivel2PorSigla(obj.getSigla()))); // Gestor
		// N2

		linhasTabela.append("<td " + corResultado.toString() + "> &ensp; " + resultado + finalTD);// Resultado
		linhasTabela.append(TrataDados.incluirHtmlTdSemEspacosComTextoCentralizado(obj.getNotaProjeto() + "%")); // Nota
		linhasTabela.append(TrataDados.incluirHtmlTdSemEspacosComTextoCentralizado(defineMilhar(obj.getLinhaCodigo())));// Linha
																														// de
																														// código
		linhasTabela.append(TrataDados.incluirHtmlTdSemEspacosComTextoCentralizado(defineMilhar(obj.getIssuesAlta())));// Critical
		linhasTabela.append(
				TrataDados.incluirHtmlTdSemEspacosComTextoCentralizado(defineMilhar(obj.getCriticalReferencia())));// Critical
																													// Ref
		linhasTabela.append("<td style='background-color:#dee0e2;text-align:center'>  &ensp;"
				+ defineMilhar(obj.getIssuesAlta() - obj.getCriticalReferencia()) + finalTD);// Diferença
		// Critical
		linhasTabela
				.append(TrataDados.incluirHtmlTdSemEspacosComTextoCentralizado(defineMilhar(obj.getIssuesMuitoAlta())));// Blocker
		linhasTabela.append(
				TrataDados.incluirHtmlTdSemEspacosComTextoCentralizado(defineMilhar(obj.getBlockerReferencia())));// Blocker
																													// Ref

		linhasTabela.append("<td style='background-color:#dee0e2;text-align:center'>  &ensp;"
				+ defineMilhar(obj.getIssuesMuitoAlta() - obj.getBlockerReferencia()) + finalTD);// Diferença
																									// Blocker

		linhasTabela.append(TrataDados.incluirHtmlTdSemEspacosComTextoCentralizado(
				obj.getPadraoNomeSonar() == null ? obj.getNomeProjeto() : obj.getPadraoNomeSonar())); // Projeto
		linhasTabela.append(TrataDados.incluirHtmlTdLink(obj.getUrl())); // URL

		linhasTabela.append(TrataDados.incluirHtmlTdSemEspacosComTextoCentralizado(
				TrataDados.tratarDataHoraTextoTraco(obj.getDataCaptura()))); // --------
		linhasTabela.append("</tr>");

		return linhasTabela;
	}

	private String defineMilhar(Integer numeroInteiro) {
		Double numeroConvertidoDouble = Double.parseDouble(numeroInteiro.toString());
		DecimalFormat df = new DecimalFormat("###,###,###,###,###");
		return df.format(numeroConvertidoDouble);
	}

}
