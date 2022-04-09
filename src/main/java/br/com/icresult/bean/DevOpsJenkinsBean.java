package br.com.icresult.bean;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;

import org.omnifaces.util.Messages;
import org.primefaces.model.StreamedContent;

import br.com.icresult.dao.complementos.AnaliseDevJenkinsDAO;
import br.com.icresult.dao.complementos.ConfigDAO;
import br.com.icresult.dao.complementos.DevOpsJenkinsDAO;
import br.com.icresult.domain.complementos.Analise_Dev_Jenkins;
import br.com.icresult.domain.complementos.Config;
import br.com.icresult.domain.complementos.DevOpsJenkins;
import br.com.icresult.model.Captura;
import br.com.icresult.nomeprojeto.Ambiente;
import br.com.icresult.nomeprojeto.ProjectName;
import br.com.icresult.nomeprojeto.Repositorio;
import br.com.icresult.util.MetodosUteis;
import br.com.icresult.util.YourThreadFactory;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.common.Logger;

/**
 * 
 * @author andre.graca
 * @since 10-03-2020
 *
 */
@ManagedBean
@SessionScoped
public class DevOpsJenkinsBean {

	private DevOpsJenkins obj;
	private DevOpsJenkinsDAO dao;
	private List<DevOpsJenkins> listaObj;
	private List<DevOpsJenkins> listaSnapshot, listaSnapshotFiltrada;
	private int total;
	private int id;
	private int totalSnapshot;
	private String painelGestor;
	private String sigla;
	private String nome_Projeto;
	private String devOps;
	private String chave;
	private String sonar;
	private String path;
	private static Logger LOG = Logger.getLogger(DevOpsJenkinsBean.class);

	/**
	 * Executa a captura de um RelacaoProjetoSiglaGestor selecionado
	 */
	public void capturar() {
		Long id = obj.getId();
		try {
			Executors.newFixedThreadPool(1, new YourThreadFactory(id)).submit(captura);
		} catch (Exception e) {
			LOG.error("Erro ao executar captura: " + e.getCause());
		}
	}

	/**
	 * 
	 * Runnable responsavel por capturar os paineis DevOps para a teabela de
	 * Desenvolvimento
	 * 
	 */
	private static Runnable captura = new Runnable() {
		public void run() {
			try {
				System.out.println("\n-----------------------\nID identificado: " + Thread.currentThread().getName());
				DevOpsJenkinsDAO daoTemp = new DevOpsJenkinsDAO();
				DevOpsJenkins tempObj = daoTemp.buscar(Long.valueOf(Thread.currentThread().getName()));
				List<Config> listaConfigSonar = new ConfigDAO().listar();
				ColetaInformacoesSonarBean sonar = null;
				String urlSonarCommunity = "https://sonarqube-ce.paas.santanderbr.corp/", loginSonar = "x212253";
				sonar = new ColetaInformacoesSonarBean(listaConfigSonar.stream().filter(
						config -> config.getUrl().equals(urlSonarCommunity) && config.getLogin().equals(loginSonar))
						.findFirst().get());
				AnaliseDevJenkinsDAO analiseDAO = new AnaliseDevJenkinsDAO();
				String chave = tempObj.getChave();
				String nomeProjeto = tempObj.getNome_Projeto();
				Captura captura = sonar.getSonarApi(chave);
				Repositorio repositorio = Repositorio.DEVOPS;
				Ambiente ambiente = Ambiente.DESENVOLVIMENTO;
				String sigla = tempObj.getSigla();
				if (captura != null) {
					Analise_Dev_Jenkins analise = new Analise_Dev_Jenkins(captura);
					analise.setSigla(tempObj.getSigla());
					analise.setNomeProjeto(nomeProjeto);
					analise.setAmbiente(ambiente.getAmbiente());
					analise.setRepositorio(repositorio.getRepositorio());
					analise.setPadraoNomeSonar(
							new ProjectName(sigla, nomeProjeto, repositorio, ambiente).getPadraoNomeProjeto());
					analise.setUrlGit(tempObj.getUrlGit());
					analiseDAO.salvar(analise);

				} else {
					LOG.info("Captura nula, pesquisando a ultima inspeção para esta chave");
					analiseDAO.buscaUltimaInspecaoComNomeProjeto(tempObj.getNome_Projeto(),
							new ProjectName(sigla, nomeProjeto, repositorio, ambiente).getPadraoNomeProjeto(),
							repositorio.getRepositorio(), ambiente.getAmbiente());
				}

			} catch (Exception e) {

				LOG.error("Erro ao capturar painel");
			}

		}
	};

