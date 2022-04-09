package br.com.icresult.domain.complementos;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * -Classe POJO RelacaoProjetoSiglaGestor entity do DB via Hibernate.
 * 
 * @author helio.franca
 * @version v2.4.4
 * @since 18-10-2018
 * 
 */

@Entity
public class DevOpsJenkins implements Serializable {

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

	@Column(name = "Url_Git")
	private String urlGit;

	private boolean selecionado;

	// --------------------------------------------------

	public boolean getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
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

	public String getUrlGit() {
		return urlGit;
	}

	public void setUrlGit(String urlGit) {
		this.urlGit = urlGit;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ID == null) ? 0 : ID.hashCode());
		result = prime * result + ((chave == null) ? 0 : chave.hashCode());
		result = prime * result + ((nome_Projeto == null) ? 0 : nome_Projeto.hashCode());
		result = prime * result + (selecionado ? 1231 : 1237);
		result = prime * result + ((sigla == null) ? 0 : sigla.hashCode());
		result = prime * result + ((urlGit == null) ? 0 : urlGit.hashCode());
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
		DevOpsJenkins other = (DevOpsJenkins) obj;
		if (ID == null) {
			if (other.ID != null)
				return false;
		} else if (!ID.equals(other.ID))
			return false;
		if (chave == null) {
			if (other.chave != null)
				return false;
		} else if (!chave.equals(other.chave))
			return false;
		if (nome_Projeto == null) {
			if (other.nome_Projeto != null)
				return false;
		} else if (!nome_Projeto.equals(other.nome_Projeto))
			return false;
		if (selecionado != other.selecionado)
			return false;
		if (sigla == null) {
			if (other.sigla != null)
				return false;
		} else if (!sigla.equals(other.sigla))
			return false;
		if (urlGit == null) {
			if (other.urlGit != null)
				return false;
		} else if (!urlGit.equals(other.urlGit))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DevOpsJenkins [ID=" + ID + ", sigla=" + sigla + ", nome_Projeto=" + nome_Projeto + ", chave=" + chave
				+ ", urlGit=" + urlGit + ", selecionado=" + selecionado + "]";
	}

}