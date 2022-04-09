package br.com.icresult.domain.complementos;

import java.util.Comparator;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.icresult.model.Captura;

/***
 * -Classe POJO Analise_Codigo_Diaria entity do DB via Hibernate.
 * 
 * @author andre.graca
 * @version v1.0
 * @since 21-11-2018
 * 
 */

@Entity
public class Analise_Dev_Diario extends GenericDomainID {

	private static final long serialVersionUID = 1L;

	public static Comparator<Analise_Dev_Diario> getComparadorPorDataCapturaCrescente() {
		return new Comparator<Analise_Dev_Diario>() {
			@Override
			public int compare(Analise_Dev_Diario o1, Analise_Dev_Diario o2) {
				return o1.getDataCaptura().compareTo(o2.getDataCaptura());
			}
		};
	}

	public static Comparator<Analise_Dev_Diario> getComparadorPorDataCapturaDescrecente() {
		return new Comparator<Analise_Dev_Diario>() {
			@Override
			public int compare(Analise_Dev_Diario o1, Analise_Dev_Diario o2) {
				return o1.getDataCaptura().compareTo(o2.getDataCaptura());
			}
		};
	}

	public static Comparator<Analise_Dev_Diario> getComparatorPorSigla() {
		return new Comparator<Analise_Dev_Diario>() {
			@Override
			public int compare(Analise_Dev_Diario o1, Analise_Dev_Diario o2) {
				return o1.getSigla().compareTo(o2.getSigla());
			}
		};
	}

	public Analise_Dev_Diario() {
	}

	public Analise_Dev_Diario(Captura captura) {
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
	}

	private String selecionado;

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

	@Column(name = "Issues_Severity_Muito_Alta_Anterior")
	private Integer issuesMuitoAltaAnterior;

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

	@Column(name = "Descricao")
	@Lob
	private String descricao;

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

	@Column(name = "Qualidade")
	private String qualidade;

	@Column(name = "CodigoAlteracao")
	private String codigoAlterado;

	@Column(name = "Debito_Tecnico_Minutos")
	private String debitoTecnicoMinutos;

	@Column(name = "DevOps")
	private String devOps;

	@Column(name = "Blocker_Referencia")
	private Integer blockerReferencia;

	@Column(name = "Critical_Referencia")
	private Integer criticalReferencia;

	@Column(name = "Ambiente")
	private String ambiente;

	@Column(name = "Repositorio")
	private String repositorio;

	@Column(name = "Padrao_Nome_Sonar")
	private String padraoNomeSonar;

	// --------------------------------------------------

	@Override
	public String toString() {
		return "Analise_Dev_Diario [" + (selecionado != null ? "selecionado=" + selecionado + ", " : "")
				+ (url != null ? "url=" + url + ", " : "")
				+ (painelGestor != null ? "painelGestor=" + painelGestor + ", " : "")
				+ (sigla != null ? "sigla=" + sigla + ", " : "")
				+ (dataCommit != null ? "dataCommit=" + dataCommit + ", " : "")
				+ (dataSonar != null ? "dataSonar=" + dataSonar + ", " : "")
				+ (versao != null ? "versao=" + versao + ", " : "")
				+ (dataCaptura != null ? "dataCaptura=" + dataCaptura + ", " : "")
				+ (nomeProjeto != null ? "nomeProjeto=" + nomeProjeto + ", " : "") + "linhaCodigo=" + linhaCodigo
				+ ", bugs=" + bugs + ", codeSmall=" + codeSmall + ", vulnerabilidades=" + vulnerabilidades
				+ ", issuesMuitoAlta=" + issuesMuitoAlta + ", "
				+ (issuesMuitoAltaAnterior != null ? "issuesMuitoAltaAnterior=" + issuesMuitoAltaAnterior + ", " : "")
				+ "issuesAlta=" + issuesAlta + ", issuesMedia=" + issuesMedia + ", issuesBaixa=" + issuesBaixa
				+ ", issuesMuitoBaixa=" + issuesMuitoBaixa + ", vulnerabilityMuitoAlta=" + vulnerabilityMuitoAlta
				+ ", vulnerabilityAlta=" + vulnerabilityAlta + ", vulnerabilityMedia=" + vulnerabilityMedia
				+ ", vulnerabilityBaixa=" + vulnerabilityBaixa + ", vulnerabilityMuitoBaixa=" + vulnerabilityMuitoBaixa
				+ ", " + (descricao != null ? "descricao=" + descricao + ", " : "")
				+ (debitoTecnico != null ? "debitoTecnico=" + debitoTecnico + ", " : "")
				+ (esforcoConfiabilidade != null ? "esforcoConfiabilidade=" + esforcoConfiabilidade + ", " : "")
				+ (esforcoSeguranca != null ? "esforcoSeguranca=" + esforcoSeguranca + ", " : "")
				+ (cobertura != null ? "cobertura=" + cobertura + ", " : "")
				+ (notaProjeto != null ? "notaProjeto=" + notaProjeto + ", " : "")
				+ (notaAnterior != null ? "notaAnterior=" + notaAnterior + ", " : "")
				+ (resultado != null ? "resultado=" + resultado + ", " : "")
				+ (qualidade != null ? "qualidade=" + qualidade + ", " : "")
				+ (codigoAlterado != null ? "codigoAlterado=" + codigoAlterado + ", " : "")
				+ (debitoTecnicoMinutos != null ? "debitoTecnicoMinutos=" + debitoTecnicoMinutos + ", " : "")
				+ (devOps != null ? "devOps=" + devOps + ", " : "")
				+ (blockerReferencia != null ? "blockerReferencia=" + blockerReferencia + ", " : "")
				+ (criticalReferencia != null ? "criticalReferencia=" + criticalReferencia + ", " : "")
				+ (ambiente != null ? "ambiente=" + ambiente + ", " : "")
				+ (repositorio != null ? "repositorio=" + repositorio + ", " : "")
				+ (padraoNomeSonar != null ? "padraoNomeSonar=" + padraoNomeSonar : "") + "]";
	}

