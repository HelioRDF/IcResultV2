package br.com.icresult.domain.complementos;

import javax.persistence.Column;
import javax.persistence.Entity;

@SuppressWarnings("serial")
@Entity(name = "ConfigGit")
public class ConfigGit extends GenericDomainID {
	public ConfigGit() {
	}

	@Column(name = "Url")
	private String url;

	@Column(name = "Login")
	private String login;

	@Column(name = "acessoGit")
	private String acessoGit;

	// toString / Hash / Equal
	// --------------------------------------------------

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((acessoGit == null) ? 0 : acessoGit.hashCode());
		result = prime * result + ((login == null) ? 0 : login.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "Config [url=" + url + ", login=" + login + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConfigGit other = (ConfigGit) obj;
		if (acessoGit == null) {
			if (other.acessoGit != null)
				return false;
		} else if (!acessoGit.equals(other.acessoGit))
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
		return url;
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
		return acessoGit;
	}

	public void setAcessoSonar(String acessoSonar) {
		this.acessoGit = acessoSonar;
	}

	// --------------------------------------------------

}
