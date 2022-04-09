package br.com.icresult.domain.complementos;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * -Classe POJO ControleGitSonar, e entity do DB via Hibernate.
 * 
 * @author andre.graca
 * @version v1.0
 * @since 13-06-2019
 * 
 */

@Entity
public class ControleGitSonar extends GenericDomain {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6514655642275512105L;

	@Column(nullable = false)
	private String sigla;

	@Column(nullable = true)
	private String nomeSistema;

	@Column(nullable = true)
	private String caminho;

	@Column(nullable = true, name = "pacote")
	private String pacoteChaveNomePainelSonar;

	@Column(nullable = true)
	private String author;

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

	@Column
	private String usuarioGit;

	@Column
	private boolean selecionado;

	@Column
	private String url;

	@Column
	private String branch;

	@Column
	private String erroGitPull;

	// --------------------------------------------------

	public Long getId() {
		return super.getCodigo();
	}

	public String getChave() {
		return pacoteChaveNomePainelSonar;
	}

	public String getNomePainel() {
		return pacoteChaveNomePainelSonar;
	}

	public boolean getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	public String getNomeArquivo() {
		return nomeArquivo;
	}

	public String getUsuarioGit() {
		return usuarioGit;
	}

	public void setUsuarioGit(String usuarioGit) {
		this.usuarioGit = usuarioGit;
	}

	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}

	public String getSigla() {
		return sigla;
	}

	public boolean isAlteracao() {
		return alteracao;
	}

	public void setAlteracao(boolean alteracao) {
		this.alteracao = alteracao;
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

	public String getPacoteChaveNomePainelSonar() {
		return pacoteChaveNomePainelSonar;
	}

	public void setPacoteChaveNomePainelSonar(String pacoteChaveNomePainelSonar) {
		this.pacoteChaveNomePainelSonar = pacoteChaveNomePainelSonar;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getErroGitPull() {
		return erroGitPull;
	}

	public void setErroGitPull(String erroGitPull) {
		this.erroGitPull = erroGitPull;
	}

	// --------------------------------------------------

	@Override
	public String toString() {
		return "[Pacote: " + pacoteChaveNomePainelSonar + " Usuario Git: " + usuarioGit + "]";
	}

}
