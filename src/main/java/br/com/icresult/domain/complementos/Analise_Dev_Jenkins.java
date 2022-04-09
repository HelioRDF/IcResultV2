package br.com.icresult.domain.complementos;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.icresult.model.Captura;

/**
 * -Classe POJO Analise_Dev_Jenkins entity do DB via Hibernate.
 * 
 * @author andre
 * @version v1.0
 * @since 10-03-2020
 * 
 */

@Entity
@Table(name = "Analise_Dev_Jenkins")
public class Analise_Dev_Jenkins implements Serializable {

	private static final long serialVersionUID = 3440210707050332945L;

	public static Comparator<Analise_Dev_Jenkins> getComparadorPorDataCaptura() {
		return new Comparator<Analise_Dev_Jenkins>() {
			@Override
			public int compare(Analise_Dev_Jenkins o1, Analise_Dev_Jenkins o2) {
				return -1 * o1.getDataCaptura().compareTo(o2.getDataCaptura());
			}
		};
	}

	public Analise_Dev_Jenkins() {

	}

	public Analise_Dev_Jenkins(Captura captura) {
		this.branch = captura.getBranch();
		this.url = captura.getUrl();
		this.dataSonar = captura.getDataSonar();
		this.versao = captura.getVersao();
		this.dataCaptura = captura.getDataCaptura();
		this.linhaCodigo = captura.getLinhaCodigo();
		this.bugs = captura.getBugs();
		this.codeSmall = captura.getCodeSmell();
		this.vulnerabilidades = captura.getVulnerabilidades();
		this.issuesMuitoAlta = captura.getIssuesMuitoAlta();
		this.issuesAlta = captura.getIssuesAlta();
		this.issuesMedia = captura.getIssuesMedia();
		this.issuesBaixa = captura.getIssuesBaixa();
		this.issuesMuitoBaixa = captura.getIssuesMuitoBaixa();
		this.vulnerabilityMuitoAlta = captura.getVulnerabilityMuitoAlta();
		this.vulnerabilityAlta = captura.getVulnerabilityAlta();
		this.vulnerabilityMedia = captura.getVulnerabilityMedia();
		this.vulnerabilityBaixa = captura.getVulnerabilityBaixa();
		this.vulnerabilityMuitoBaixa = captura.getVulnerabilityMuitoBaixa();
		this.debitoTecnico = captura.getDebitoTecnico();
		this.debitoTecnicoMinutos = captura.getDebitoTecnicoMinutos();
		this.selecionado = "ui-icon-blank";
		this.novasIssuesMuitoAlta = captura.getNovasIssuesMuitoAlta();
		this.novasIssuesAlta = captura.getNovasIssuesAlta();
		this.novasIssuesMedia = captura.getNovasIssuesMedia();
		this.novasLinhasCodigo = captura.getNovasLinhasCodigo();
		this.novaCobertura = captura.getNovaCobertura();
		this.cobertura = captura.getCobertura();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private int id;

	private String selecionado;

	@Column(name = "URL", length = 500)
	private String url;

	@Column(name = "Painel_Gestor", length = 500)
	private String painelGestor;

	@Column(name = "Gestor_Nivel1")
	private String gestorNivel1;

	@Column(name = "Sigla", length = 100)
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

	@Column(name = "Novas_Linhas_Codigo")
	private Integer novasLinhasCodigo;

	@Column(name = "Linhas_Codigo_Ant")
	private int linhaCodigoAnt;

	@Column(name = "Bugs")
	private int bugs;

	@Column(name = "Code_Smells")
	private int codeSmall;

	@Column(name = "Vulnerabilidades", nullable = true)
	private int vulnerabilidades;

	@Column(name = "Issues_Severity_Muito_Alta")
	private int issuesMuitoAlta;

	@Column(name = "Novas_Issues_Severity_Muito_Alta")
	private Integer novasIssuesMuitoAlta;

	@Column(name = "Issues_Severity_Alta")
	private int issuesAlta;

	@Column(name = "Novas_Issues_Severity_Alta")
	private Integer novasIssuesAlta;

	@Column(name = "Issues_Severity_Media")
	private int issuesMedia;

	@Column(name = "Novas_Issues_Severity_Media")
	private Integer novasIssuesMedia;

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

	@Column(name = "Descricao")
	private String descricao;

	@Column(name = "Debito_Tecnico")
	private String debitoTecnico;

	@Column(name = "Debito_Tecnico_Minutos")
	private String debitoTecnicoMinutos;

	@Column(name = "Esforco_Confiabilidade")
	private String esforcoConfiabilidade;

	@Column(name = "Esforco_Seguranca")
	private String esforcoSeguranca;

	@Column(name = "Cobertura")
	private String cobertura;

	@Column(name = "Nova_Cobertura")
	private String novaCobertura;

	@Column(name = "Nota_Projeto")
	private String notaProjeto;

	@Column(name = "Nota_Anterior")
	private String notaAnterior;

	@Column(name = "Qualidade")
	private String qualidade;

	@Column(name = "Coeficiente")
	private String coeficiente;

	@Column(name = "DevOps")
	private String devops;

	@Column(name = "Tipo")
	private String codigoAlterado;

	@Column(name = "Revisar")
	private Boolean revisarAnalise;

	@Column(name = "Ambiente")
	private String ambiente;

	@Column(name = "Repositorio")
	private String repositorio;

	@Column(name = "Padrao_Nome_Sonar")
	private String padraoNomeSonar;

	@Column(name = "Branch")
	private String branch;

	@Column(name = "Url_Git")
	private String urlGit;

	// --------------------------------------------------

	public String getUrl() {
		return url;
	}

	public String getCodigoAlterado() {
		return codigoAlterado;
	}

	public void setCodigoAlterado(String codigoAlterado) {
		this.codigoAlterado = codigoAlterado;
	}

	public String getDevops() {
		return devops;
	}

	public void setDevops(String devops) {
		this.devops = devops;
	}

	public int getLinhaCodigoAnt() {
		return linhaCodigoAnt;
	}

	public void setLinhaCodigoAnt(int linhaCodigoAnt) {
		this.linhaCodigoAnt = linhaCodigoAnt;
	}

	public String getCoeficiente() {
		return coeficiente;
	}

	public void setCoeficiente(String coeficiente) {
		this.coeficiente = coeficiente;
	}

	public String getDataCommit() {
		return dataCommit;
	}

	public void setDataCommit(String dataCommit) {
		this.dataCommit = dataCommit;
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

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
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

	public String getQualidade() {
		return qualidade;
	}

	public void setQualidade(String qualidade) {
		this.qualidade = qualidade;
	}

	public String getDebitoTecnicoMinutos() {
		return debitoTecnicoMinutos;
	}

	public void setDebitoTecnicoMinutos(String debitoTecnicoMinutos) {
		this.debitoTecnicoMinutos = debitoTecnicoMinutos;
	}

	public String getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(String selecionado) {
		this.selecionado = selecionado;
	}

	public String getGestorNivel1() {
		return gestorNivel1;
	}

	public void setGestorNivel1(String gestorNivel1) {
		this.gestorNivel1 = gestorNivel1;
	}

	public Boolean getRevisarAnalise() {
		return revisarAnalise;
	}

	public void setRevisarAnalise(Boolean revisarAnalise) {
		this.revisarAnalise = revisarAnalise;
	}

	public String getAmbiente() {
		return ambiente;
	}

	public void setAmbiente(String ambiente) {
		this.ambiente = ambiente;
	}

	public String getRepositorio() {
		return repositorio;
	}

	public void setRepositorio(String repositorio) {
		this.repositorio = repositorio;
	}

	public String getPadraoNomeSonar() {
		return padraoNomeSonar;
	}

	public void setPadraoNomeSonar(String padraoNomeSonar) {
		this.padraoNomeSonar = padraoNomeSonar;
	}

	public Integer getNovasLinhasCodigo() {
		return novasLinhasCodigo;
	}

	public void setNovasLinhasCodigo(Integer novasLinhasCodigo) {
		this.novasLinhasCodigo = novasLinhasCodigo;
	}

	public Integer getNovasIssuesMuitoAlta() {
		return novasIssuesMuitoAlta;
	}

	public void setNovasIssuesMuitoAlta(Integer novasIssuesMuitoAlta) {
		this.novasIssuesMuitoAlta = novasIssuesMuitoAlta;
	}

	public Integer getNovasIssuesAlta() {
		return novasIssuesAlta;
	}

	public void setNovasIssuesAlta(Integer novasIssuesAlta) {
		this.novasIssuesAlta = novasIssuesAlta;
	}

	public Integer getNovasIssuesMedia() {
		return novasIssuesMedia;
	}

	public void setNovasIssuesMedia(Integer novasIssuesMedia) {
		this.novasIssuesMedia = novasIssuesMedia;
	}

	public String getNovaCobertura() {
		return novaCobertura;
	}

	public void setNovaCobertura(String novaCobertura) {
		this.novaCobertura = novaCobertura;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUrlGit() {
		return urlGit;
	}

	public void setUrlGit(String urlGit) {
		this.urlGit = urlGit;
	}

	@Override
	public String toString() {
		return "Analise_Dev_Mensal [ branch = " + branch + " id=" + id + ", selecionado=" + selecionado + ", url=" + url
				+ ", painelGestor=" + painelGestor + ", gestorNivel1=" + gestorNivel1 + ", sigla=" + sigla
				+ ", dataCommit=" + dataCommit + ", dataSonar=" + dataSonar + ", versao=" + versao + ", dataCaptura="
				+ dataCaptura + ", nomeProjeto=" + nomeProjeto + ", linhaCodigo=" + linhaCodigo + ", novasLinhasCodigo="
				+ novasLinhasCodigo + ", linhaCodigoAnt=" + linhaCodigoAnt + ", bugs=" + bugs + ", codeSmall="
				+ codeSmall + ", vulnerabilidades=" + vulnerabilidades + ", issuesMuitoAlta=" + issuesMuitoAlta
				+ ", novasIssuesMuitoAlta=" + novasIssuesMuitoAlta + ", issuesAlta=" + issuesAlta + ", novasIssuesAlta="
				+ novasIssuesAlta + ", issuesMedia=" + issuesMedia + ", novasIssuesMedia=" + novasIssuesMedia
				+ ", issuesBaixa=" + issuesBaixa + ", issuesMuitoBaixa=" + issuesMuitoBaixa
				+ ", vulnerabilityMuitoAlta=" + vulnerabilityMuitoAlta + ", vulnerabilityAlta=" + vulnerabilityAlta
				+ ", vulnerabilityMedia=" + vulnerabilityMedia + ", vulnerabilityBaixa=" + vulnerabilityBaixa
				+ ", vulnerabilityMuitoBaixa=" + vulnerabilityMuitoBaixa + ", descricao=" + descricao
				+ ", debitoTecnico=" + debitoTecnico + ", debitoTecnicoMinutos=" + debitoTecnicoMinutos
				+ ", esforcoConfiabilidade=" + esforcoConfiabilidade + ", esforcoSeguranca=" + esforcoSeguranca
				+ ", cobertura=" + cobertura + ", novaCobertura=" + novaCobertura + ", notaProjeto=" + notaProjeto
				+ ", notaAnterior=" + notaAnterior + ", qualidade=" + qualidade + ", coeficiente=" + coeficiente
				+ ", devops=" + devops + ", codigoAlterado=" + codigoAlterado + ", revisarAnalise=" + revisarAnalise
				+ ", ambiente=" + ambiente + ", repositorio=" + repositorio + ", padraoNomeSonar=" + padraoNomeSonar
				+ ", branch=" + branch + "]";
	}

}
