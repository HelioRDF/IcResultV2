package br.com.icresult.util;

/**
 * Classe que auxilia na criação da apresentação da comparação de nomes dos
 * módulos
 * 
 * @author andre.graca
 *
 */
public class ModuloOrigem {

	private String nomeModulo;
	private String origemDoModulo;

	public ModuloOrigem() {

	}

	public ModuloOrigem(String nomeModulo, String origemDoModulo) {
		this.nomeModulo = nomeModulo;
		this.origemDoModulo = origemDoModulo;
	}

	public String getNomeModulo() {
		return nomeModulo;
	}

	public String getOrigemDoModulo() {
		return origemDoModulo;
	}

}
