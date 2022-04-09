package br.com.icresult.domain.complementos;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Lob;

/**
 * 
 * @author x205451
 *
 *         Classe POJO para definir a estrutura da lista do CloudBees para a
 *         construção da lista DevOps.
 *
 */

@SuppressWarnings("serial")
@Entity
public class ListaCloudBees extends GenericDomainID {

	private String master;

	private String sigla;

	private String tecnologia;

	private String job;

	@Lob
	private String urlCloudBees;

	private String nomePainel;

	private String chavePainel;

	private Date dataDoPainelSonar;

	private String tipoSonar;

	private Date dataUltimaCarga;

	private String caminhoDaLista;

	private boolean podeEntrarComoDevOps;

	public boolean getPodeEntrarComoDevOps() {
		return podeEntrarComoDevOps;
	}

	public void setPodeEntrarComoDevOps(boolean podeEntrarComoDevOps) {
		this.podeEntrarComoDevOps = podeEntrarComoDevOps;
	}

	public Date getDataDoPainelSonar() {
		return dataDoPainelSonar;
	}

	public void setDataDoPainelSonar(Date dataDoPainelSonar) {
		this.dataDoPainelSonar = dataDoPainelSonar;
	}

	public Date getDataUltimaCarga() {
		return dataUltimaCarga;
	}

	public void setDataUltimaCarga(Date dataUltimaCarga) {
		this.dataUltimaCarga = dataUltimaCarga;
	}

	public String getMaster() {
		return master;
	}

	public void setMaster(String master) {
		this.master = master;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public String getTecnologia() {
		return tecnologia;
	}

	public void setTecnologia(String tecnologia) {
		this.tecnologia = tecnologia;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getUrlCloudBees() {
		return urlCloudBees;
	}

	public void setUrlCloudBees(String urlCloudBees) {
		this.urlCloudBees = urlCloudBees;
	}

	public String getNomePainel() {
		return nomePainel;
	}

	public void setNomePainel(String nomePainel) {
		this.nomePainel = nomePainel;
	}

	public String getChavePainel() {
		return chavePainel;
	}

	public void setChavePainel(String chavePainel) {
		this.chavePainel = chavePainel;
	}

	public String getTipoSonar() {
		return tipoSonar;
	}

	public void setTipoSonar(String tipoSonar) {
		this.tipoSonar = tipoSonar;
	}

	public String getCaminhoDaLista() {
		return caminhoDaLista;
	}

	public void setCaminhoDaLista(String caminhoDaLista) {
		this.caminhoDaLista = caminhoDaLista;
	}

	public boolean isPodeEntrarComoDevOps() {
		return podeEntrarComoDevOps;
	}

	public String getNomeConcatenado() {
		StringBuilder nomeConcatenado = new StringBuilder();
		if (tecnologia.isEmpty()) {
			nomeConcatenado.append(sigla).append("-").append(job);
		} else {
			if (tecnologia.charAt(0) == '_') {
				nomeConcatenado.append(sigla).append(tecnologia).append("-").append(job);
			} else {
				nomeConcatenado.append(sigla).append("-").append(tecnologia).append("-").append(job);
			}
		}
		return nomeConcatenado.toString();
	}

	public String getClassificaComoDevOps() {
		return podeEntrarComoDevOps ? "Sim" : "Não";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((caminhoDaLista == null) ? 0 : caminhoDaLista.hashCode());
		result = prime * result + ((chavePainel == null) ? 0 : chavePainel.hashCode());
		result = prime * result + ((dataDoPainelSonar == null) ? 0 : dataDoPainelSonar.hashCode());
		result = prime * result + ((dataUltimaCarga == null) ? 0 : dataUltimaCarga.hashCode());
		result = prime * result + ((job == null) ? 0 : job.hashCode());
		result = prime * result + ((master == null) ? 0 : master.hashCode());
		result = prime * result + ((nomePainel == null) ? 0 : nomePainel.hashCode());
		result = prime * result + (podeEntrarComoDevOps ? 1231 : 1237);
		result = prime * result + ((sigla == null) ? 0 : sigla.hashCode());
		result = prime * result + ((tecnologia == null) ? 0 : tecnologia.hashCode());
		result = prime * result + ((tipoSonar == null) ? 0 : tipoSonar.hashCode());
		result = prime * result + ((urlCloudBees == null) ? 0 : urlCloudBees.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ListaCloudBees other = (ListaCloudBees) obj;
		if (caminhoDaLista == null) {
			if (other.caminhoDaLista != null)
				return false;
		} else if (!caminhoDaLista.equals(other.caminhoDaLista))
			return false;
		if (chavePainel == null) {
			if (other.chavePainel != null)
				return false;
		} else if (!chavePainel.equals(other.chavePainel))
			return false;
		if (dataDoPainelSonar == null) {
			if (other.dataDoPainelSonar != null)
				return false;
		} else if (!dataDoPainelSonar.equals(other.dataDoPainelSonar))
			return false;
		if (dataUltimaCarga == null) {
			if (other.dataUltimaCarga != null)
				return false;
		} else if (!dataUltimaCarga.equals(other.dataUltimaCarga))
			return false;
		if (job == null) {
			if (other.job != null)
				return false;
		} else if (!job.equals(other.job))
			return false;
		if (master == null) {
			if (other.master != null)
				return false;
		} else if (!master.equals(other.master))
			return false;
		if (nomePainel == null) {
			if (other.nomePainel != null)
				return false;
		} else if (!nomePainel.equals(other.nomePainel))
			return false;
		if (podeEntrarComoDevOps != other.podeEntrarComoDevOps)
			return false;
		if (sigla == null) {
			if (other.sigla != null)
				return false;
		} else if (!sigla.equals(other.sigla))
			return false;
		if (tecnologia == null) {
			if (other.tecnologia != null)
				return false;
		} else if (!tecnologia.equals(other.tecnologia))
			return false;
		if (tipoSonar == null) {
			if (other.tipoSonar != null)
				return false;
		} else if (!tipoSonar.equals(other.tipoSonar))
			return false;
		if (urlCloudBees == null) {
			if (other.urlCloudBees != null)
				return false;
		} else if (!urlCloudBees.equals(other.urlCloudBees))
			return false;
		return true;
	}

}
