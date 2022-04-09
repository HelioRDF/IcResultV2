package br.com.icresult.domain.complementos;

import java.util.Comparator;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.icresult.model.SonarAtributos;

@SuppressWarnings("serial")
@Entity(name = "Analise_Sonar")
public class Analise_Sonar extends GenericDomainID {

	public static Comparator<Analise_Sonar> getComparadorPorDataCaptura() {
		return new Comparator<Analise_Sonar>() {
			@Override
			public int compare(Analise_Sonar o1, Analise_Sonar o2) {
				return o1.getDataCaptura().compareTo(o2.getDataCaptura());
			}
		};
	}

	public Analise_Sonar() {
	}

	public Analise_Sonar(SonarAtributos captura) {
		dataSonar = captura.getDataSonar();
		dataCaptura = captura.getDataCaptura();
		modulo = captura.getNomeProjeto();
		linhaCodigo = captura.getLinhaCodigo();
		blockers = captura.getIssuesMuitoAlta();
		novosBlockers = captura.getNovasIssuesMuitoAlta();
		criticals = captura.getIssuesAlta();
		novosCriticals = captura.getNovasIssuesAlta();
		majors = captura.getIssuesMedia();
		novosMajors = captura.getNovasIssuesMedia();
		url = captura.getUrl();
		cobertura = captura.getCoberturaCodigo() == null ? "0.0%" : captura.getCoberturaCodigo();
		novaCobertura = captura.getNovaCobertura() == null ? "0.0%" : captura.getNovaCobertura();
		debitoTecnicoEmMinutos = captura.getDebitoTecnicoEmMinutos();
	}

	@Column(name = "Sigla")
	private String sigla;

	@Column(name = "Data_Commit")
	private String dataCommit;

	@Column(name = "Data_Sonar")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataSonar;

	@Column(name = "Data_Captura")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataCaptura;

	@Column(name = "Modulo", length = 500)
	private String modulo;

	@Column(name = "Tipo_Entrega", length = 100)
	private String tipoEntrega;

	@Column(name = "Linhas_Codigo")
	private Integer linhaCodigo;

	@Column(name = "Blocker")
	private Integer blockers;

	@Column(name = "Novos_Blockers")
	private Integer novosBlockers;

	@Column(name = "Critical")
	private Integer criticals;

	@Column(name = "Novos_Criticals")
	private Integer novosCriticals;

	@Column(name = "Major")
	private Integer majors;

	@Column(name = "Novos_Majors")
	private Integer novosMajors;

	@Column(name = "URL", nullable = true)
	private String url;

	@Column(name = "Cobertura_Codigo")
	private String cobertura;

	@Column(name = "Nova_Cobertura_Codigo")
	private String novaCobertura;

	@Column(name = "Debito_Tecnico")
	private String debitoTecnicoEmMinutos;

	// toString / Hash / Equals
	// --------------------------------------------------

	@Override
	public String toString() {
		return "Analise_Sonar [sigla=" + sigla + ", dataCommit=" + dataCommit + ", dataSonar=" + dataSonar
				+ ", dataCaptura=" + dataCaptura + ", modulo=" + modulo + ", tipoEntrega=" + tipoEntrega
				+ ", linhaCodigo=" + linhaCodigo + ", blockers=" + blockers + ", novosBlockerss=" + novosBlockers
				+ ", criticals=" + criticals + ", novosCriticals=" + novosCriticals + ", majors=" + majors
				+ ", novosMajors=" + novosMajors + ", url=" + url + ", cobertura=" + cobertura + ", novaCobertura="
				+ novaCobertura + ", debitoTecnicoEmMinutos=" + debitoTecnicoEmMinutos + "]";
	}

	// Get e Set
	// --------------------------------------------------

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public String getDataCommit() {
		return dataCommit;
	}

	public void setDataCommit(String dataCommit) {
		this.dataCommit = dataCommit;
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

	public String getModulo() {
		return modulo;
	}

	public void setModulo(String modulo) {
		this.modulo = modulo;
	}

	public String getTipoEntrega() {
		return tipoEntrega;
	}

	public void setTipoEntrega(String tipoEntrega) {
		this.tipoEntrega = tipoEntrega;
	}

	public Integer getLinhaCodigo() {
		return linhaCodigo;
	}

	public void setLinhaCodigo(Integer linhaCodigo) {
		this.linhaCodigo = linhaCodigo;
	}

	public Integer getBlockers() {
		return blockers;
	}

	public void setBlockers(Integer blockers) {
		this.blockers = blockers;
	}

	public Integer getNovosBlockers() {
		return novosBlockers;
	}

	public void setNovosBlockerss(Integer novosBlockers) {
		this.novosBlockers = novosBlockers;
	}

	public Integer getCriticals() {
		return criticals;
	}

	public void setCriticals(Integer criticals) {
		this.criticals = criticals;
	}

	public Integer getNovosCriticals() {
		return novosCriticals;
	}

	public void setNovosCriticals(Integer novosCriticals) {
		this.novosCriticals = novosCriticals;
	}

	public Integer getMajors() {
		return majors;
	}

	public void setMajors(Integer majors) {
		this.majors = majors;
	}

	public Integer getNovosMajors() {
		return novosMajors;
	}

	public void setNovosMajors(Integer novosMajors) {
		this.novosMajors = novosMajors;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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

	public String getDebitoTecnicoEmMinutos() {
		return debitoTecnicoEmMinutos;
	}

	public void setDebitoTecnicoEmMinutos(String debitoTecnicoEmMinutos) {
		this.debitoTecnicoEmMinutos = debitoTecnicoEmMinutos;
	}
	// --------------------------------------------------

}