	/**
	 * @return - lista de DevOpsJenkins selecionados
	 */
	public List<DevOpsJenkins> paineisSelecionados() {
		return new DevOpsJenkinsDAO().listar().stream().filter(r -> r.getSelecionado()).collect(Collectors.toList());
	}

	/**
	 * @return - lista de DevOpsJenkins filtrados e selecionados
	 */
	public List<DevOpsJenkins> paineisFiltradosSelecionados() {
		return listaSnapshotFiltrada == null ? Collections.emptyList()
				: listaSnapshotFiltrada.stream().filter(r -> r.getSelecionado()).collect(Collectors.toList());
	}

	/**
	 * @return - lista de DevOpsJenkins não selecionados
	 */
	public List<DevOpsJenkins> paineisNaoSelecionados() {
		return new DevOpsJenkinsDAO().listar().stream().filter(r -> !r.getSelecionado()).collect(Collectors.toList());
	}

	public void selecionarTodosModulosFiltrados() {
		if (listaSnapshotFiltrada != null)
			for (DevOpsJenkins relacao : listaSnapshotFiltrada) {
				relacao.setSelecionado(true);
				new DevOpsJenkinsDAO().editar(relacao);
			}
			listarInfosSnapshot();
	}

	/**
	 * Seleciona todos os modulos
	 */
	public void selecionarTodosModulos() {
		for (DevOpsJenkins relacao : paineisNaoSelecionados()) {
			relacao.setSelecionado(true);
			new DevOpsJenkinsDAO().editar(relacao);
		}
		listarInfosSnapshot();
	}

	/**
	 * Seleciona um objeto DevOpsJenkins, deixando o objeto marcado para captura
	 * em grupo de modulos
	 */
	// -------------------------------------------------------------------------------------
	public void selecionarModulo(ActionEvent evento) {
		try {
			obj = (DevOpsJenkins) evento.getComponent().getAttributes().get("meuSelect");
			System.out.println("\n\n\n\n------------ID:--------\n" + obj.getChave());
			obj.setSelecionado(!obj.getSelecionado());
			new DevOpsJenkinsDAO().editar(obj);
		} catch (Exception e) {
			Messages.addGlobalError("Erro ao Selecionar: " + obj);
		}
	}

	/**
	 * Limpa a seleção dos modulos
	 */
	public void limparSelecaoTodosModulos() {
		if (paineisFiltradosSelecionados().isEmpty()) {
			for (DevOpsJenkins relacao : paineisSelecionados()) {
				relacao.setSelecionado(false);
				new DevOpsJenkinsDAO().editar(relacao);
			}
		} else {
			for (DevOpsJenkins relacao : paineisFiltradosSelecionados()) {
				relacao.setSelecionado(false);
				new DevOpsJenkinsDAO().editar(relacao);
			}
		}
		listarInfosSnapshot();
	}

	/**
	 * Executa a captura de todos os modulos DevOpsJenkins selecionados
	 * 
	 * @param evento
	 *            - contem a lista de DevOpsJenkins para serem capturadas
	 */
	public void executarCapturaModulosSelecionados(ActionEvent evento) {
		try {
			new Thread(runnableExecutaCapturaModulosSelecionados).start();
		} catch (Exception e) {
			e.getStackTrace();
		}
	}

