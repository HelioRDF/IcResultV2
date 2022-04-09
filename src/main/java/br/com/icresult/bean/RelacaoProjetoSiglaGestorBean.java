package br.com.icresult.bean;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;

import org.json.JSONException;
import org.omnifaces.util.Messages;
import org.primefaces.model.StreamedContent;

import br.com.icresult.dao.complementos.AnaliseDevDAO;
import br.com.icresult.dao.complementos.Analise_HomologacaoDAO;
import br.com.icresult.dao.complementos.ConfigDAO;
import br.com.icresult.dao.complementos.ControleSiglasDAO;
import br.com.icresult.dao.complementos.RelacaoProjetoSiglaGestorDAO;
import br.com.icresult.domain.complementos.Analise_Dev_Mensal;
import br.com.icresult.domain.complementos.Analise_Homologacao;
import br.com.icresult.domain.complementos.Config;
import br.com.icresult.domain.complementos.RelacaoProjetoSiglaGestor;
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
 * @since 09-01-19
 *
 */
@ManagedBean
@SessionScoped
public class RelacaoProjetoSiglaGestorBean {

	private RelacaoProjetoSiglaGestor obj;
	private RelacaoProjetoSiglaGestorDAO dao;
	private List<RelacaoProjetoSiglaGestor> listaObj;
	private List<RelacaoProjetoSiglaGestor> listaSnapshot, listaSnapshotFiltrada;
	private int total;
	private int id;
	private int totalSnapshot;
	private String painelGestor;
	private String sigla;
	private String nome_Projeto;
	private String devOps;
	private String chave;
	private String sonar;
	private boolean tipoCaptura;// tipoCaptura == true -> Salva captura na tabela de Homologação
	// tipoCaptura == false -> Salva captura na tabela de Desenvolvimento
	private String path;
	private static Logger LOG = Logger.getLogger(RelacaoProjetoSiglaGestorBean.class);

	/**
	 * Executa a captura de um RelacaoProjetoSiglaGestor selecionado
	 */
	public void capturar() {
		Long id = obj.getId();
		System.out.println("\n\nID:" + id + "\n");
		try {
			if (tipoCaptura) {
				Executors.newFixedThreadPool(1, new YourThreadFactory(id)).submit(capturaHomologacao);

			} else {
				Executors.newFixedThreadPool(1, new YourThreadFactory(id)).submit(capturaDesenvolvimento);

			}
		} catch (Exception e) {
			LOG.error("Erro ao executar captura: " + e.getCause());
		}
	}

