package br.com.icresult.nomeprojeto;

public enum Ambiente {
	
	DESENVOLVIMENTO("HG"),
	HOMOLOGACAO("HK");
	
	public String ambiente;
	
	Ambiente(String branch){
		this.ambiente = branch;
	}
	
	public String getAmbiente() {
		return ambiente;
	}
}
