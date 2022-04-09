package br.com.icresult.bean;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;

import org.omnifaces.util.Messages;
import org.primefaces.model.StreamedContent;

import br.com.icresult.dao.complementos.AnaliseDevDAO;
import br.com.icresult.dao.complementos.ControleGitDevDAO;
import br.com.icresult.dao.complementos.RelacaoProjetoSiglaGestorDAO;
import br.com.icresult.dao.complementos.SiglasGitDAO;
import br.com.icresult.domain.complementos.Analise_Dev_Mensal;
import br.com.icresult.domain.complementos.ControleGitDev;
import br.com.icresult.domain.complementos.RelacaoProjetoSiglaGestor;
import br.com.icresult.domain.complementos.SiglasGit;
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

@SuppressWarnings("serial")
@ManagedBean
@SessionScoped
public class SiglasGitBean implements Serializable {

	/**
	 * 
	 */
	private static String CAMINHO;
	protected static SiglasGit sigla;
	private SiglasGit controle;
	private List<SiglasGit> listaSiglas;
	private SiglasGit obj;
	private SiglasGitDAO dao;
	private int total;
	private static Logger LOG = Logger.getLogger(SiglasGitBean.class);

	public void executarTodasSiglasAtualizadasAutomaticoMensal() {

		try {
			new Thread(runnableExecutaTodasSiglasAtualizadasMensal).start();
		} catch (Exception e) {
			LOG.error("Erro ao executar o Sonar para as siglas atualizadas", e);
		}

	}

	private Runnable runnableExecutaTodasSiglasAtualizadasMensal = new Runnable() {
		public void run() {
			try {
				List<SiglasGit> listaTodasSiglas = new SiglasGitDAO().listar();
				List<SiglasGit> listaSiglasQueExecutaraoSonar = new ArrayList<SiglasGit>();
				List<SiglasGit> listaSiglasQueSoSeraoCapturadas = new ArrayList<SiglasGit>();
				for (SiglasGit sigla : listaTodasSiglas) {
					Analise_Dev_Mensal ultimaInspecaoSiglaGit = new SiglasGitDAO()
							.buscarUltimaInspecaoPorNomeSistema(sigla);
					if (ultimaInspecaoSiglaGit != null) {
						String commitUltimaInspecao = ultimaInspecaoSiglaGit.getDataCommit();
						if (commitUltimaInspecao != null && !(commitUltimaInspecao.equals("N/A"))) {
							System.out.println(ultimaInspecaoSiglaGit.getDataCommit());
							Integer dia = Integer.parseInt(commitUltimaInspecao.trim().substring(8));
							Integer mes = Integer.parseInt(commitUltimaInspecao.trim().substring(5, 7));
							Integer ano = Integer.parseInt(commitUltimaInspecao.trim().substring(0, 4));
							LocalDate dataCommitUltimaInspecao = LocalDate.of(ano, mes, dia);
							if (sigla.getDataCommit() != null) {
								LocalDate dataCommitSiglaGit = sigla.getDataCommit().toInstant()
										.atZone(ZoneId.systemDefault()).toLocalDate();
								if (dataCommitSiglaGit.isAfter(dataCommitUltimaInspecao)) {
									listaSiglasQueExecutaraoSonar.add(sigla);
								}
							}
						} else {
							listaSiglasQueSoSeraoCapturadas.add(sigla);
							LOG.error("Erro ao executar " + sigla.getSigla()
									+ ", pois sua ultima execução do Sonar tem uma data nula");
						}

					}
				}

				if (listaSiglasQueExecutaraoSonar.isEmpty()) {
					LOG.info("Nenhuma Sigla do Git Atualizada");
				} else {
					for (SiglasGit sigla : listaSiglasQueExecutaraoSonar) {
						Executors
								.newFixedThreadPool(5,
										new YourThreadFactory(SiglasGit.class.getName() + "-" + sigla.getId()))
								.submit(ExecutarSonarPorModulosBean.sonarScannerAutomaticoMensal);
					}
				}

				for (SiglasGit sigla : listaSiglasQueSoSeraoCapturadas) {
					System.out.println("Capturando " + sigla.getNome_Projeto());
					Executors.newFixedThreadPool(5, new YourThreadFactory(sigla.getId())).submit(SiglasGitBean.captura);
				}
			} catch (Exception e) {
				LOG.error("Erro ao executar Modulos do Git");
				e.printStackTrace();
			}
		}
	};

