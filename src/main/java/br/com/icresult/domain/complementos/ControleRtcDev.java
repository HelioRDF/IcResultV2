package br.com.icresult.domain.complementos;

import java.util.Comparator;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.icresult.nomeprojeto.Ambiente;
import br.com.icresult.nomeprojeto.Repositorio;

/**
 * - Classe POJO ControleRtcDev, e entity do DB via Hibernate.
 * 
 * @author helio.franca
 * @version v1.8
 * @since 12-07-2018
 * 
 */

@Entity
public class ControleRtcDev extends GenericDomain {

	/**
	 * 
	 */

	private static final long serialVersionUID = 3542998853452632826L;

	public static Comparator<ControleRtcDev> getComparadorPorDataCommit() {
		return new Comparator<ControleRtcDev>() {
			@Override
			public int compare(ControleRtcDev o1, ControleRtcDev o2) {
				return -1 * Boolean.compare(o1.isAlteracao(), o2.isAlteracao());
			}
		};
	}

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

	@Column(length = 500)
	private String chave;

	@Column(nullable = true)
	private String versao;

	@Column()
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataSonar;

	@Column
	private String selecionado;

	@Column
	private Boolean capturado;

	private String resultadoUltimaInspecao;

	private String repositorio = Repositorio.RTC.getRepositorio();

	private String ambiente = Ambiente.DESENVOLVIMENTO.getAmbiente();

	private String nomeProjetoPadronizado;

	// --------------------------------------------------

	public Long getId() {
		return super.getCodigo();
	}

	public String getNomePainel() {
		return nomeSistema;
	}

	public String getChave() {
		return chave;
	}

	public String getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(String selecionado) {
		this.selecionado = selecionado;
	}

	public Boolean getCapturado() {
		return capturado;
	}

	public void setCapturado(Boolean capturado) {
		this.capturado = capturado;
	}

	public void setChave(String chave) {
		this.chave = chave;
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

	public void setResultadoUltimaInspecao(String resultado) {
		this.resultadoUltimaInspecao = resultado;
	}

	public String getResultadoUltimaInspecao() {
		return resultadoUltimaInspecao;
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

	public void setNomeProjetoPadronizado(String nomeProjetoPadronizado) {
		this.nomeProjetoPadronizado = nomeProjetoPadronizado;
	}

	public String getNomeProjetoPadronizado() {
		return nomeProjetoPadronizado;
	}

	// --------------------------------------------------

}
