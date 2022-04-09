package br.com.icresult.model;

import java.util.Date;

/**
 * Classe utilizada para padronizar as capturas
 * 
 * @author andre.graca
 *
 */

public class Captura {

	private String url;

	private Date dataSonar;

	private String versao;

	private Date dataCaptura;

	private String nomeProjeto;

	private int linhaCodigo;

	private Integer novasLinhasCodigo;

	private int linhaCodigoAnt;

	private int bugs;

	private int codeSmell;

	private int vulnerabilidades;

	private int issuesMuitoAlta;

	private Integer novasIssuesMuitoAlta;

	private int issuesAlta;

	private Integer novasIssuesAlta;

	private int issuesMedia;

	private Integer novasIssuesMedia;

	private int issuesBaixa;

	private int issuesMuitoBaixa;

	private int vulnerabilityMuitoAlta;

	private int vulnerabilityAlta;

	private int vulnerabilityMedia;

	private int vulnerabilityBaixa;

	private int vulnerabilityMuitoBaixa;

	private String debitoTecnico;

	private String debitoTecnicoMinutos;

	private String cobertura;

	private String novaCobertura;

	private String branch;

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

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

	public String getVersao() {
		return versao;
	}

	public void setVersao(String versao) {
		this.versao = versao;
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

	public Integer getNovasLinhasCodigo() {
		return novasLinhasCodigo;
	}

	public void setNovasLinhasCodigo(Integer novasLinhasCodigo) {
		this.novasLinhasCodigo = novasLinhasCodigo;
	}

	public int getLinhaCodigoAnt() {
		return linhaCodigoAnt;
	}

	public void setLinhaCodigoAnt(int linhaCodigoAnt) {
		this.linhaCodigoAnt = linhaCodigoAnt;
	}

	public int getBugs() {
		return bugs;
	}

	public void setBugs(int bugs) {
		this.bugs = bugs;
	}

	public int getCodeSmell() {
		return codeSmell;
	}

	public void setCodeSmell(int codeSmell) {
		this.codeSmell = codeSmell;
	}

	public int getVulnerabilidades() {
		return vulnerabilidades;
	}

	public void setVulnerabilidades(int vulnerabilidades) {
		this.vulnerabilidades = vulnerabilidades;
	}

	public int getIssuesMuitoAlta() {
		return issuesMuitoAlta;
	}

	public void setIssuesMuitoAlta(int issuesMuitoAlta) {
		this.issuesMuitoAlta = issuesMuitoAlta;
	}

	public Integer getNovasIssuesMuitoAlta() {
		return novasIssuesMuitoAlta;
	}

	public void setNovasIssuesMuitoAlta(Integer novasIssuesMuitoAlta) {
		this.novasIssuesMuitoAlta = novasIssuesMuitoAlta;
	}

	public int getIssuesAlta() {
		return issuesAlta;
	}

	public void setIssuesAlta(int issuesAlta) {
		this.issuesAlta = issuesAlta;
	}

	public Integer getNovasIssuesAlta() {
		return novasIssuesAlta;
	}

	public void setNovasIssuesAlta(Integer novasIssuesAlta) {
		this.novasIssuesAlta = novasIssuesAlta;
	}

	public int getIssuesMedia() {
		return issuesMedia;
	}

	public void setIssuesMedia(int issuesMedia) {
		this.issuesMedia = issuesMedia;
	}

	public Integer getNovasIssuesMedia() {
		return novasIssuesMedia;
	}

	public void setNovasIssuesMedia(Integer novasIssuesMedia) {
		this.novasIssuesMedia = novasIssuesMedia;
	}

	public int getIssuesBaixa() {
		return issuesBaixa;
	}

	public void setIssuesBaixa(int issuesBaixa) {
		this.issuesBaixa = issuesBaixa;
	}

	public int getIssuesMuitoBaixa() {
		return issuesMuitoBaixa;
	}

	public void setIssuesMuitoBaixa(int issuesMuitoBaixa) {
		this.issuesMuitoBaixa = issuesMuitoBaixa;
	}

	public int getVulnerabilityMuitoAlta() {
		return vulnerabilityMuitoAlta;
	}

	public void setVulnerabilityMuitoAlta(int vulnerabilityMuitoAlta) {
		this.vulnerabilityMuitoAlta = vulnerabilityMuitoAlta;
	}

	public int getVulnerabilityAlta() {
		return vulnerabilityAlta;
	}

	public void setVulnerabilityAlta(int vulnerabilityAlta) {
		this.vulnerabilityAlta = vulnerabilityAlta;
	}

	public int getVulnerabilityMedia() {
		return vulnerabilityMedia;
	}

	public void setVulnerabilityMedia(int vulnerabilityMedia) {
		this.vulnerabilityMedia = vulnerabilityMedia;
	}

	public int getVulnerabilityBaixa() {
		return vulnerabilityBaixa;
	}

	public void setVulnerabilityBaixa(int vulnerabilityBaixa) {
		this.vulnerabilityBaixa = vulnerabilityBaixa;
	}

	public int getVulnerabilityMuitoBaixa() {
		return vulnerabilityMuitoBaixa;
	}

	public void setVulnerabilityMuitoBaixa(int vulnerabilityMuitoBaixa) {
		this.vulnerabilityMuitoBaixa = vulnerabilityMuitoBaixa;
	}

	public String getDebitoTecnico() {
		return debitoTecnico;
	}

	public void setDebitoTecnico(String debitoTecnico) {
		this.debitoTecnico = debitoTecnico;
	}

	public String getDebitoTecnicoMinutos() {
		return debitoTecnicoMinutos;
	}

	public void setDebitoTecnicoMinutos(String debitoTecnicoMinutos) {
		this.debitoTecnicoMinutos = debitoTecnicoMinutos;
	}

	public String getCobertura() {
		return cobertura;
	}

	public void setCobertura(String cobertura) {
		this.cobertura = cobertura;
	}

	public String getNovaCobertura() {
		return novaCobertura;
	}

	public void setNovaCobertura(String novaCobertura) {
		this.novaCobertura = novaCobertura;
	}

	@Override
	public String toString() {
		return "Captura [" + (url != null ? "url=" + url + ", " : "")
				+ (dataSonar != null ? "dataSonar=" + dataSonar + ", " : "")
				+ (versao != null ? "versao=" + versao + ", " : "")
				+ (dataCaptura != null ? "dataCaptura=" + dataCaptura + ", " : "")
				+ (nomeProjeto != null ? "nomeProjeto=" + nomeProjeto + ", " : "") + "linhaCodigo=" + linhaCodigo + ", "
				+ (novasLinhasCodigo != null ? "novasLinhasCodigo=" + novasLinhasCodigo + ", " : "") + "linhaCodigoAnt="
				+ linhaCodigoAnt + ", bugs=" + bugs + ", codeSmell=" + codeSmell + ", vulnerabilidades="
				+ vulnerabilidades + ", issuesMuitoAlta=" + issuesMuitoAlta + ", "
				+ (novasIssuesMuitoAlta != null ? "novasIssuesMuitoAlta=" + novasIssuesMuitoAlta + ", " : "")
				+ "issuesAlta=" + issuesAlta + ", "
				+ (novasIssuesAlta != null ? "novasIssuesAlta=" + novasIssuesAlta + ", " : "") + "issuesMedia="
				+ issuesMedia + ", " + (novasIssuesMedia != null ? "novasIssuesMedia=" + novasIssuesMedia + ", " : "")
				+ "issuesBaixa=" + issuesBaixa + ", issuesMuitoBaixa=" + issuesMuitoBaixa + ", vulnerabilityMuitoAlta="
				+ vulnerabilityMuitoAlta + ", vulnerabilityAlta=" + vulnerabilityAlta + ", vulnerabilityMedia="
				+ vulnerabilityMedia + ", vulnerabilityBaixa=" + vulnerabilityBaixa + ", vulnerabilityMuitoBaixa="
				+ vulnerabilityMuitoBaixa + ", "
				+ (debitoTecnico != null ? "debitoTecnico=" + debitoTecnico + ", " : "")
				+ (debitoTecnicoMinutos != null ? "debitoTecnicoMinutos=" + debitoTecnicoMinutos + ", " : "")
				+ (cobertura != null ? "cobertura=" + cobertura + ", " : "")
				+ (novaCobertura != null ? "novaCobertura=" + novaCobertura : "") + "]";
	}

}