	/**
	 * 
	 * Runnable para capturar Paineis DevOps para a tabela de Homologação
	 * 
	 */
	private static Runnable capturaHomologacao = new Runnable() {

		public void run() {
			try {
				System.out.println("\n-----------------------\nID identificado: " + Thread.currentThread().getName());
				RelacaoProjetoSiglaGestorDAO daoTemp = new RelacaoProjetoSiglaGestorDAO();
				RelacaoProjetoSiglaGestor tempObj = daoTemp.buscar(Long.valueOf(Thread.currentThread().getName()));
				ColetaInformacoesSonarBean sonar = new ColetaInformacoesSonarBean();
				Captura captura = sonar.getSonarApi(tempObj.getChave());
				if (captura != null) {
					Analise_HomologacaoDAO analiseDAO = new Analise_HomologacaoDAO();
					Analise_Homologacao analise = new Analise_Homologacao(captura);
					analise.setPainelGestor(new ControleSiglasDAO().buscaGestorPorSigla(tempObj.getSigla()));
					analise.setSigla(tempObj.getSigla());
					analise.setNomeProjeto(tempObj.getNome_Projeto());
					System.out.println(analise);
					analiseDAO.salvar(analise);
					new Analise_Homologacao_Bean().calcNotaInfosAnt();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	};

	/**
	 * 
	 * Runnable responsavel por capturar os paineis DevOps para a teabela de
	 * Desenvolvimento
	 * 
	 */
	private static Runnable capturaDesenvolvimento = new Runnable() {
		public void run() {
			try {
				System.out.println("\n-----------------------\nID identificado: " + Thread.currentThread().getName());
				RelacaoProjetoSiglaGestorDAO daoTemp = new RelacaoProjetoSiglaGestorDAO();
				RelacaoProjetoSiglaGestor tempObj = daoTemp.buscar(Long.valueOf(Thread.currentThread().getName()));
				List<Config> listaConfigSonar = new ConfigDAO().listar();
				ColetaInformacoesSonarBean sonar = null;
				String urlSonarCommunity = "https://sonarqube-ce.paas.santanderbr.corp/",
						urlSonarEnterprise = "http://sonar.santanderbr.corp/", loginSonar = "x212253";
				if (tempObj.getSonar().equals("Community")) {
					sonar = new ColetaInformacoesSonarBean(listaConfigSonar.stream()
							.filter(config -> config.getUrl().equals(urlSonarCommunity) && config.getLogin().equals(loginSonar)).findFirst()
							.get());
				} else {
					sonar = new ColetaInformacoesSonarBean(listaConfigSonar.stream()
							.filter(config -> config.getUrl().equals(urlSonarEnterprise) && config.getLogin().equals(loginSonar))
							.findFirst().get());
				}
				AnaliseDevDAO analiseDAO = new AnaliseDevDAO();
				String chave = tempObj.getChave();
				String nomeProjeto = tempObj.getNome_Projeto();
				Captura captura = sonar.getSonarApi(chave);
				Repositorio repositorio = Repositorio.DEVOPS;
				Ambiente ambiente = Ambiente.DESENVOLVIMENTO;
				String sigla = tempObj.getSigla();
				if (captura != null) {
					Analise_Dev_Mensal analise = new Analise_Dev_Mensal(captura);
					analise.setSigla(tempObj.getSigla());
					analise.setNomeProjeto(nomeProjeto);
					analise.setAmbiente(ambiente.getAmbiente());
					analise.setRepositorio(repositorio.getRepositorio());
					analise.setPadraoNomeSonar(
							new ProjectName(sigla, nomeProjeto, repositorio, ambiente).getPadraoNomeProjeto());
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
	 * @return - lista de RelacaoProjetoSiglaGestor selecionados
	 */
	public List<RelacaoProjetoSiglaGestor> paineisSelecionados() {
		return new RelacaoProjetoSiglaGestorDAO().listar().stream()
				.filter(r -> r.getSelecionado() != null && r.getSelecionado().equals("ui-icon-check"))
				.collect(Collectors.toList());
	}

	/**
	 * @return - lista de RelacaoProjetoSiglaGestor selecionados
	 */
	public List<RelacaoProjetoSiglaGestor> paineisFiltradosSelecionados() {
		return listaSnapshotFiltrada == null ? Collections.emptyList()
				: listaSnapshotFiltrada.stream()
						.filter(r -> r.getSelecionado() != null && r.getSelecionado().equals("ui-icon-check"))
						.collect(Collectors.toList());
	}

	/**
	 * Seleciona se um modulo será capturado na tabela da Inspeção Diária ou da
	 * Inspeção Mensal
	 * 
	 */
	public void editaTipoCaptura() {
		tipoCaptura = !tipoCaptura;
		String tipo = tipoCaptura ? "homologação" : "desenvolvimento";
		System.out.println(tipo);
	}

	/**
	 * @return - lista de RelacaoProjetoSiglaGestor não selecionados
	 */
	public List<RelacaoProjetoSiglaGestor> paineisNaoSelecionados() {
		return new RelacaoProjetoSiglaGestorDAO().listar().stream()
				.filter(r -> r.getSelecionado() != null && r.getSelecionado().equals("ui-icon-blank"))
				.collect(Collectors.toList());
	}

	public void selecionarTodosModulosFiltrados() {
		for (RelacaoProjetoSiglaGestor relacao : listaSnapshotFiltrada) {
			relacao.setSelecionado("ui-icon-check");
			new RelacaoProjetoSiglaGestorDAO().editar(relacao);
		}
		listarInfosSnapshot();
	}

	/**
	 * Seleciona todos os modulos
	 */
	public void selecionarTodosModulos() {
		for (RelacaoProjetoSiglaGestor relacao : paineisNaoSelecionados()) {
			relacao.setSelecionado("ui-icon-check");
			new RelacaoProjetoSiglaGestorDAO().editar(relacao);
		}
		listarInfosSnapshot();
	}

	/**
	 * Seleciona um objeto RelacaoProjetoSiglaGestor, deixando o objeto marcado para
	 * captura em grupo de modulos
	 */
	// -------------------------------------------------------------------------------------
	public void selecionarModulo(ActionEvent evento) {
		try {
			obj = (RelacaoProjetoSiglaGestor) evento.getComponent().getAttributes().get("meuSelect");
			System.out.println("\n\n\n\n------------ID:--------\n" + obj.getChave());
			obj.setSelecionado(obj.getSelecionado().equals("ui-icon-check") ? "ui-icon-blank" : "ui-icon-check");
			new RelacaoProjetoSiglaGestorDAO().editar(obj);
		} catch (Exception e) {
			Messages.addGlobalError("Erro ao Selecionar: " + obj);
		}
	}

	/**
	 * Limpa a seleção dos modulos
	 */
	public void limparSelecaoTodosModulos() {
		if (paineisFiltradosSelecionados().isEmpty()) {
			for (RelacaoProjetoSiglaGestor relacao : paineisSelecionados()) {
				relacao.setSelecionado("ui-icon-blank");
				new RelacaoProjetoSiglaGestorDAO().editar(relacao);
			}
		} else {
			for (RelacaoProjetoSiglaGestor relacao : paineisFiltradosSelecionados()) {
				relacao.setSelecionado("ui-icon-blank");
				new RelacaoProjetoSiglaGestorDAO().editar(relacao);
			}
		}
		listarInfosSnapshot();
	}

	/**
	 * Executa a captura de todos os modulos RelacaoProjetoSiglaGestor selecionados
	 * 
	 * @param evento - contem a lista de RelacaoProjetoSiglaGestor para serem
	 *               capturadas
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
				List<RelacaoProjetoSiglaGestor> listaModulosSelecionados = new RelacaoProjetoSiglaGestorDAO()
						.listaSnapshot().stream().filter(r -> r.getSelecionado().equals("ui-icon-check"))
						.collect(Collectors.toList());

				System.out.println("\n\n\n\n------------" + listaModulosSelecionados.size()
						+ " SELECIONADOS -----------\n\n\n\n\n\n");
				for (RelacaoProjetoSiglaGestor relacao : listaModulosSelecionados) {
					if (tipoCaptura) {
						Executors.newFixedThreadPool(5, new YourThreadFactory(relacao.getId()))
								.submit(capturaHomologacao);
					} else {
						Executors.newFixedThreadPool(1, new YourThreadFactory(relacao.getId()))
								.submit(capturaDesenvolvimento);

					}
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
		obj = new RelacaoProjetoSiglaGestor();
		dao = new RelacaoProjetoSiglaGestorDAO();

		painelGestor = "";
		sigla = "";
		nome_Projeto = "";
		devOps = "";
		chave = "";
	}

	/**
	 * Salvar um objeto RelacaoProjetoSiglaGestor
	 */
	// -------------------------------------------------------------------------------------
	public void salvar() {
		try {
			obj.setChave(chave);
			obj.setNome_Projeto(nome_Projeto);
			obj.setPainelGestor(painelGestor);
			obj.setSigla(sigla);
			obj.setDevOps(devOps);
			obj.setSonar(sonar);
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
	 * Criar uma lista com os objetos RelacaoProjetoSiglaGestor que são DEVOPS
	 */
	// ------------------------------------------------------------------------------------------------------------------------------------------------------
	public void listarInfosSnapshot() {
		try {
			listaSnapshot = dao.listaSnapshot();
			List<RelacaoProjetoSiglaGestor> listaSelecionadosNulos = listaSnapshot.stream()
					.filter(r -> r.getSelecionado() == null).collect(Collectors.toList());
			for (RelacaoProjetoSiglaGestor relacao : listaSelecionadosNulos) {
				relacao.setSelecionado("ui-icon-blank");
				dao.editar(relacao);
			}
			listaSnapshot = dao.listaSnapshot();
			verificarDuplicidade(listaSnapshot);
			totalSnapshot = listaSnapshot.size();
			Messages.addGlobalInfo("Lista Atualizada!");
			System.out.println("Total: " + total);

		} catch (Exception e) {
			Messages.addGlobalError("Erro listarInfos  ");
			System.out.println("\nErro: " + e.getCause());
		}
	}

	/**
	 * 
	 * @param lista - Lista com todos os paineis DevOps para analise de duplicidade.
	 * 
	 */
	private void verificarDuplicidade(List<RelacaoProjetoSiglaGestor> lista) {
		dao = new RelacaoProjetoSiglaGestorDAO();
		int contador = 1;
		Set<RelacaoProjetoSiglaGestor> paineisDuplicados = new HashSet<RelacaoProjetoSiglaGestor>();
		for (RelacaoProjetoSiglaGestor painelDevOps : lista) {

			List<RelacaoProjetoSiglaGestor> listaModulosDuplicados = lista.stream()
					.filter(elemento -> elemento.getChave().equals(painelDevOps.getChave())
							|| elemento.getNome_Projeto().equals(painelDevOps.getNome_Projeto()))
					.collect(Collectors.toList());

			System.out.println("\n\n\nAnalisando módulo " + contador + " de " + lista.size() + "\n\n\n");
			contador++;

			if (listaModulosDuplicados.size() > 1) {
				listaModulosDuplicados.forEach(elemento -> {
					elemento.setRevisar(true);
					paineisDuplicados.add(elemento);
				});
			}
		}

		for (RelacaoProjetoSiglaGestor painelDevOps : paineisDuplicados) {
			dao.editar(painelDevOps);
		}

		List<RelacaoProjetoSiglaGestor> paineisDevOpsComRevisaoNulla = lista.stream()
				.filter(elemento -> elemento.getRevisar() == null).collect(Collectors.toList());

		for (RelacaoProjetoSiglaGestor painelDevOps : paineisDevOpsComRevisaoNulla) {
			painelDevOps.setRevisar(false);
			dao.editar(painelDevOps);
		}

	}

	// ------------------------------------------------------------------------------------------------------------------------------------------------------
	/**
	 * Criar uma lista com os objetos RelacaoProjetoSiglaGestor
	 */
	// ------------------------------------------------------------------------------------------------------------------------------------------------------
	public void listarInfos() {
		try {
			listaObj = dao.listar();
			total = listaObj.size();
			Messages.addGlobalInfo("Lista Atualizada!");
			System.out.println("Total: " + total);

		} catch (Exception e) {
			Messages.addGlobalError("Erro listarInfos  ");
			System.out.println("\nErro: " + e.getCause());
		}
	}

	/**
	 * Exclui um objeto RelacaoProjetoSiglaGestor
	 */
	// -------------------------------------------------------------------------------------
	public void excluir() {

		try {
			System.out.println("-------------Chamado");
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
			obj = (RelacaoProjetoSiglaGestor) evento.getComponent().getAttributes().get("meuSelect");
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

	public RelacaoProjetoSiglaGestor getObj() {
		return obj;
	}

	public void setObj(RelacaoProjetoSiglaGestor obj) {
		this.obj = obj;
	}

	public List<RelacaoProjetoSiglaGestor> getListaObj() {
		return listaObj;
	}

	public void setListaObj(List<RelacaoProjetoSiglaGestor> listaObj) {
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

	public List<RelacaoProjetoSiglaGestor> getListaSnapshot() {
		return listaSnapshot;
	}

	public int getTotalSnapshot() {
		return totalSnapshot;
	}

	public boolean isTipoCaptura() {
		return tipoCaptura;
	}

	public void setTipoCaptura(boolean tipoCaptura) {
		this.tipoCaptura = tipoCaptura;
	}

	public String getSonar() {
		return sonar;
	}

	public void setSonar(String sonar) {
		this.sonar = sonar;
	}

	public void salvarPlanilha(String caminho) {
		RelacaoProjetoSiglaGestor modulo = new RelacaoProjetoSiglaGestor();
		String chave, nomePainel, nomeGestor, sigla, sonar;
		dao = new RelacaoProjetoSiglaGestorDAO();

		// Carrega a panilha
		Workbook planilha = null;

		try {
			planilha = Workbook.getWorkbook(new File(caminho));

			Sheet abaComItensParaInclusao = planilha.getSheet(0);

			int qtdLinhasComDadosParaInclusao = abaComItensParaInclusao.getRows();

			for (int i = 1; i < qtdLinhasComDadosParaInclusao; i++) {
				Cell celulaGestor = abaComItensParaInclusao.getCell(0, i);
				Cell celulaSigla = abaComItensParaInclusao.getCell(1, i);
				Cell celulaNomePainel = abaComItensParaInclusao.getCell(2, i);
				Cell celulaChavePainel = abaComItensParaInclusao.getCell(3, i);
				Cell celulaSonar = abaComItensParaInclusao.getCell(4, i);

				chave = celulaChavePainel.getContents().toString().trim();
				nomeGestor = celulaGestor.getContents().toString();
				nomePainel = celulaNomePainel.getContents().toString().trim();
				sigla = celulaSigla.getContents().toUpperCase().toString().trim();
				sonar = celulaSonar.getContents().toUpperCase().toString().trim();

				if (nomeGestor.isEmpty()) {
					break;
				} else {
					modulo.setChave(chave);
					modulo.setDevOps("SIM");
					modulo.setNome_Projeto(nomePainel);
					modulo.setPainelGestor(nomeGestor);
					modulo.setSigla(sigla);
					modulo.setSelecionado("ui-icon-blank");
					modulo.setSonar(sonar);

					dao.salvar(modulo);
				}
			}

			Sheet abaComItensParaExclusao = planilha.getSheet(1);

			int qtdLinhasComDadosParaExclusao = abaComItensParaExclusao.getRows();

			for (int i = 1; i < qtdLinhasComDadosParaExclusao; i++) {
				Cell celulaNomePainel = abaComItensParaExclusao.getCell(0, i);

				nomePainel = celulaNomePainel.getContents().toString().trim();

				if (nomePainel.isEmpty()) {
					break;
				} else {
					RelacaoProjetoSiglaGestor relacaoProjetoSiglaGestor = dao.buscar(nomePainel);
					if (relacaoProjetoSiglaGestor != null) {
						dao.excluir(relacaoProjetoSiglaGestor);
					}
				}
			}

			Messages.addGlobalInfo("Execução realizada com sucesso");
		} catch (Exception e) {
			Messages.addGlobalError("Não foi possivel salvar a planilha");
			e.printStackTrace();
		}
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public StreamedContent getExemploExcelDevOps() {
		return new MetodosUteis().getExcelComoStreamedContent(
				"/resources/exemplos_excel/exemplo_devOps_inclusao_exclusao.xls",
				"exemplo_devOps_inclusao_exclusao.xls");
	}

	public List<RelacaoProjetoSiglaGestor> getListaSnapshotFiltrada() {
		return listaSnapshotFiltrada;
	}

	public void setListaSnapshotFiltrada(List<RelacaoProjetoSiglaGestor> listaSnapshotFiltrada) {
		this.listaSnapshotFiltrada = listaSnapshotFiltrada;
	}
}
