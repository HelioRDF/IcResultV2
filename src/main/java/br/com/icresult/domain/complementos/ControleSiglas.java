package br.com.icresult.domain.complementos;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * -Classe POJO p/ controle de Siglas, e Entity do banco de dados via Hibernate.
 * 
 * @author helio.franca
 * @version v1.7
 * @since N/A
 * 
 */

/**
 * @author x205451
 *
 */
@Entity
public class ControleSiglas extends GenericDomain {

	private static final long serialVersionUID = 9077030507376334373L;

	@Column(nullable = true)
	private String sigla;

	@Column(nullable = true)
	private String nomeSistema;

	@Column(nullable = true)
	private String linguagem;

	@Column(nullable = true)
	private String mapa;

	@Column(nullable = true)
	private String espanha;

	@Column(nullable = true)
	private String rtc;

	@Column(nullable = true)
	private String git;

	@Column(nullable = true)
	private String devOps;

	@Column(nullable = true)
	private String validaAdocaoDevOps;

	@Column(nullable = true)
	private String projetoArsenal;

	@Column(nullable = true)
	private String teamArea;

	@Column(nullable = true)
	private String workspace;

	@Column(nullable = true)
	private String inclusaoExclusao;

	@Column(nullable = true)
	private String nivel1;

	@Column(nullable = true)
	private String nivel2;

	@Column(nullable = true)
	@Lob
	private String instrucoes;

	@Column(nullable = true)
	private String liderTeste;

	@Column()
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataCadastro;

	@Column(nullable = true)
	private String nomeArquivo;

	// --------------------------------------------------

	public String getSigla() {
		return sigla;
	}

	public String getNivel1() {
		return nivel1;
	}

	public void setNivel1(String nivel1) {
		this.nivel1 = nivel1;
	}

	public String getNivel2() {
		return nivel2;
	}

	public void setNivel2(String nivel2) {
		this.nivel2 = nivel2;
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

	public String getLinguagem() {
		return linguagem;
	}

	public void setLinguagem(String linguagem) {
		this.linguagem = linguagem;
	}

	public String getMapa() {
		return mapa;
	}

	public void setMapa(String mapa) {
		this.mapa = mapa;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public String getNomeArquivo() {
		return nomeArquivo;
	}

	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}

	public String getEspanha() {
		return espanha;
	}

	public void setEspanha(String espanha) {
		this.espanha = espanha;
	}

	public String getRtc() {
		return rtc;
	}

	public void setRtc(String rtc) {
		this.rtc = rtc;
	}

	public String getGit() {
		return git;
	}

	public void setGit(String git) {
		this.git = git;
	}

	public String getDevOps() {
		return devOps;
	}

	public void setDevOps(String devOps) {
		this.devOps = devOps;
	}

	public String getProjetoArsenal() {
		return projetoArsenal;
	}

	public void setProjetoArsenal(String projetoArsenal) {
		this.projetoArsenal = projetoArsenal;
	}

	public String getWorkspace() {
		return workspace;
	}

	public void setWorkspace(String workspace) {
		this.workspace = workspace;
	}

	public String getInclusaoExclusao() {
		return inclusaoExclusao;
	}

	public void setInclusaoExclusao(String inclusaoExclusao) {
		this.inclusaoExclusao = inclusaoExclusao;
	}

	public String getTeamArea() {
		return teamArea;
	}

	public void setTeamArea(String teamArea) {
		this.teamArea = teamArea;
	}

	public void setValidaAdocaoDevOps(String validaAdocaoDevOps) {
		this.validaAdocaoDevOps = validaAdocaoDevOps;
	}

	public String getValidaAdocaoDevOps() {
		return validaAdocaoDevOps;
	}

	public String getInstrucoes() {
		return instrucoes;
	}

	public void setInstrucoes(String instrucoes) {
		this.instrucoes = instrucoes;
	}

	public void setLiderTeste(String liderTeste) {
		this.liderTeste = liderTeste;
	}

	public String getLiderTeste() {
		return liderTeste;
	}

	@Override
	public String toString() {
		return "ControleSiglas [sigla=" + sigla + ", nomeSistema=" + nomeSistema + ", linguagem=" + linguagem
				+ ", mapa=" + mapa + ", espanha=" + espanha + ", rtc=" + rtc + ", git=" + git + ", devOps=" + devOps
				+ ", validaAdocaoDevOps=" + validaAdocaoDevOps + ", projetoArsenal=" + projetoArsenal + ", teamArea="
				+ teamArea + ", workspace=" + workspace + ", inclusaoExclusao=" + inclusaoExclusao + ", nivel1="
				+ nivel1 + ", nivel2=" + nivel2 + ", instrucoes=" + instrucoes + ", liderTeste=" + liderTeste
				+ ", dataCadastro=" + dataCadastro + ", nomeArquivo=" + nomeArquivo + "]";
	}

	// --------------------------------------------------

}
