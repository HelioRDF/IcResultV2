package br.com.icresult.domain.complementos;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.icresult.bean.LoginBean;

@Entity
public class LogApp extends GenericDomain {
	
	public LogApp() {}
	
	public LogApp(String mensagem) {
		this.mensagem = mensagem;
		this.usuario = LoginBean.getUsuarioLogado().getEmail();
	}
	
	private static final long serialVersionUID = -3642438867982693825L;
	
	@Column(length=1000)
	private String mensagem;
	
	@Column
	private String usuario;
	
	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataAtual = Date.from(LocalDateTime.now().atZone(ZoneId.of("Etc/GMT+3")).toInstant());
	
	@Override
	public String toString() {
		return "LogApp [mensagem=" + mensagem + ", usuario=" + usuario + ", dataAtual=" + dataAtual + "]";
	}

	public String getMensagem() {
		return mensagem;
	}

	public String getUsuario() {
		return usuario;
	}
	
	public Date getDataAtual() {
		return dataAtual;
	}


}