	Runnable runnableExecutaCapturaModulosSelecionados = new Runnable() {

		@Override
		public void run() {
			try {
				List<DevOpsJenkins> listaModulosSelecionados = new DevOpsJenkinsDAO().listar().stream()
						.filter(r -> r.getSelecionado()).collect(Collectors.toList());

				System.out.println("\n\n\n\n------------" + listaModulosSelecionados.size()
						+ " SELECIONADOS -----------\n\n\n\n\n\n");
				for (DevOpsJenkins relacao : listaModulosSelecionados) {
					Executors.newFixedThreadPool(5, new YourThreadFactory(relacao.getId())).submit(captura);
					Thread.sleep(100);
				}
			} catch (Exception e) {
				e.getStackTrace();
			}
		}
	};

	/**
	 * Limpa os atributos e objetos
	 */
	// -------------------------------------------------------------------------------------
	@PostConstruct
	public void iniciarObj() {
		obj = new DevOpsJenkins();
		dao = new DevOpsJenkinsDAO();

		sigla = "";
		nome_Projeto = "";
		devOps = "";
		chave = "";
	}

	/**
	 * Salvar um objeto DevOpsJenkins
	 */
	// -------------------------------------------------------------------------------------
	public void salvar() {
		try {
			obj.setChave(chave);
			obj.setNome_Projeto(nome_Projeto);
			obj.setSigla(sigla);
			dao.salvar(obj);
			iniciarObj();

			Messages.addGlobalInfo("Módulo Salvo: " + sigla);

		} catch (Exception e) {
			Messages.addGlobalError("Não foi possível salvar a Silga:" + e.getMessage());
			System.out.println("---\n----" + e.getCause());
			System.out.println("---\n----" + e.getMessage());
		}
	}

	// ------------------------------------------------------------------------------------------------------------------------------------------------------
	/**
	 * Criar uma lista com os objetos DevOpsJenkins que são DEVOPS
	 */
	// ------------------------------------------------------------------------------------------------------------------------------------------------------
	public void listarInfosSnapshot() {
		try {
			listaSnapshot = dao.listar();
			totalSnapshot = listaSnapshot.size();
			Messages.addGlobalInfo("Lista Atualizada!");
			System.out.println("Total: " + total);

		} catch (Exception e) {
			Messages.addGlobalError("Erro listarInfos  ");
			System.out.println("\nErro: " + e.getCause());
		}
	}

	/**
	 * Exclui um objeto DevOpsJenkins
	 */
	// -------------------------------------------------------------------------------------
	public void excluir() {

		try {
			dao.excluir(obj);
			Messages.addGlobalInfo("Removido com sucesso!!!");
		} catch (Exception e) {
			Messages.addGlobalError("Erro ao Remover: ");

		}
	}

	/**
	 * Seleciona um objeto RelacaoProjetoSiglaGestor da tabela
	 */
	// -------------------------------------------------------------------------------------
	public void selecionarObj(ActionEvent evento) {

		try {
			obj = (DevOpsJenkins) evento.getComponent().getAttributes().get("meuSelect");
			System.out.println("\n\n\n\n------------ID:--------\n" + obj.getId());

		} catch (Exception e) {
			Messages.addGlobalError("Erro ao Selecionar: ");
		}
	}

	// Editar
	// -------------------------------------------------------------------------------------------
	public void editar() {

		try {
			dao.merge(obj);
			Messages.addGlobalInfo("Editado com sucesso!!!");

		} catch (Exception e) {
			Messages.addGlobalError("Erro ao Editar");
		}
	}

	// --------------------------------------------------
	// Get e Set
	// --------------------------------------------------

	public DevOpsJenkins getObj() {
		return obj;
	}

	public void setObj(DevOpsJenkins obj) {
		this.obj = obj;
	}

