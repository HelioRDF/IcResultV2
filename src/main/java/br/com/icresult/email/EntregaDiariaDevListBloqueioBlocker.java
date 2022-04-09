package br.com.icresult.email;

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
public class EntregaDiariaDevListBloqueioBlocker implements EntregaDiariaDevList {

	/**
	 * 
	 * @param obj - Recebe um objeto
	 * 
	 * @return - Retorna uma linha para tabela HTML
	 * 
	 * @deprecated
	 * 
	 */
	public StringBuilder alertaInspecao(Analise_Dev_Diario obj) {

		StringBuilder linhasTabela = new StringBuilder();
		StringBuilder estiloH2 = new StringBuilder();

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
		
		if (obj.getResultado().equalsIgnoreCase("Liberado")) {
			corResultado = new StringBuilder("style='color:#12d812;font-size:11px;'");
		} else {
			corResultado = new StringBuilder("style='color:red;font-size:11px;'");

		}
		linhasTabela.append(" <tr>");
		linhasTabela.append(TrataDados.incluirHtmlTd(obj.getSigla())); // Sigla
		linhasTabela.append(TrataDados.incluirHtmlTd(obj.getPainelGestor())); // Gestor
		linhasTabela.append("<td " + corResultado.toString() + "> &ensp; " + obj.getResultado() + "&ensp; </td>");
		linhasTabela.append(TrataDados.incluirHtmlTd(obj.getNotaProjeto()+"%")); // Nota
		linhasTabela.append(TrataDados.incluirHtmlTd(Integer.toString(obj.getLinhaCodigo())));
		linhasTabela.append(TrataDados.incluirHtmlTd(Integer.toString(obj.getIssuesMuitoAlta())));
		linhasTabela.append(TrataDados.incluirHtmlTd(Integer.toString(obj.getBlockerReferencia())));
		linhasTabela.append(TrataDados.incluirHtmlTd(obj.getNomeProjeto())); // Projeto
		linhasTabela.append(TrataDados.incluirHtmlTdLink(obj.getUrl())); // URL
//		linhasTabela
//				.append(TrataDados.incluirHtmlTd(TrataDados.tratarDataHoraTextoTraco(obj.getDataCaptura().toString()))); // --------
		linhasTabela.append("</tr>");

		return linhasTabela;
	}

}
