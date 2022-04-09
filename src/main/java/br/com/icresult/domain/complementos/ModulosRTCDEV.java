package br.com.icresult.domain.complementos;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * -Classe POJO RelacaoProjetoSiglaGestor entity do DB via Hibernate.
 * 
 * @author helio.franca
 * @version v2.4.4
 * @since 18-10-2018
 * 
 */

@Entity(name = "Relacao_Modulos_RTC_Dev")
public class ModulosRTCDEV extends GenericChaveSonar {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1551670027566815279L;
	
	@Column
	private String selecionado;
	
	@Column
	private Boolean capturado;
	
	@Column
	private Boolean alterada;
	
	@Column
	private String caminho;
	
	@Column()
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataCommit;
	
	@Column()
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataCommitAnt;

	private String autor;
	
	@Override
	public String getChave() {
		return super.getChave();
	}
	
	@Override
	public Long getId() {
		return super.getId();
	}
	
	public String getNomePainel() {
		return getNome_Projeto();
	}
	
	public Date getDataCommitAnt() {
		return dataCommitAnt;
	}
	
	public void setDataCommitAnt(Date dataCommitAnt) {
		this.dataCommitAnt = dataCommitAnt;
	}
	
	public void setDataCommit(Date dataCommit) {
		this.dataCommit = dataCommit;
	}
	
	public Date getDataCommit() {
		return dataCommit;
	}
	
	public String getCaminho() {
		return caminho;
	}
	
	public void setCaminho(String caminho) {
		this.caminho = caminho;
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
	
	public void setAlterada(Boolean alterada) {
		this.alterada = alterada;
	}

	public Boolean getAlterada() {
		return alterada;
	}

	public void setAutor(String autor) {
		this.autor = autor;
	}
	
	public String getAutor() {
		return autor;
	}

	@Override
	public String toString() {
		return "ModulosRTCDEV [selecionado=" + selecionado + ", capturado=" + capturado + ", alterada="
				+ alterada + ", caminho=" + caminho + ", dataCommit=" + dataCommit + ", dataCommitAnt=" + dataCommitAnt
				+ ", autor=" + autor + "]";
	}
}
