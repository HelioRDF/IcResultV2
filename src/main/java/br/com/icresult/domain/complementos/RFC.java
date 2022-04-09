package br.com.icresult.domain.complementos;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * [ Detalhes... ]
 * -Classe Pojo do Objeto RFC e Entity do banco de dados via Hibernate.
 * -Entity - Diz que a classe é uma entidade do hibernate
 * 
 * -Column( | length = Tamanho do campo | name = define o nome no banco |
 * nullable = Diz se o campo pode ou não ser nulo "True ou False"); | precision
 * = Define quantidade de digitos | scale= Define quantos digitos ficam após a
 * virgula
 * 
 * -JoinColumn - Permite personalizar colunas que são chaves estrangeiras
 * 
 * @author helio.franca
 * @version v1.7
 * @since N/A
 * 
 */

@Entity
public class RFC extends GenericDomain {


	private static final long serialVersionUID = -6088889189120103106L;

	@Column(nullable = false)
	private String sigla;

	@Column(nullable = false)
	private String codProj;

	@Column(nullable = false)
	private String codRfc;

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataCadastro;

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataInspecao;
	
	@Column(nullable = true)
	private String dataPro;

	@Column
	@Lob
	private String observacao;

	@Column(nullable = false)
	private String status;

	@Column(nullable = false)
	private String codVazio;

	@Column(nullable = true)
	private int codInspecao;

	@Column(nullable = true)
	private String  lider;
	
	@Column(nullable = true)
	private String  gestorEntrega;

	@Column(nullable = true)
	private String inspecionar;
	
	@Column(nullable = true)
	private String emailLider;

	// --------------------------------------------------
	public Date getDataCadastro() {
		return dataCadastro;
	}
	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}
	public String getEmailLider() {
		return emailLider;
	}
	public void setEmailLider(String emailLider) {
		this.emailLider = emailLider;
	}
	public String getSigla() {
		return sigla;
	}
	public void setSigla(String sigla) {
		this.sigla = sigla;
	}
	public String getCodProj() {
		return codProj;
	}
	public void setCodProj(String codProj) {
		this.codProj = codProj;
	}
	public String getCodRfc() {
		return codRfc;
	}
	public void setCodRfc(String codRfc) {
		this.codRfc = codRfc;
	}
	public Date getDataInspecao() {
		return dataInspecao;
	}
	public void setDataInspecao(Date dataInspecao) {
		this.dataInspecao = dataInspecao;
	}
	public String getObservacao() {
		return observacao;
	}
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCodVazio() {
		return codVazio;
	}
	public void setCodVazio(String codVazio) {
		this.codVazio = codVazio;
	}
	public int getCodInspecao() {
		return codInspecao;
	}
	public void setCodInspecao(int codInspecao) {
		this.codInspecao = codInspecao;
	}
	public String getInspecionar() {
		return inspecionar;
	}
	public void setInspecionar(String inspecionar) {
		this.inspecionar = inspecionar;
	}
	public String getLider() {
		return lider;
	}
	public void setLider(String lider) {
		this.lider = lider;
	}
	public String getGestorEntrega() {
		return gestorEntrega;
	}
	public void setGestorEntrega(String gestorEntrega) {
		this.gestorEntrega = gestorEntrega;
	}
	public String getDataPro() {
		return dataPro;
	}
	public void setDataPro(String dataPro) {
		this.dataPro = dataPro;
	}
	
}