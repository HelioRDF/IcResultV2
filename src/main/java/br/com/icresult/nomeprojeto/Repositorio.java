package br.com.icresult.nomeprojeto;

public enum Repositorio {
	
	DEVOPS("DEVOPS"),
	INDETERMINADA("INDETERMINADA"),
	RTC("RTC"),
	SONAR_ESPANHA("SONAR ESPANHA"),
	SONAR_BRASIL("SONAR BRASIL"),
	GIT("GIT");
	
	public String repositorio;
	
	Repositorio(String localizacao){
		this.repositorio = localizacao;
	}
	
	public String getRepositorio() {
		return repositorio;
	}
}
