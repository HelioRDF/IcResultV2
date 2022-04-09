package br.com.icresult.domain.complementos;

import java.util.Comparator;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.icresult.model.SonarAtributos;

@SuppressWarnings("serial")
@Entity(name = "Paineis_Dentro_Sonar")
public class PaineisDentroSonar extends GenericDomainID {

	public static Comparator<PaineisDentroSonar> getComparadorPorDataCaptura() {
		return new Comparator<PaineisDentroSonar>() {
			@Override
			public int compare(PaineisDentroSonar o1, PaineisDentroSonar o2) {
				return o1.getDataCaptura().compareTo(o2.getDataCaptura());
			}
		};
	}

	public PaineisDentroSonar() {
	}

	public PaineisDentroSonar(SonarAtributos captura) {
		dataSonar = captura.getDataSonar();
		dataCaptura = captura.getDataCaptura();
		modulo = captura.getNomeProjeto();
		linhaCodigo = captura.getLinhaCodigo();
		Blocker = captura.getIssuesMuitoAlta();
		Critical = captura.getIssuesAlta();
		Major = captura.getIssuesMedia();
		novosBlockers = captura.getNovasIssuesMuitoAlta();
		novosCriticals = captura.getNovasIssuesAlta();
		novosMajors = captura.getNovasIssuesMedia();
		linkIntegracaoContinua = captura.getLinkIntegracaoContinua();
		debitoTecnicoEmMinutos = captura.getDebitoTecnicoEmMinutos();
	}

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

	@Column(name = "Linhas_Codigo")
	private int linhaCodigo;

	@Column(name = "Bug_Blocker")
	private int Blocker;

	@Column(name = "Bug_Critical")
	private int Critical;

	@Column(name = "Bug_Major")
	private int Major;

	@Column(name = "Novos_Criticals", nullable = true)
	private int novosCriticals;

	@Column(name = "Novos_Blockers", nullable = true)
	private int novosBlockers;

	@Column(name = "Novos_Majors", nullable = true)
	private int novosMajors;

	@Column(name = "Link_Integracao_Continua")
	private String linkIntegracaoContinua;

	@Column(name = "Debito_Tecnico_Em_Minutos")
	private String debitoTecnicoEmMinutos;

	// toString / Hash / Equals
	// --------------------------------------------------

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Blocker;
		result = prime * result + Critical;
		result = prime * result + Major;
		result = prime * result + ((dataCaptura == null) ? 0 : dataCaptura.hashCode());
		result = prime * result + ((dataCommit == null) ? 0 : dataCommit.hashCode());
		result = prime * result + ((dataSonar == null) ? 0 : dataSonar.hashCode());
		result = prime * result + linhaCodigo;
		result = prime * result + ((modulo == null) ? 0 : modulo.hashCode());
		result = prime * result + novosBlockers;
		result = prime * result + novosCriticals;
		return result;
	}

	@Override
	public String toString() {
		return "PaineisDentroSonar [dataCommit=" + dataCommit + ", dataSonar=" + dataSonar + ", dataCaptura="
				+ dataCaptura + ", modulo=" + modulo + ", linhaCodigo=" + linhaCodigo + ", bugBlocker=" + Blocker
				+ ", bugCritical=" + Critical + ", bugMajor=" + Major + ", novosCriticals=" + novosCriticals
				+ ", novosBlockers=" + novosBlockers + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PaineisDentroSonar other = (PaineisDentroSonar) obj;
		if (Blocker != other.Blocker)
			return false;
		if (Critical != other.Critical)
			return false;
		if (Major != other.Major)
			return false;
		if (dataCaptura == null) {
			if (other.dataCaptura != null)
				return false;
		} else if (!dataCaptura.equals(other.dataCaptura))
			return false;
		if (dataCommit == null) {
			if (other.dataCommit != null)
				return false;
		} else if (!dataCommit.equals(other.dataCommit))
			return false;
		if (dataSonar == null) {
			if (other.dataSonar != null)
				return false;
		} else if (!dataSonar.equals(other.dataSonar))
			return false;
		if (linhaCodigo != other.linhaCodigo)
			return false;
		if (modulo == null) {
			if (other.modulo != null)
				return false;
		} else if (!modulo.equals(other.modulo))
			return false;
		if (novosBlockers != other.novosBlockers)
			return false;
		if (novosCriticals != other.novosCriticals)
			return false;
		return true;
	}

	// Get e Set
	// --------------------------------------------------

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

	public String getmodulo() {
		return modulo;
	}

	public void setmodulo(String modulo) {
		this.modulo = modulo;
	}

	public int getLinhaCodigo() {
		return linhaCodigo;
	}

	public void setLinhaCodigo(int linhaCodigo) {
		this.linhaCodigo = linhaCodigo;
	}

	public int getBlocker() {
		return Blocker;
	}

	public void setBlocker(int Blocker) {
		this.Blocker = Blocker;
	}

	public int getCritical() {
		return Critical;
	}

	public void setCritical(int Critical) {
		this.Critical = Critical;
	}

	public int getMajor() {
		return Major;
	}

	public void setMajor(int Major) {
		this.Major = Major;
	}

	public int getNovosCriticals() {
		return novosCriticals;
	}

	public void setNovosCriticals(int novosCriticals) {
		this.novosCriticals = novosCriticals;
	}

	public int getNovosBlockers() {
		return novosBlockers;
	}

	public void setNovosBlockers(int novosBlockers) {
		this.novosBlockers = novosBlockers;
	}

	public int getNovosMajors() {
		return novosMajors;
	}

	public void setNovosMajors(int novosMajors) {
		this.novosMajors = novosMajors;
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

	// --------------------------------------------------

}
