package br.com.icresult.domain.complementos;

import java.util.Comparator;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;

import br.com.icresult.nomeprojeto.Ambiente;
import br.com.icresult.nomeprojeto.ProjectName;
import br.com.icresult.nomeprojeto.Repositorio;

@Entity
public class SiglasGit extends GenericChaveSonar {

	public SiglasGit() {

	}

	public SiglasGit(String sigla, String nomeProjeto) {
		setSigla(sigla);
		setNome_Projeto(nomeProjeto);
	}

	private static final long serialVersionUID = 1L;

	@Column()
	private String selecionado;

	@Column()
	private String caminho;

	@Column(nullable = true)
	private boolean alteracao;

	@Column(name = "data_commit")
	private Date dataCommit;

	private String nomeProjetoPadronizado;

	private String ambiente = Ambiente.DESENVOLVIMENTO.getAmbiente();

	private String repositorio = Repositorio.GIT.getRepositorio();

	// -------Getters e Setters

	@Override
	public String getChave() {
		return super.getChave();
	}

	public String getNomePainel() {
		return super.getNome_Projeto();
	}

	@Override
	public Long getId() {
		return super.getId();
	}

	public String getCaminho() {
		return caminho;
	}

	public boolean isAlteracao() {
		return alteracao;
	}

	public void setAlteracao(boolean alteracao) {
		this.alteracao = alteracao;
	}

	public void setCaminho(String caminho) {
		this.caminho = caminho;

	}

	public String getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(String selecionado) {
		this.selecionado = selecionado;
	}

	public void setDataCommit(Date dataCommit) {
		this.dataCommit = dataCommit;
	}

	public Date getDataCommit() {
		return dataCommit;
	}

	@Override
	public String toString() {
		return "SiglasGit [selecionado=" + selecionado + ", caminho=" + caminho + "]";
	}

	public static Comparator<SiglasGit> getComparadorPorDataCommit() {
		return new Comparator<SiglasGit>() {
			@Override
			public int compare(SiglasGit o1, SiglasGit o2) {
				Date dataCommit1 = o1.getDataCommit();
				Date dataCommit2 = o2.getDataCommit();
				if (dataCommit1 == null || dataCommit2 == null) {
					return 1;
				}
				return -1 * dataCommit1.compareTo(dataCommit2);
			}
		};
	}

	public String getNomeProjetoPadronizado() {
		return nomeProjetoPadronizado;
	}

	public void setNomeProjetoPadronizado(ProjectName padraoNomeProjetoSonar) {
		this.nomeProjetoPadronizado = padraoNomeProjetoSonar.getPadraoNomeProjeto();
	}

	public String getAmbiente() {
		return ambiente;
	}

	public void setAmbiente(String ambiente) {
		this.ambiente = ambiente;
	}

	public String getRepositorio() {
		return repositorio;
	}

	public void setRepositorio(String repositorio) {
		this.repositorio = repositorio;
	}
}
