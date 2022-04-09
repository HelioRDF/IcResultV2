package br.com.icresult.domain.complementos;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * -Classe POJO DevOps entity do DB via Hibernate.
 * 
 * @author andre.graca
 * @version v1.0
 * @since 19-06-2019
 * 
 */

@Entity(name = "DevOpsGenerico")
public class DevOpsGenerico {

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private Long ID;

	@Column(name = "Sigla")
	private String sigla;

	@Column(name = "NomeProjeto")
	private String nomeProjeto;

	@Column(name = "Chave")
	private String chave;

	private boolean selecionado;

	// -----------------getters e setters----------------------

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	public boolean getSelecionado() {
		return selecionado;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
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

	public Long getId() {
		return ID;
	}

	@Override
	public String toString() {
		return "DevOpsGenerico [ID=" + ID + ", sigla=" + sigla + ", nomeProjeto=" + nomeProjeto + ", chave=" + chave
				+ ", selecionado=" + selecionado + "]";
	}

}