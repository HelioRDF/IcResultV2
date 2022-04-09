package br.com.icresult.domain.complementos;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * -Classe POJO Analise_HK, e entity do DB via Hibernate.
 * 
 * @author helio.franca
 * @version v2.0.7
 * @since 30-07-2018
 * 
 */

@Entity
public class Analise_HK implements Serializable {

	private static final long serialVersionUID = 3440210707050332945L;

	@Column(nullable = false, name = "ID")
	@Id
	private int id;

	@Column(name = "URL", length = 500)
	private String url;

	@Column(name = "Painel_Gestor", length = 500)
	private String painelGestor;

	@Column(name = "Sigla")
	private String sigla;

	@Column(name = "DataCommit")
	private String dataCommit;

	@Column(name = "Data")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataSonar;

	@Column(name = "Versao", length = 500)
	private String versao;

	@Column(name = "DataCaptura")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataCaptura;

	@Column(name = "Nome_Projeto", length = 500)
	private String nomeProjeto;

	@Column(name = "Linhas_Codigo")
	private int linhaCodigo;

	@Column(name = "Bugs")
	private int bugs;

	@Column(name = "Code_Smells")
	private int codeSmall;

	@Column(name = "Vulnerabilidades", nullable = true)
	private int vulnerabilidades;

	@Column(name = "Issues_Severity_Muito_Alta")
	private int issuesMuitoAlta;

	@Column(name = "Issues_Severity_Alta")
	private int issuesAlta;

	@Column(name = "Issues_Severity_Media")
	private int issuesMedia;

	@Column(name = "Issues_Severity_Baixa")
	private int issuesBaixa;

	@Column(name = "Issues_Severity_Muito_Baixa")
	private int issuesMuitoBaixa;

	@Column(name = "Vulnerability_Severity_Muito_Alta", nullable = true)
	private int vulnerabilityMuitoAlta;

	@Column(name = "Vulnerability_Severity_Alta", nullable = true)
	private int vulnerabilityAlta;

	@Column(name = "Vulnerability_Severity_Media", nullable = true)
	private int vulnerabilityMedia;

	@Column(name = "Vulnerability_Severity_Baixa", nullable = true)
	private int vulnerabilityBaixa;

	@Column(name = "Vulnerability_Severity_Muito_Baixa", nullable = true)
	private int vulnerabilityMuitoBaixa;

	@Column(name = "Debito_Tecnico")
	private String debitoTecnico;

	@Column(name = "Esforco_Confiabilidade")
	private String esforcoConfiabilidade;

	@Column(name = "Esforco_Seguranca")
	private String esforcoSeguranca;

	@Column(name = "Cobertura")
	private String cobertura;

	@Column(name = "Nota_Projeto")
	private String notaProjeto;

	@Column(name = "Nota_Anterior")
	private String notaAnterior;

	@Column(name = "Resultado")
	private String resultado;

	@Column(name = "CodigoAlteracao")
	private String codigoAlterado;

	@Column(name = "Debito_Tecnico_Minutos")
	private String debitoTecnicoMinutos;

	// --------------------------------------------------

	public String getUrl() {
		return url;
	}

	public String getDebitoTecnicoMinutos() {
		return debitoTecnicoMinutos;
	}

	public void setDebitoTecnicoMinutos(String debitoTecnicoMinutos) {
		this.debitoTecnicoMinutos = debitoTecnicoMinutos;
	}

	public String getCodigoAlterado() {
		return codigoAlterado;
	}

	public void setCodigoAlterado(String codigoAlterado) {
		this.codigoAlterado = codigoAlterado;
	}

	public String getNotaAnterior() {
		return notaAnterior;
	}

	public void setNotaAnterior(String notaAnterior) {
		this.notaAnterior = notaAnterior;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPainelGestor() {
		return painelGestor;
	}

	public void setPainelGestor(String painelGestor) {
		this.painelGestor = painelGestor;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
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

	public int getBugs() {
		return bugs;
	}

	public void setBugs(int bugs) {
		this.bugs = bugs;
	}

	public int getCodeSmall() {
		return codeSmall;
	}

	public void setCodeSmall(int codeSmall) {
		this.codeSmall = codeSmall;
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

	public String getEsforcoConfiabilidade() {
		return esforcoConfiabilidade;
	}

	public void setEsforcoConfiabilidade(String esforcoConfiabilidade) {
		this.esforcoConfiabilidade = esforcoConfiabilidade;
	}

	public String getEsforcoSeguranca() {
		return esforcoSeguranca;
	}

	public void setEsforcoSeguranca(String esforcoSeguranca) {
		this.esforcoSeguranca = esforcoSeguranca;
	}

	public String getCobertura() {
		return cobertura;
	}

	public void setCobertura(String cobertura) {
		this.cobertura = cobertura;
	}

	public String getNotaProjeto() {
		return notaProjeto;
	}

	public void setNotaProjeto(String notaProjeto) {
		this.notaProjeto = notaProjeto;
	}

	public String getResultado() {
		return resultado;
	}

	public void setResultado(String resultado) {
		this.resultado = resultado;
	}

	public String getDataCommit() {
		return dataCommit;
	}

	public void setDataCommit(String dataCommit) {
		this.dataCommit = dataCommit;
	}

}
