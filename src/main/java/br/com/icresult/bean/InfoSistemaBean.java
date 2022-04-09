package br.com.icresult.bean;

import javax.faces.bean.ManagedBean;

/**
 * -Classe BEAN InfoSistemaBean.
 * 
 * @author helio.franca
 * @version v2.5.6
 * @since 23-11-2018
 *
 */

@ManagedBean
public class InfoSistemaBean {

	private static String nomeAplicacao = "IC-Result";
	private static String versaoAplicacao = "Versão 3.0.0";

	// --------------------------------------------------------------------------------

	public String getNomeAplicacao() {
		return nomeAplicacao;
	}

	public String getVersaoAplicacao() {
		return versaoAplicacao;
	}

	// --------------------------------------------------------------------------------

}
