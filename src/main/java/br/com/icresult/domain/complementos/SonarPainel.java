package br.com.icresult.domain.complementos;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * Classe utilizada para padronizar as capturas
 * 
 * @author helio.franca
 * @version v3.0.0
 * @since 12-06-2019
 *
 */

@Entity(name = "Sonar_Painel")
public class SonarPainel extends GenericDomainID {

	/**
	 * 
	 */
	
	
	private static final long serialVersionUID = -8513984992347744344L;
	@Column
	private String nomeProjeto;
	@Column
	private String chave;
	@Column
	private String dataSonar;
	
	@Transient
	private String dash = "http://sonar.produbanbr.corp/dashboard?id="+chave;

	// --------------- Get e Set
	// ------------------------------------------------------------------

	public String getDataSonar() {
		return dataSonar;
	}

	public void setDataSonar(String dataSonar) {
		this.dataSonar = dataSonar;
	}

	public String getNomeProjeto() {
		return nomeProjeto;
	}

	public void setNomeProjeto(String nomeProjeto) {
		this.nomeProjeto = nomeProjeto;
	}

	public String getChave() {
		return chave;
	}

	public void setChave(String chave) {
		this.chave = chave;
	}

	// --------------- ToString
	// ------------------------------------------------------------------

	@Override
	public String toString() {
		return "SonarPainel [ nomeProjeto=" + nomeProjeto + ", chave=" + chave + "]";
	}

}
