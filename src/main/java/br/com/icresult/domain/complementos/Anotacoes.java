package br.com.icresult.domain.complementos;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * 
 * @author andre.graca
 * @since 09-01-19
 */
@Entity
public class Anotacoes extends GenericDomain{

	private static final long serialVersionUID = -7203590183684085286L;
	
	@Column(length = 500)
	private String mensagem;
	
	@Column
	private String usuario;
	
	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataInclusao = new Date (Calendar.getInstance().getTimeInMillis());

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public Date getDataInclusao() {
		return dataInclusao;
	}

	public void setDataInclusao(Date dataInclusao) {
		this.dataInclusao = dataInclusao;
	}
}
