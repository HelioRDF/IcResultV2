package br.com.icresult.domain.complementos;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * -Classe POJO ControleRtcHK, e entity do DB via Hibernate.
 * 
 * @author helio.franca
 * @version v1.8
 * @since 12-07-2018
 * 
 */

@Entity
public class ControleRtcHK extends GenericDomain {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7732726881086691704L;

	@Column(nullable = false)
	private String sigla;

	@Column(nullable = true)
	private String nomeSistema;

	@Column(nullable = true)
	private String caminho;

	@Column()
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataCommit;

	@Column()
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataCommitAnt;

	@Column
	@Lob
	private String descricaoLog;

	@Column()
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataVerificacao;

	@Column(nullable = true)
	private String nomeArquivo;

	@Column(nullable = true)
	private boolean alteracao;

	private String comitadores;

	// --------------------------------------------------

	public boolean isAlteracao() {
		return alteracao;
	}

	public void setAlteracao(boolean alteracao) {
		this.alteracao = alteracao;
	}

	public String getNomeArquivo() {
		return nomeArquivo;
	}

	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public String getNomeSistema() {
		return nomeSistema;
	}

	public void setNomeSistema(String nomeSistema) {
		this.nomeSistema = nomeSistema;
	}

	public String getCaminho() {
		return caminho;
	}

	public void setCaminho(String caminho) {
		this.caminho = caminho;
	}

	public Date getDataCommit() {
		return dataCommit;
	}

	public void setDataCommit(Date dataCommit) {
		this.dataCommit = dataCommit;
	}

	public String getDescricaoLog() {
		return descricaoLog;
	}

	public void setDescricaoLog(String descricaoLog) {
		this.descricaoLog = descricaoLog;
	}

	public Date getDataVerificacao() {
		return dataVerificacao;
	}

	public void setDataVerificacao(Date dataVerificacao) {
		this.dataVerificacao = dataVerificacao;
	}

	public Date getDataCommitAnt() {
		return dataCommitAnt;
	}

	public void setDataCommitAnt(Date dataCommitAnt) {
		this.dataCommitAnt = dataCommitAnt;
	}

	public void setComitadores(String comitadores) {
		this.comitadores = comitadores;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getComitadores() {
		return comitadores;
	}

	// --------------------------------------------------

}
