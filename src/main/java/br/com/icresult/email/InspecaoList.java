package br.com.icresult.email;

import java.text.SimpleDateFormat;

import br.com.icresult.dao.complementos.AnaliseHKDAO;
import br.com.icresult.domain.complementos.Analise_HK;
import br.com.icresult.domain.complementos.RFC;
import br.com.icresult.tratamentos.TrataDados;
import jxl.common.Logger;

/**
 * Classe criada p/ gerar linhas html(td), para inclusão em uma tabela HTML.
 *
 * @author helio.franca
 * @version V2.4.0
 * @since 27-09-2018
 *
 */
public class InspecaoList {
	
	private static Logger log = Logger.getLogger(InspecaoList.class);

	/**
	 * 
	 * @param obj - Recebe um objeto
	 * 
	 * @return - Retorna uma linha para tabela HTML
	 * 
	 */
	public StringBuilder alertaInspecao(RFC obj) {
		AnaliseHKDAO dao = new AnaliseHKDAO();
		Analise_HK inspecaoObj = dao.buscarPorID(obj.getCodInspecao());

		StringBuilder linhasTabela = new StringBuilder();
		StringBuilder estiloH2 = new StringBuilder();
		String dataCapTxt = inspecaoObj.getDataCaptura().toString();
		String dataCommit = inspecaoObj.getDataCommit();
		StringBuilder notaAnterior = new StringBuilder(inspecaoObj.getNotaAnterior() + "%");

		if (notaAnterior.toString().equalsIgnoreCase("null%") || notaAnterior.toString().equalsIgnoreCase("0%")) {
			notaAnterior = new StringBuilder("N/A");
		}

		try {
			SimpleDateFormat formatar = new SimpleDateFormat("dd-MM-yyyy");
			dataCapTxt = formatar.format(inspecaoObj.getDataCaptura());
			String array[] = dataCommit.split("-");
			dataCommit = array[2] + "-" + array[1] + "-" + array[0];

		} catch (Exception e) {
			log.error("Erro ao validar data de captura: "+e.getMessage());
		}

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
		if (inspecaoObj.getResultado().equalsIgnoreCase("Bloqueado")) {
			corResultado = new StringBuilder("style='color:red;font-size:10px;'");
		} else {
			corResultado = new StringBuilder("style='color:#12d812;font-size:10px;'");
		}

		if (inspecaoObj.getResultado().equalsIgnoreCase("Alerta")) {

			linhasTabela.append(" <tr>");
			linhasTabela.append(TrataDados.incluirHtmlTd(obj.getLider())); // Gestor de Teste
			linhasTabela.append(TrataDados.incluirHtmlTd(obj.getCodRfc())); // RFC
			linhasTabela.append(TrataDados.incluirHtmlTd(obj.getSigla())); // Sigla
			linhasTabela.append(TrataDados.incluirHtmlTd(inspecaoObj.getPainelGestor().toUpperCase()));
			linhasTabela.append(TrataDados.incluirHtmlTd(dataCapTxt));// Data Captura
			linhasTabela.append(TrataDados.incluirHtmlTd(dataCommit));
			linhasTabela.append(TrataDados.incluirHtmlTd(TrataDados.tratarDataTextoTraco(obj.getDataPro()))); // --------
			linhasTabela.append(TrataDados.incluirHtmlTd(Integer.toString(inspecaoObj.getLinhaCodigo())));
			linhasTabela.append(TrataDados.incluirHtmlTd(Integer.toString(inspecaoObj.getIssuesMuitoAlta())));
			linhasTabela.append(TrataDados.incluirHtmlTd(Integer.toString(inspecaoObj.getIssuesAlta())));
			linhasTabela.append("<td " + corResultado + "> &ensp; " + inspecaoObj.getResultado() + "&ensp; </td>");
			linhasTabela.append("</tr>");
		}
		return linhasTabela;
	}

}