	/**
	 * 
	 * Verifica atualizações nas siglas do GIT
	 * 
	 */
	public void verificarAtualizacoes() {
		SiglasGitDAO siglaDAO = new SiglasGitDAO();
		for (SiglasGit sigla : siglaDAO.listar()) {
			System.out.println("Buscando atualização: " + sigla.getSigla());
			siglaDAO.verificaAtualizacoes(sigla);
		}

	}

	public void validaSiglas() {
		new Thread(validaModulos).start();
	}

	/**
	 * Runnable que cria a estrutura de execução do Sonar
	 */
	private static Runnable validaModulos = new Runnable() {

		@Override
		public void run() {
			SiglasGitDAO siglasDAO = new SiglasGitDAO();
			for (SiglasGit controle : siglasDAO.listar()) {
				SiglasGitBean.sigla = controle;
				criarPastas();
			}
		}
	};

	/**
	 * 
	 * Metodo para capturar todas as siglas atualizadas
	 * 
	 */
	public void executarCapturaTodasSiglasAtualizadas() {
		try {
			new Thread(runnableExecutarCapturaTodasSiglasAtualizadas).start();
		} catch (Exception e) {
			System.out.println("Erro ao captura todas as sigla do git atualizadas");
		}
	}

	Runnable runnableExecutarCapturaTodasSiglasAtualizadas = new Runnable() {

		@Override
		public void run() {
			SiglasGitDAO controleSiglasGITDAO = new SiglasGitDAO();
			List<SiglasGit> listaSiglaAtualizadas = controleSiglasGITDAO.listar().stream().filter(c -> c.isAlteracao())
					.collect(Collectors.toList());
			for (SiglasGit sigla : listaSiglaAtualizadas) {
				System.out.println(sigla.getChave());
				Executors.newFixedThreadPool(10, new YourThreadFactory(sigla.getId().toString())).submit(captura);
			}
		}
	};

	/**
	 * Seleciona um objeto SiglasGitBean, deixando o objeto marcado para execução do
	 * sonar ou captura em grupo de modulos
	 */
	public void selecionarModulo(ActionEvent evento) {

		try {
			obj = (SiglasGit) evento.getComponent().getAttributes().get("meuSelect");
			System.out.println("\n\n\n\n------------ID:--------\n" + obj.getChave());
			obj.setSelecionado(obj.getSelecionado().equals("ui-icon-check") ? "ui-icon-blank" : "ui-icon-check");

		} catch (Exception e) {
			Messages.addGlobalError("Erro ao Selecionar: ");
		}
	}

	/**
	 * 
	 * Seleciona o objeto que executa o scan ou captura
	 * 
	 */
	public void selecionarObj(ActionEvent event) {
		try {
			obj = (SiglasGit) event.getComponent().getAttributes().get("meuSelect");
			System.out.println("PACOTE :" + obj);
		} catch (Exception e) {
			System.out.println("Erro ao selecionar objeto");
		}
	}

	/**
	 * @return - retorna uma lista de SiglasGit selecionados na aplicação
	 */
	public List<SiglasGit> paineisSelecionados() {
		return new SiglasGitDAO().listar().stream().filter(r -> r.getSelecionado().equals("ui-icon-check"))
				.collect(Collectors.toList());
	}

	/**
	 * @return - retorna uma lista de SiglasGit selecionados na aplicação
	 */
	public List<SiglasGit> paineisNaoSelecionados() {
		return new SiglasGitDAO().listar().stream().filter(r -> r.getSelecionado().equals("ui-icon-blank"))
				.collect(Collectors.toList());
	}

	/**
	 * Seleciona todos os modulos
	 */
	public void selecionarTodosModulos() {
		SiglasGitDAO controleDAO = new SiglasGitDAO();
		for (SiglasGit controle : paineisNaoSelecionados()) {
			controle.setSelecionado("ui-icon-check");
			controleDAO.editar(controle);
		}
		listarInfos();
	}

