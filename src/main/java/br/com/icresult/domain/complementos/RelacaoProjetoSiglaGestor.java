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

@Entity(name = "RelacaoProjetoSiglaGestor")
public class RelacaoProjetoSiglaGestor implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4479368544693328675L;

	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	@Column(insertable = true)
	private Long ID;

	@Column(name = "Painel_Gestor")
	private String painelGestor;

	@Column(name = "Sigla")
	private String sigla;

	@Column(name = "Nome_Projeto")
	private String nome_Projeto;

	@Column(name = "Chave")
	private String chave;

	@Column(name = "DevOps")
	private String devOps;

	private String selecionado;

	private Boolean revisar;

	private String sonar;

	// --------------------------------------------------

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

	public String getPainelGestor() {
		return painelGestor;
	}

	public void setPainelGestor(String painelGestor) {
		this.painelGestor = painelGestor;
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

	public Boolean getRevisar() {
		return revisar;
	}

	public void setRevisar(Boolean revisar) {
		this.revisar = revisar;
	}

	public String getSonar() {
		return sonar;
	}

	public void setSonar(String sonar) {
		this.sonar = sonar;
	}

	@Override
	public String toString() {
		return "RelacaoProjetoSiglaGestor [" + (ID != null ? "ID=" + ID + ", " : "")
				+ (painelGestor != null ? "painelGestor=" + painelGestor + ", " : "")
				+ (sigla != null ? "sigla=" + sigla + ", " : "")
				+ (nome_Projeto != null ? "nome_Projeto=" + nome_Projeto + ", " : "")
				+ (chave != null ? "chave=" + chave + ", " : "") + (devOps != null ? "devOps=" + devOps + ", " : "")
				+ (selecionado != null ? "selecionado=" + selecionado + ", " : "")
				+ (revisar != null ? "revisar=" + revisar + ", " : "") + (sonar != null ? "sonar=" + sonar : "") + "]";
	}

}