package br.com.icresult.domain.complementos;

import java.util.Comparator;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.icresult.nomeprojeto.Ambiente;
import br.com.icresult.nomeprojeto.ProjectName;
import br.com.icresult.nomeprojeto.Repositorio;

/**
 * -Classe POJO ControleGitDev, e entity do DB via Hibernate.
 * 
 * @author helio.franca
 * @version v2.1.3
 * @since 08-08-2018
 * 
 */

@Entity
public class ControleGitDev extends GenericDomain {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3100048312260295449L;
	public static final String CONTA_PAULA = "XB201520";
	public static final String CONTA_LUIS = "XI324337";
	public static final String CONTA_HELIO = "XB205451";

	public static Comparator<ControleGitDev> getComparadorPorAlteracao() {
		return new Comparator<ControleGitDev>() {
			@Override
			public int compare(ControleGitDev o1, ControleGitDev o2) {
				Date dataCommit1 = o1.getDataCommit();
				Date dataCommit2 = o2.getDataCommit();
				if (dataCommit1 == null || dataCommit2 == null) {
					return 1;
				}
				return -1 * dataCommit1.compareTo(dataCommit2);
			}
		};
	}

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
	private String versao;

	@Column(nullable = true)
	private Date dataSonar;

	@Column(nullable = true)
	private boolean alteracao;

	@Column
	private String usuarioGit;

	@Column
	private String selecionado;

	@Column
	private Boolean capturado;

	@Column
	private String url;

	@Column
	private String branch;

	private String resultadoUltimaInspecao;

	@Column
	private String erroGitPull;

	private String repositorio = Repositorio.GIT.getRepositorio();

	private String ambiente = Ambiente.DESENVOLVIMENTO.getAmbiente();

	private String nomeProjetoPadronizado;

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

	public String getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(String selecionado) {
		this.selecionado = selecionado;
	}

	public void setCapturado(Boolean capturado) {
		this.capturado = capturado;
	}

	public Boolean isCapturado() {
		return capturado;
	}

	public Boolean getCapturado() {
		return capturado;
	}

	public String getNomeArquivo() {
		return nomeArquivo;
	}

	public String getVersao() {
		return versao;
	}

	public void setVersao(String versao) {
		this.versao = versao;
	}

	public Date getDataSonar() {
		return dataSonar;
	}

	public void setDataSonar(Date dataSonar) {
		this.dataSonar = dataSonar;
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

	@Override
	public String toString() {
		return "[Pacote: " + pacoteChaveNomePainelSonar + " Usuario Git: " + usuarioGit + "]";
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
		return erroGitPull == null ? "" : erroGitPull;
	}

	public void setErroGitPull(String erroGitPull) {
		this.erroGitPull = erroGitPull;
	}

	public boolean getAcessoGitOk() {
		return erroGitPull == null ? false : erroGitPull.isEmpty();
	}

	public String getResultadoUltimaInspecao() {
		return resultadoUltimaInspecao;
	}

	public void setResultadoUltimaInspecao(String resultadoUltimaInspecao) {
		this.resultadoUltimaInspecao = resultadoUltimaInspecao;
	}

	public String getRepositorio() {
		return repositorio;
	}

	public void setRepositorio(String repositorio) {
		this.repositorio = repositorio;
	}

	public String getAmbiente() {
		return ambiente;
	}

	public void setAmbiente(String ambiente) {
		this.ambiente = ambiente;
	}

	public String getNomeProjetoPadronizado() {
		return nomeProjetoPadronizado;
	}

	public void setNomeProjetoPadronizado(ProjectName padraoNomeProjetoSonar) {
		this.nomeProjetoPadronizado = padraoNomeProjetoSonar.getPadraoNomeProjeto();
	}

}