	/**
	 * Limpa a seleção dos modulos
	 */
	public void limparSelecaoTodosModulos() {
		SiglasGitDAO controleDAO = new SiglasGitDAO();
		for (SiglasGit controle : paineisSelecionados()) {
			controle.setSelecionado("ui-icon-blank");
			controleDAO.editar(controle);
		}
		listarInfos();
	}

	/**
	 * Captura as informações de uma planilha xls e salva no banco de dados
	 * 
	 * @param caminho - caminho do arquivo que contem as siglas do git
	 */
	public void salvarPlanilha(String caminho) {
		controle = new SiglasGit();
		String sigla;
		String chave = new String();

		// Carrega a planilha
		Workbook workbook = null;
		try {
			workbook = Workbook.getWorkbook(new File(caminho));

			// Seleciona a aba do excel
			Sheet sheet = workbook.getSheet(0);

			// Numero de linhas com dados do xls
			int linhas = sheet.getRows();
			limparDB();
			for (int i = 1; i < linhas; i++) {
				Cell celula1 = sheet.getCell(0, i); // coluna 1 -Sigla
				Cell celula2 = sheet.getCell(1, i); // coluna 2 -Chave

				sigla = celula1.getContents().toString().trim().toUpperCase();
				chave = celula2.getContents().toString().trim();

				// Encerra a leitura quando encontra linha vazia
				if (sigla.isEmpty()) {
					break;
				}

				if (!sigla.isEmpty()) {
					controle.setChave(chave);
					String auxSigla = sigla;
					String auxChave = chave;
					List<ControleGitDev> listaControle = new ControleGitDevDAO().listar().stream()
							.filter(g -> g.getSigla().equals(auxSigla)).collect(Collectors.toList());
					if (!listaControle.isEmpty()) {
						RelacaoProjetoSiglaGestor relacao = new RelacaoProjetoSiglaGestorDAO().listar().stream()
								.filter(r -> r.getChave().equals(auxChave)).collect(Collectors.toList()).get(0);
						String nomePainel = relacao.getNome_Projeto();
						controle.setNome_Projeto(nomePainel);
						controle.setSelecionado("ui-icon-blank");
						controle.setSigla(sigla);
						String caminhoSigla = listaControle.stream()
								.filter(e -> e.getNomeSistema().equals(controle.getNome_Projeto()))
								.collect(Collectors.toList()).get(0).getCaminho();
						int ultimaBarra = caminhoSigla.lastIndexOf("\\");
						controle.setCaminho(caminhoSigla.substring(0, ultimaBarra));
						controle.setNomeProjetoPadronizado(
								new ProjectName(sigla, nomePainel, Repositorio.GIT, Ambiente.DESENVOLVIMENTO));
						System.out.println(controle);
						salvar();
					}

				}
			}
			Messages.addGlobalInfo("Planilha salva com sucesso!");
		} catch (Exception e) {
			System.out.println(chave);
			e.printStackTrace();
			Messages.addGlobalError("Não foi possível salvar");
		}
	}

	/***
	 * Executa sonar individualmente
	 */
	public void executarSonar() {
		Executors.newFixedThreadPool(1, new YourThreadFactory(SiglasGit.class.getName() + "-" + obj.getId()))
				.submit(ExecutarSonarPorModulosBean.sonarScannerAutomaticoMensal);
	}

	/***
	 * Executa captura individualmente
	 */
	public void executarCaptura() {
		Executors.newFixedThreadPool(1, new YourThreadFactory(obj.getId())).submit(captura);
	}

	/**
	 * Lista os objetos do tipo SiglasGit
	 */
	public void listarInfos() {
		try {
			dao = new SiglasGitDAO();
			dao.preencherCampoSelecionado();
			listaSiglas = dao.listar();
			listaSiglas.sort(SiglasGit.getComparadorPorDataCommit());
			total = listaSiglas.size();
			Messages.addGlobalInfo("Lista de Siglas Atualizada!");
			LOG.info("Lista Atualizada!");
		} catch (Exception e) {
			LOG.error("Erro ao Atualizar Lista.");
		}
	}

	/**
	 * salva os objetos SiglasGit vindos da planilha
	 * 
	 */
	private void salvar() {
		SiglasGitDAO dao = new SiglasGitDAO();
		dao.salvar(controle);

	}

