package br.com.icresult.nomeprojeto;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import jxl.common.Logger;

public class ProjectName {

	private static Logger log = Logger.getLogger(ProjectName.class);

	@NotNull
	private String sigla;
	@NotNull
	@Size(min = 2)
	private String nomePainel;
	@NotNull
	private Repositorio repositorio;
	@NotNull
	private Ambiente ambiente;
	private Validator validator;

	/***
	 * 
	 * @param sigla       - sigla do painel
	 * @param nomePainel  - nome do painel no SONAR
	 * @param repositorio - Enum Localizacao
	 * @param ambiente    - Enum branch
	 * @return - retorna a concatenação entre sigla + painel + localizacao + branch
	 */
	public ProjectName(String sigla, String nomePainel, Repositorio repositorio, Ambiente ambiente) {
		this.sigla = sigla;
		this.nomePainel = nomePainel;
		this.repositorio = repositorio;
		this.ambiente = ambiente;
		this.validator = Validation.buildDefaultValidatorFactory().getValidator();
	}

	

	public String getPadraoNomeProjeto() {
		StringBuilder nomePadronizado = new StringBuilder();
		Set<ConstraintViolation<ProjectName>> restricoes = this.validator.validate(this);
		if (restricoes.isEmpty()) {
			if (!sigla.isEmpty()) {
				nomePadronizado.append(sigla.toUpperCase());
				nomePadronizado.append("-");
			}
			nomePadronizado.append(ambiente.getAmbiente().toUpperCase());
			nomePadronizado.append("-");
			nomePadronizado.append(repositorio.getRepositorio().toUpperCase());
			nomePadronizado.append("-");
			nomePadronizado.append(nomePainel.toUpperCase().replaceAll("\\s", "_"));

		} else {
			for (ConstraintViolation<ProjectName> restricao : restricoes) {
				log.error("Restrição de nome do projeto não atendida: " + restricao.getMessage());
			}
			throw new RuntimeException("Falhou na validação do nome");
		}
		return nomePadronizado.toString();
	}
}