	public String getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(String selecionado) {
		this.selecionado = selecionado;
	}

	public String getDevOps() {
		return devOps;
	}

	public void setDevOps(String devOps) {
		this.devOps = devOps;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setPainelGestor(String painelGestor) {
		this.painelGestor = painelGestor;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public void setDataCommit(String dataCommit) {
		this.dataCommit = dataCommit;
	}

	public void setDataSonar(Date dataSonar) {
		this.dataSonar = dataSonar;
	}

	public void setVersao(String versao) {
		this.versao = versao;
	}

	public void setDataCaptura(Date dataCaptura) {
		this.dataCaptura = dataCaptura;
	}

	public void setNomeProjeto(String nomeProjeto) {
		this.nomeProjeto = nomeProjeto;
	}

	public void setLinhaCodigo(int linhaCodigo) {
		this.linhaCodigo = linhaCodigo;
	}

	public void setBugs(int bugs) {
		this.bugs = bugs;
	}

	public void setCodeSmall(int codeSmall) {
		this.codeSmall = codeSmall;
	}

	public void setVulnerabilidades(int vulnerabilidades) {
		this.vulnerabilidades = vulnerabilidades;
	}

	public void setIssuesMuitoAlta(int issuesMuitoAlta) {
		this.issuesMuitoAlta = issuesMuitoAlta;
	}

	public void setIssuesAlta(int issuesAlta) {
		this.issuesAlta = issuesAlta;
	}

	public void setIssuesMedia(int issuesMedia) {
		this.issuesMedia = issuesMedia;
	}

	public void setIssuesBaixa(int issuesBaixa) {
		this.issuesBaixa = issuesBaixa;
	}

	public void setIssuesMuitoBaixa(int issuesMuitoBaixa) {
		this.issuesMuitoBaixa = issuesMuitoBaixa;
	}

	public void setVulnerabilityMuitoAlta(int vulnerabilityMuitoAlta) {
		this.vulnerabilityMuitoAlta = vulnerabilityMuitoAlta;
	}

	public void setVulnerabilityAlta(int vulnerabilityAlta) {
		this.vulnerabilityAlta = vulnerabilityAlta;
	}

	public void setVulnerabilityMedia(int vulnerabilityMedia) {
		this.vulnerabilityMedia = vulnerabilityMedia;
	}

	public void setVulnerabilityBaixa(int vulnerabilityBaixa) {
		this.vulnerabilityBaixa = vulnerabilityBaixa;
	}

	public void setVulnerabilityMuitoBaixa(int vulnerabilityMuitoBaixa) {
		this.vulnerabilityMuitoBaixa = vulnerabilityMuitoBaixa;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public void setDebitoTecnico(String debitoTecnico) {
		this.debitoTecnico = debitoTecnico;
	}

	public void setEsforcoConfiabilidade(String esforcoConfiabilidade) {
		this.esforcoConfiabilidade = esforcoConfiabilidade;
	}

	public void setEsforcoSeguranca(String esforcoSeguranca) {
		this.esforcoSeguranca = esforcoSeguranca;
	}

	public void setCobertura(String cobertura) {
		this.cobertura = cobertura;
	}

	public void setNotaProjeto(String notaProjeto) {
		this.notaProjeto = notaProjeto;
	}

	public void setNotaAnterior(String notaAnterior) {
		this.notaAnterior = notaAnterior;
	}

	public void setResultado(String resultado) {
		this.resultado = resultado;
	}

	public void setQualidade(String qualidade) {
		this.qualidade = qualidade;
	}

	public void setCodigoAlterado(String codigoAlterado) {
		this.codigoAlterado = codigoAlterado;
	}

	public void setDebitoTecnicoMinutos(String debitoTecnicoMinutos) {
		this.debitoTecnicoMinutos = debitoTecnicoMinutos;
	}

	public String getUrl() {
		return url;
	}

	public String getPainelGestor() {
		return painelGestor;
	}

	public String getSigla() {
		return sigla;
	}

	public String getDataCommit() {
		return dataCommit;
	}

	public Date getDataSonar() {
		return dataSonar;
	}

	public String getVersao() {
		return versao;
	}

	public Date getDataCaptura() {
		return dataCaptura;
	}

	public String getNomeProjeto() {
		return nomeProjeto;
	}

	public int getLinhaCodigo() {
		return linhaCodigo;
	}

	public int getBugs() {
		return bugs;
	}

	public int getCodeSmall() {
		return codeSmall;
	}

	public int getVulnerabilidades() {
		return vulnerabilidades;
	}

	public int getIssuesMuitoAlta() {
		return issuesMuitoAlta;
	}

	public int getIssuesAlta() {
		return issuesAlta;
	}

	public int getIssuesMedia() {
		return issuesMedia;
	}

	public int getIssuesBaixa() {
		return issuesBaixa;
	}

	public int getIssuesMuitoBaixa() {
		return issuesMuitoBaixa;
	}

	public int getVulnerabilityMuitoAlta() {
		return vulnerabilityMuitoAlta;
	}

	public int getVulnerabilityAlta() {
		return vulnerabilityAlta;
	}

	public int getVulnerabilityMedia() {
		return vulnerabilityMedia;
	}

	public int getVulnerabilityBaixa() {
		return vulnerabilityBaixa;
	}

	public int getVulnerabilityMuitoBaixa() {
		return vulnerabilityMuitoBaixa;
	}

	public String getDescricao() {
		return descricao;
	}

	public String getDebitoTecnico() {
		return debitoTecnico;
	}

	public String getEsforcoConfiabilidade() {
		return esforcoConfiabilidade;
	}

	public String getEsforcoSeguranca() {
		return esforcoSeguranca;
	}

	public String getCobertura() {
		return cobertura;
	}

	public String getNotaProjeto() {
		return notaProjeto;
	}

	public String getNotaAnterior() {
		return notaAnterior;
	}

	public String getResultado() {
		return resultado;
	}

	public String getQualidade() {
		return qualidade;
	}

	public String getCodigoAlterado() {
		return codigoAlterado;
	}

	public String getDebitoTecnicoMinutos() {
		return debitoTecnicoMinutos;
	}

	public Integer getIssuesMuitoAltaAnterior() {
		return issuesMuitoAltaAnterior;
	}

	public void setIssuesMuitoAltaAnterior(Integer issuesMuitoAltaAnterior) {
		this.issuesMuitoAltaAnterior = issuesMuitoAltaAnterior;
	}

	public Integer getBlockerReferencia() {
		return blockerReferencia;
	}

	public void setBlockerReferencia(Integer blockerReferencia) {
		this.blockerReferencia = blockerReferencia;
	}

	public Integer getCriticalReferencia() {
		return criticalReferencia;
	}

	public void setCriticalReferencia(Integer criticalReferencia) {
		this.criticalReferencia = criticalReferencia;
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

}