	/**
	 * Limpa o banco de dados em caso de atualizações
	 * 
	 */
	private void limparDB() {
		SiglasGitDAO dao = new SiglasGitDAO();
		for (SiglasGit s : dao.listar()) {
			dao.excluir(s);
		}
	}

	/***
	 * Executa o Sonar para todas as siglas selecionadas
	 * 
	 * @param evento - parametro que contém a lista das siglas do Git
	 * 
	 */
	public void executarSonarTodasSiglasSelecionadas(ActionEvent evento) {
		try {
			@SuppressWarnings("unchecked")
			List<SiglasGit> listaModulosSelecionados = ((List<SiglasGit>) evento.getComponent().getAttributes()
					.get("tabela")).stream().filter(r -> r.getSelecionado().equals("ui-icon-check"))
							.collect(Collectors.toList());
			for (SiglasGit controle : listaModulosSelecionados) {
				System.out.println(controle.getChave());
				new SiglasGitDAO().editar(controle);
			}
			new Thread(runnableExecutarSonarTodasSiglasSelecionadas).start();
		} catch (Exception e) {
			e.getStackTrace();
		}
	}

	Runnable runnableExecutarSonarTodasSiglasSelecionadas = new Runnable() {

		@Override
		public void run() {
			try {
				List<SiglasGit> listaModulosSelecionados = new SiglasGitDAO().listar().stream()
						.filter(r -> r.getSelecionado().equals("ui-icon-check")).collect(Collectors.toList());
				for (SiglasGit controle : listaModulosSelecionados) {
					System.out.println(controle.getChave());
					Executors
							.newFixedThreadPool(3,
									new YourThreadFactory(SiglasGit.class.getName() + "-" + controle.getId()))
							.submit(ExecutarSonarPorModulosBean.sonarScannerAutomaticoMensal);
				}
			} catch (Exception e) {
				e.getStackTrace();
			}
		}
	};

	/***
	 * Executa a captura para todas as siglas selecionadas
	 * 
	 * @param evento - parametro que contém a lista das siglas do Git
	 * 
	 */
	public void executarCapturaTodasSiglasSelecionadas(ActionEvent evento) {
		try {
			@SuppressWarnings("unchecked")
			List<SiglasGit> listaModulosSelecionados = ((List<SiglasGit>) evento.getComponent().getAttributes()
					.get("tabela")).stream().filter(r -> r.getSelecionado().equals("ui-icon-check"))
							.collect(Collectors.toList());
			for (SiglasGit controle : listaModulosSelecionados) {
				System.out.println(controle.getChave());
				new SiglasGitDAO().editar(controle);
			}
			new Thread(runnableExecutaCapturaSiglasSelecionadas).start();
		} catch (Exception e) {
			e.getStackTrace();
		}
	}

	Runnable runnableExecutaCapturaSiglasSelecionadas = new Runnable() {

		@Override
		public void run() {
			try {
				List<SiglasGit> listaModulosSelecionados = new SiglasGitDAO().listar().stream()
						.filter(r -> r.getSelecionado().equals("ui-icon-check")).collect(Collectors.toList());
				for (SiglasGit controle : listaModulosSelecionados) {
					System.out.println(controle.getChave());
					Executors.newFixedThreadPool(10, new YourThreadFactory(controle.getId())).submit(captura);
				}
			} catch (Exception e) {
				e.getStackTrace();
			}
		}
	};

