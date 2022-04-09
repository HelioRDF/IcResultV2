package br.com.icresult.domain.complementos;

import javax.persistence.Column;
import javax.persistence.Entity;

@SuppressWarnings("serial")
@Entity(name = "Config")
public class Config extends GenericDomainID {
	public Config() {
	}

	@Column(name = "Url")
	private String url;

	@Column(name = "Login")
	private String login;

	@Column(name = "acessoSonar")
	private String acessoSonar;
	
	@Column(name = "padrao")
	private boolean padrao;

	// toString / Hash / Equal
	// --------------------------------------------------

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((acessoSonar == null) ? 0 : acessoSonar.hashCode());
		result = prime * result + ((login == null) ? 0 : login.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "Url=" + url + " | Login=" + login ;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Config other = (Config) obj;
		if (acessoSonar == null) {
			if (other.acessoSonar != null)
				return false;
		} else if (!acessoSonar.equals(other.acessoSonar))
			return false;
		if (login == null) {
			if (other.login != null)
				return false;
		} else if (!login.equals(other.login))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}

	// Get e Set
	// --------------------------------------------------

	public String getUrl() {
		StringBuilder URL = new StringBuilder(url);
		if (url.charAt(url.length() - 1) != '/') {
			URL.append("/");
		}
		return URL.toString();
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getAcessoSonar() {
		return acessoSonar;
	}

	public void setAcessoSonar(String acessoSonar) {
		this.acessoSonar = acessoSonar;
	}
	
	public boolean getPadrao() {
		return padrao;
	}
	
	public void setPadrao(boolean padrao) {
		this.padrao = padrao;
	}

	// --------------------------------------------------

}
