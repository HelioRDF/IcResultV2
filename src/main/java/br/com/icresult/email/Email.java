package br.com.icresult.email;

public class Email {

	private String resultadoHtml;
	private String titulo;
	private String chave;
	private String emailOrigem;
	private String emailDestino;
	
	public Email() {
		
	}
	
	public Email(String resultadoHtml, String titulo, String chave, String emailOrigem, String emailDestino) {
		this.resultadoHtml = resultadoHtml;
		this.titulo = titulo;
		this.chave = chave;
		this.emailOrigem = emailOrigem;
		this.emailDestino = emailDestino;
	}



	public String getResultadoHtml() {
		return resultadoHtml;
	}

	public void setResultadoHtml(String resultadoHtml) {
		this.resultadoHtml = resultadoHtml;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getChave() {
		return chave;
	}

	public void setChave(String chave) {
		this.chave = chave;
	}

	public String getEmailOrigem() {
		return emailOrigem;
	}

	public void setEmailOrigem(String emailOrigem) {
		this.emailOrigem = emailOrigem;
	}

	public String getEmailDestino() {
		return emailDestino;
	}

	public void setEmailDestino(String emailDestino) {
		this.emailDestino = emailDestino;
	}

}