	/**
	 * 
	 * Runnable responsavel por capturar os paineis
	 * 
	 */
	private static Runnable captura = new Runnable() {

		public void run() {
			try {
				LOG.info("\n-----------------------\nID identificado: " + Thread.currentThread().getName());
				SiglasGitDAO daoTemp = new SiglasGitDAO();
				SiglasGit tempObj = daoTemp.buscar(Long.valueOf(Thread.currentThread().getName()));
				ColetaInformacoesSonarBean sonar = new ColetaInformacoesSonarBean();
				AnaliseDevDAO analiseDAO = new AnaliseDevDAO();
				String chave = tempObj.getChave();
				String nomeProjetoPadronizado = tempObj.getNomeProjetoPadronizado();
				Repositorio repositorio = Repositorio.GIT;
				Ambiente ambiente = Ambiente.DESENVOLVIMENTO;
				Captura captura = sonar.getSonarApi(chave);
				if (captura != null) {
					String nomeProjeto = tempObj.getNome_Projeto();
					Analise_Dev_Mensal analise = new Analise_Dev_Mensal(captura);
					analise.setSigla(tempObj.getSigla());
					analise.setNomeProjeto(nomeProjeto);
					analise.setAmbiente(ambiente.getAmbiente());
					analise.setRepositorio(repositorio.getRepositorio());
					analise.setPadraoNomeSonar(tempObj.getNomeProjetoPadronizado());
					Date dataCommit = tempObj.getDataCommit();
					analise.setDataCommit(dataCommit == null ? null
							: dataCommit.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString());

					tempObj.setDataSonar(analise.getDataSonar());
					tempObj.setVersao(analise.getVersao());

					new SiglasGitDAO().editar(tempObj);
					analiseDAO.salvar(analise);
				} else {
					LOG.info("Captura nula, pesquisando a ultima inspeção para esta chave");
					analiseDAO.buscaUltimaInspecaoComNomeProjeto(tempObj.getNomeProjetoPadronizado(), nomeProjetoPadronizado,
							repositorio.getRepositorio(), ambiente.getAmbiente());
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	/**
	 * Criar as pastas target, o arquivo de propriedades do sonar
	 */
	private static void criarPastas() {

		String caminhoModulo = sigla.getCaminho().toString();
		ExecutarSonarPorModulosBean.escreveInformacoesPropertiesSonar(caminhoModulo + "\\" + "sonar-project.properties",
				sigla.getChave(), sigla.getNomeProjetoPadronizado(), "");

		// Copiando a pasta target para dentro das pastas dos modulos
		Path targetDentroDoPrograma = new MetodosUteis().caminhoTarget();
		Path caminhoDosModulos = Paths.get(caminhoModulo + "\\target");
		try {
			if (!Files.exists(caminhoDosModulos)) {
				Files.createDirectory(caminhoDosModulos);
				copiarArquivos(targetDentroDoPrograma, caminhoDosModulos);
			}
		} catch (Exception e) {
			LOG.error("Erro ao copiar pasta target para " + caminhoModulo);
		}

	}

	/***
	 * 
	 * @param origem  - Path do arquivo ou pasta de origem
	 * @param destino - Path do arquivo ou pasta de destino
	 * @throws IOException - Exceção lançada se ocorrer um erro ao copiar os
	 *                     arquivos
	 * 
	 */

	public static void copiarArquivos(Path origem, Path destino) throws IOException {
		// se é um diretório, tentamos criar. se já existir, não tem problema.
		if (Files.isDirectory(origem)) {
			Files.createDirectories(destino);

			// listamos todas as entradas do diretório
			try (DirectoryStream<Path> entradas = Files.newDirectoryStream(origem);) {

				for (Path entrada : entradas) {
					// para cada entrada, achamos o arquivo equivalente dentro de cada arvore
					Path novaOrigem = origem.resolve(entrada.getFileName());
					Path novoDestino = destino.resolve(entrada.getFileName());

					// invoca o metodo de maneira recursiva
					copiarArquivos(novaOrigem, novoDestino);
				}

			} catch (Exception e) {
				System.out.println("Erro ao copiar pasta target para " + destino);
			}

		} else {
			// copiamos o arquivo
			Files.copy(origem, destino);
		}
	}

	public static String getCAMINHO() {
		return CAMINHO;
	}

	public static void setCAMINHO(String cAMINHO) {
		CAMINHO = cAMINHO;
	}

	// -------------Getters e Setters
	public List<SiglasGit> getListaSiglas() {
		return listaSiglas;
	}

	public SiglasGit getObj() {
		return obj;
	}

	public int getTotal() {
		return total;
	}

	public static Runnable getCaptura() {
		return captura;
	}

	public Runnable getCapturaMensal() {
		return captura;
	}

	public StreamedContent getExemploSiglasGit() {
		return new MetodosUteis().getExcelComoStreamedContent("/resources/exemplos_excel/exemplo_siglasGit.xls",
				"exemplo_siglasGit.xls");
	}

}
