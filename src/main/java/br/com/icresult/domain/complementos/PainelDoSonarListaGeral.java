package br.com.icresult.domain.complementos;

import java.util.Date;

import javax.persistence.Entity;

/**
 * 
 * @author x205451
 * 
 *         Classe POJO para definir a estrutura da lista do Sonar para a
 *         construção da lista DevOps.
 * 
 */


@SuppressWarnings("serial")
@Entity
public class PainelDoSonarListaGeral extends GenericDomainID{

	private String nomePainel;

	private String chavePainel;

	private Date ultimaAnalise;

	private String tipoSonar;

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

	public Date getUltimaAnalise() {
		return ultimaAnalise;
	}

	public void setUltimaAnalise(Date ultimaAnalise) {
		this.ultimaAnalise = ultimaAnalise;
	}

	public String getTipoSonar() {
		return tipoSonar;
	}

	public void setTipoSonar(String tipoSonar) {
		this.tipoSonar = tipoSonar;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((chavePainel == null) ? 0 : chavePainel.hashCode());
		result = prime * result + ((nomePainel == null) ? 0 : nomePainel.hashCode());
		result = prime * result + ((tipoSonar == null) ? 0 : tipoSonar.hashCode());
		result = prime * result + ((ultimaAnalise == null) ? 0 : ultimaAnalise.hashCode());
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
		PainelDoSonarListaGeral other = (PainelDoSonarListaGeral) obj;
		if (chavePainel == null) {
			if (other.chavePainel != null)
				return false;
		} else if (!chavePainel.equals(other.chavePainel))
			return false;
		if (nomePainel == null) {
			if (other.nomePainel != null)
				return false;
		} else if (!nomePainel.equals(other.nomePainel))
			return false;
		if (tipoSonar == null) {
			if (other.tipoSonar != null)
				return false;
		} else if (!tipoSonar.equals(other.tipoSonar))
			return false;
		if (ultimaAnalise == null) {
			if (other.ultimaAnalise != null)
				return false;
		} else if (!ultimaAnalise.equals(other.ultimaAnalise))
			return false;
		return true;
	}

}
