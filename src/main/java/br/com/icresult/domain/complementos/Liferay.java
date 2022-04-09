package br.com.icresult.domain.complementos;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * -Classe POJO Liferay entity do DB via Hibernate.
 * 
 * @author andre.graca
 * @version v1.0
 * @since 16-05-2019
 * 
 */

@Entity
public class Liferay implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4479368544693328675L;

	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	@Column(insertable = true)
	private Long ID;

	@Column(name = "Sigla")
	private String sigla;

	@Column(name = "Nome_Projeto")
	private String nome_Projeto;

	@Column(name = "Chave")
	private String chave;

	private String selecionado;

	// --------------------------------------------------

	public String getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(String selecionado) {
		this.selecionado = selecionado;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public String getNome_Projeto() {
		return nome_Projeto;
	}

	public void setNome_Projeto(String nome_Projeto) {
		this.nome_Projeto = nome_Projeto;
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

}