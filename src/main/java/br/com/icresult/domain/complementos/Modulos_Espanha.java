package br.com.icresult.domain.complementos;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * 
 * @author andre.graca
 */
@Entity
public class Modulos_Espanha extends GenericChaveSonar {

	private static final long serialVersionUID = 1L;

	@Column(name = "Gestor")
	private String painelGestor;

	@Column()
	private Boolean capturado;

	@Column()
	private String selecionado;

	// -------Getters e Setters

	public String getPainelGestor() {
		return painelGestor;
	}

	public void setPainelGestor(String painelGestor) {
		this.painelGestor = painelGestor;
	}

	public Boolean getCapturado() {
		return capturado;
	}

	public void setCapturado(Boolean capturado) {
		this.capturado = capturado;
	}

	public String getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(String selecionado) {
		this.selecionado = selecionado;
	}

	@Override
	public String toString() {
		return "Modulos_Espanha [painelGestor=" + painelGestor + ", capturado=" + capturado + ", selecionado="
				+ selecionado + "]";
	}

}