	public List<DevOpsJenkins> getListaObj() {
		return listaObj;
	}

	public void setListaObj(List<DevOpsJenkins> listaObj) {
		this.listaObj = listaObj;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPainelGestor() {
		return painelGestor;
	}

	public void setPainelGestor(String painelGestor) {
		this.painelGestor = painelGestor;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public String getNome_Projeto() {
		return nome_Projeto;
	}

	public void setNome_Projeto(String nome_Projeto) {
		this.nome_Projeto = nome_Projeto;
	}

	public String getDevOps() {
		return devOps;
	}

	public void setDevOps(String devOps) {
		this.devOps = devOps;
	}

	public String getChave() {
		return chave;
	}

	public void setChave(String chave) {
		this.chave = chave;
	}

	public List<DevOpsJenkins> getListaSnapshot() {
		return listaSnapshot;
	}

	public int getTotalSnapshot() {
		return totalSnapshot;
	}

	public String getSonar() {
		return sonar;
	}

	public void setSonar(String sonar) {
		this.sonar = sonar;
	}

	public void salvarPlanilha(String caminho) {
		DevOpsJenkins modulo = new DevOpsJenkins();
		String chave, nomePainel, sigla, executou, urlGit;
		dao = new DevOpsJenkinsDAO();

		// Carrega a panilha
		Workbook planilha = null;

		try {
			planilha = Workbook.getWorkbook(new File(caminho));

			Sheet abaComItensParaInclusao = planilha.getSheet(0);

			int qtdLinhasComDadosParaInclusao = abaComItensParaInclusao.getRows();

			if (qtdLinhasComDadosParaInclusao > 0) {
				limpaTabelaDevOpsJenkins();
			}

			for (int i = 1; i < qtdLinhasComDadosParaInclusao; i++) {
				Cell celulaSigla = abaComItensParaInclusao.getCell(0, i);
				Cell celulaNomePainel = abaComItensParaInclusao.getCell(1, i);
				Cell celulaUrlGit = abaComItensParaInclusao.getCell(2, i);
				Cell celulaExecutou = abaComItensParaInclusao.getCell(3, i);
				Cell celulaChavePainel = abaComItensParaInclusao.getCell(4, i);

				chave = celulaChavePainel.getContents().toString().trim();
				nomePainel = celulaNomePainel.getContents().toString().trim();
				sigla = celulaSigla.getContents().toUpperCase().toString().trim();
				executou = celulaExecutou.getContents().toUpperCase().toString().trim();
				urlGit = celulaUrlGit.getContents().toString().trim();

				if (nomePainel.isEmpty()) {
					break;
				} else {
					modulo.setChave(chave);
					modulo.setNome_Projeto(nomePainel);
					modulo.setSigla(sigla);
					modulo.setSelecionado(executou.equals("OK"));
					modulo.setUrlGit(urlGit);

					dao.salvar(modulo);
				}
			}
			Messages.addGlobalInfo("Execução realizada com sucesso");
		} catch (Exception e) {
			Messages.addGlobalError("Não foi possivel salvar a planilha");
			e.printStackTrace();
		}
	}

	private void limpaTabelaDevOpsJenkins() {
		dao = new DevOpsJenkinsDAO();
		for (DevOpsJenkins jenkins : dao.listar()) {
			dao.excluir(jenkins);
		}
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public StreamedContent getExemploExcel() {
		return new MetodosUteis().getExcelComoStreamedContent(
				"/resources/exemplos_excel/exemplo_carga_execucao_jenkins.xls", "exemplo_carga_execucao_jenkins.xls");
	}

	public List<DevOpsJenkins> getListaSnapshotFiltrada() {
		return listaSnapshotFiltrada;
	}

	public void setListaSnapshotFiltrada(List<DevOpsJenkins> listaSnapshotFiltrada) {
		this.listaSnapshotFiltrada = listaSnapshotFiltrada;
	}
}
