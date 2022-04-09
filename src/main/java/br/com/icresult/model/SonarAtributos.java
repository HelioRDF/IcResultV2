package br.com.icresult.model;

import java.util.Date;

/**
 * Classe utilizada para padronizar as capturas
 * 
 * @author helio.franca
 * @version v3.0.0
 * @since 12-06-2019
 *
 */

public class SonarAtributos {

	private String url;

	private Date dataSonar;

	private Date dataCaptura;

	private String nomeProjeto;

	private int linhaCodigo;

	private int issuesMuitoAlta;

	private int issuesAlta;

	private int issuesMedia;

	private int novasIssuesMuitoAlta;

	private int novasIssuesAlta;

	private int novasIssuesMedia;

	private String coberturaCodigo;

	private String novaCobertura;
	
	private String linkIntegracaoContinua;
	
	private String debitoTecnicoEmMinutos; 

	// --------------- Get e Set
	// ------------------------------------------------------------------

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Date getDataSonar() {
		return dataSonar;
	}

	public void setDataSonar(Date dataSonar) {
		this.dataSonar = dataSonar;
	}

	public Date getDataCaptura() {
		return dataCaptura;
	}

	public void setDataCaptura(Date dataCaptura) {
		this.dataCaptura = dataCaptura;
	}

	public String getNomeProjeto() {
		return nomeProjeto;
	}

	public void setNomeProjeto(String nomeProjeto) {
		this.nomeProjeto = nomeProjeto;
	}

	public int getLinhaCodigo() {
		return linhaCodigo;
	}

	public void setLinhaCodigo(int linhaCodigo) {
		this.linhaCodigo = linhaCodigo;
	}

	public int getIssuesMuitoAlta() {
		return issuesMuitoAlta;
	}

	public void setIssuesMuitoAlta(int issuesMuitoAlta) {
		this.issuesMuitoAlta = issuesMuitoAlta;
	}

	public int getIssuesAlta() {
		return issuesAlta;
	}

	public void setIssuesAlta(int issuesAlta) {
		this.issuesAlta = issuesAlta;
	}

	public int getIssuesMedia() {
		return issuesMedia;
	}

	public void setIssuesMedia(int issuesMedia) {
		this.issuesMedia = issuesMedia;
	}

	// --------------- ToString
	// ------------------------------------------------------------------

	@Override
	public String toString() {
		return "SonarAtributos [url=" + url + ", dataSonar=" + dataSonar + ", dataCaptura=" + dataCaptura
				+ ", nomeProjeto=" + nomeProjeto + ", linhaCodigo=" + linhaCodigo + ", issuesMuitoAlta="
				+ issuesMuitoAlta + ", issuesAlta=" + issuesAlta + ", issuesMedia=" + issuesMedia + "]";
	}

	public int getNovasIssuesMuitoAlta() {
		return novasIssuesMuitoAlta;
	}

	public void setNovasIssuesMuitoAlta(int novasIssuesMuitoAlta) {
		this.novasIssuesMuitoAlta = novasIssuesMuitoAlta;
	}

	public int getNovasIssuesAlta() {
		return novasIssuesAlta;
	}

	public void setNovasIssuesAlta(int novasIssuesAlta) {
		this.novasIssuesAlta = novasIssuesAlta;
	}

	public int getNovasIssuesMedia() {
		return novasIssuesMedia;
	}

	public void setNovasIssuesMedia(int novasIssuesMedia) {
		this.novasIssuesMedia = novasIssuesMedia;
	}

	public String getCoberturaCodigo() {
		return coberturaCodigo;
	}

	public void setCoberturaCodigo(String coberturaCodigo) {
		this.coberturaCodigo = coberturaCodigo;
	}

	public void setNovaCobertura(String novaCobertura) {
		this.novaCobertura = novaCobertura;
	}

	public String getNovaCobertura() {
		return novaCobertura;
	}
	
	public String getLinkIntegracaoContinua() {
		return linkIntegracaoContinua;
	}
	
	public void setLinkIntegracaoContinua(String linkIntegracaoContinua) {
		this.linkIntegracaoContinua = linkIntegracaoContinua;
	}

	public String getDebitoTecnicoEmMinutos() {
		return debitoTecnicoEmMinutos;
	}
	
	public void setDebitoTecnicoEmMinutos(String debitoTecnicoEmMinutos) {
		this.debitoTecnicoEmMinutos = debitoTecnicoEmMinutos;
	}
}
