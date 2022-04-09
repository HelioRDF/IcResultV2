package br.com.icresult.bean;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Serializable;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;

import org.omnifaces.util.Messages;
import org.primefaces.PrimeFaces;
import org.primefaces.model.StreamedContent;

import br.com.icresult.dao.complementos.Analise_DiarioDAO;
import br.com.icresult.dao.complementos.ConfigGitDAO;
import br.com.icresult.dao.complementos.ControleGitDevDAO;
import br.com.icresult.domain.complementos.Analise_Dev_Diario;
import br.com.icresult.domain.complementos.ConfigGit;
import br.com.icresult.domain.complementos.ControleGitDev;
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
 * -Classe BEAN ControleGitDevBean.
 * 
 * @author helio.franca
 * @version v2.1.3
 * @since 08-08-2018
 *
 */

@SuppressWarnings("serial")
@ManagedBean
@SessionScoped
public class ControleGitDevBean implements Serializable {
	static final Runtime run = Runtime.getRuntime();
	static Process pro;
	static BufferedReader read;
	private static ControleGitDev controle;
	private ControleGitDevDAO dao;
	private List<ControleGitDev> listaControle;
	private String pathSigla;
	private String path;
	private int total;
	static String CAMINHO = "";
	private static ControleGitDev obj;
	private boolean selecao;
	private static final String ICONE_CHECK = "ui-icon-check";
	private static final String ICONE_BLANK = "ui-icon-blank";
	private static Logger logDaClasse = Logger.getLogger(ControleGitDevBean.class);

	/**
	 * @return - retorna uma lista de ControleGitDev que foram selecionadas na
	 *         aplicação
	 */
	public List<ControleGitDev> paineisSelecionados() {
		return new ControleGitDevDAO().listar().stream().filter(r -> r.getSelecionado().equals(ICONE_CHECK))
				.collect(Collectors.toList());
	}

	/**
	 * @return - retorna uma lista de ControleGitDev que não foram selecionadas na
	 *         aplicação
	 */
	public List<ControleGitDev> paineisNaoSelecionados() {
		return new ControleGitDevDAO().listar().stream().filter(r -> r.getSelecionado().equals(ICONE_BLANK))
				.collect(Collectors.toList());
	}

	/**
	 * Seleciona todos os elementos de ControleGitDev
	 */
	public void selecionarTodosModulos() {
		ControleGitDevDAO controleDAO = new ControleGitDevDAO();
		for (ControleGitDev painelNaoSelecionado : paineisNaoSelecionados()) {
			painelNaoSelecionado.setSelecionado(ICONE_CHECK);
			controleDAO.editar(painelNaoSelecionado);
		}
		listarInfos();
	}

	/**
	 * 
	 * Limpa a seleção de todo os elementos de ControleGitDev
	 * 
	 */
	public void limparSelecaoTodosModulos() {
		ControleGitDevDAO controleDAO = new ControleGitDevDAO();
		for (ControleGitDev painelSelecionado : paineisSelecionados()) {
			painelSelecionado.setSelecionado(ICONE_BLANK);
			controleDAO.editar(painelSelecionado);
		}
		listarInfos();
	}

	/**
	 * Executa o Sonar para todos os módulos ControleGitDev selecionados
	 * 
	 * @param evento - variavel que contém a lista de modulos ControleGitDev
	 */
	public void executarSonarPorModulosSelecionados(ActionEvent evento) {

		try {
			new Thread(runnableExecutarSonarModulosSelecionados).start();
		} catch (Exception e) {
			logDaClasse.error("Erro ao executar Sonar", e);
		}

	}

	private static Runnable runnableExecutarSonarModulosSelecionados = new Runnable() {

		@Override
		public void run() {
			try {
				List<ControleGitDev> listaModulosSelecionados = new ControleGitDevDAO().listar().stream()
						.filter(r -> r.getSelecionado().equals(ICONE_CHECK)).collect(Collectors.toList());
				for (ControleGitDev moduloSelecionado : listaModulosSelecionados) {
					Executors
							.newFixedThreadPool(3,
									new YourThreadFactory(
											ControleGitDev.class.getName() + "-" + moduloSelecionado.getId()))
							.submit(ExecutarSonarPorModulosBean.sonarScannerAutomaticoDiaria);
				}
			} catch (Exception e) {
				logDaClasse.error("Erro ao executar Sonar", e);
			}
		}
	};

	/**
	 * Executa o Sonar para todos os modulos ControleGitDev atualizados
	 */
	public void executarSonarModulosAtualizados() {
		new Thread(runnableExecutaSonarModulosAtualizados).start();
	}

	private static Runnable runnableExecutaSonarModulosAtualizados = new Runnable() {

		@Override
		public void run() {
			try {
				ControleGitDevDAO controleGitDAO = new ControleGitDevDAO();
				LocalDate hoje = LocalDate.now();
				ArrayList<ControleGitDev> listaModulosAtualizados = (ArrayList<ControleGitDev>) controleGitDAO.listar()
						.stream().filter(c -> Instant.ofEpochMilli(c.getDataCommit().getTime())
								.atZone(ZoneId.systemDefault()).toLocalDate().equals(hoje) && c.isAlteracao())
						.collect(Collectors.toList());
				if (listaModulosAtualizados.isEmpty()) {
					logDaClasse.info("Nenhum modulo foi atualizado");
				}
				for (ControleGitDev con : listaModulosAtualizados) {
					con.setCapturado(false);
					controleGitDAO.editar(con);
					logDaClasse.info("\nexecutarSonar :" + con.getPacoteChaveNomePainelSonar() + "\n Caminho: "
							+ con.getCaminho());

					Executors
							.newFixedThreadPool(3,
									new YourThreadFactory(ControleGitDev.class.getName() + "-" + con.getId()))
							.submit(ExecutarSonarPorModulosBean.sonarScannerAutomaticoDiaria);
					Thread.sleep(3000);

				}
			} catch (Exception e) {
				e.printStackTrace();
				logDaClasse.error("Erro :" + e + " ao tentar executar o Sonar para modulos atualziados");
			}
		}
	};

	/**
	 * Executa a captura das informações dos paineis Selecionados na Aplicação
	 * 
	 * @param evento - variavel que contém a lista de modulos ControleGitDev
	 */
	public void executarCapturaPorModulosSelecionados(ActionEvent evento) {

		try {
			@SuppressWarnings("unchecked")
			List<ControleGitDev> listaModulosSelecionados = ((List<ControleGitDev>) evento.getComponent()
					.getAttributes().get("tabela")).stream().filter(r -> r.getSelecionado().equals(ICONE_CHECK))
							.collect(Collectors.toList());
			for (ControleGitDev modulosSelecionado : listaModulosSelecionados) {
				logDaClasse.info(modulosSelecionado.getPacoteChaveNomePainelSonar());
				new ControleGitDevDAO().editar(modulosSelecionado);
			}
			new Thread(executaCapturaModulosSelecionados).start();
		} catch (Exception e) {
			logDaClasse.error("Erro ao capturar modulos", e);
		}

	}

	static Runnable executaCapturaModulosSelecionados = new Runnable() {

		@Override
		public void run() {
			try {
				List<ControleGitDev> listaModulosSelecionados = new ControleGitDevDAO().listar().stream()
						.filter(r -> r.getSelecionado().equals(ICONE_CHECK)).collect(Collectors.toList());
				for (ControleGitDev modulosSelecionadosParaCaptura : listaModulosSelecionados) {
					Executors
							.newFixedThreadPool(3,
									new YourThreadFactory(modulosSelecionadosParaCaptura.getCodigo().toString()))
							.submit(captura);
					Thread.sleep(5000);
				}
			} catch (Exception e) {
				logDaClasse.error("Erro ao capturar modulos selecionados", e);
			}
		}
	};

	/**
	 * Executa a captura dos modulos que foram atualizados
	 */
	public void executarCapturaPorModulosAtualizados() {
		try {
			new Thread(runnableExecutarCapturaPorModulosAtualizados).start();
		} catch (Exception e) {
			logDaClasse.error("Erro ao executar captura dos modulos git atualizados", e);
		}
	}

	/**
	 * Runnable para listar modulos atualizados e enviar os modulos para a Thread de
	 * captura
	 */

	private static Runnable runnableExecutarCapturaPorModulosAtualizados = new Runnable() {
		public void run() {
			try {
				ControleGitDevDAO controleGitDAO = new ControleGitDevDAO();
				LocalDate hoje = LocalDate.now();
				ArrayList<ControleGitDev> listaPacotesAtualizados = (ArrayList<ControleGitDev>) controleGitDAO.listar()
						.stream().filter(c -> Instant.ofEpochMilli(c.getDataCommit().getTime())
								.atZone(ZoneId.systemDefault()).toLocalDate().equals(hoje) && c.isAlteracao())
						.collect(Collectors.toList());

				if (listaPacotesAtualizados.isEmpty()) {
					logDaClasse.info("Nenhum modulo foi atualizado");
				} else {
					logDaClasse.info(listaPacotesAtualizados.size() + " pacotes com atualizacao.");
				}
				for (ControleGitDev con : listaPacotesAtualizados) {
					logDaClasse.info(con.getPacoteChaveNomePainelSonar());
					Long codigo = new ControleGitDevDAO().listar().stream()
							.filter(c -> c.getPacoteChaveNomePainelSonar().equals(con.getPacoteChaveNomePainelSonar()))
							.collect(Collectors.toList()).get(0).getCodigo();
					Executors.newFixedThreadPool(3, new YourThreadFactory(codigo.toString())).submit(captura);
					Thread.sleep(5000);

				}
			} catch (Exception e) {
				logDaClasse.error("Erro ao executar a captura dos modoulos com atualização: ", e);
			}
		}
	};

	/**
	 * // Salvar um objeto do tipo ControleGitDev
	 */
	private void salvar() {
		try {
			dao.salvar(controle);
		} catch (Exception e) {
			logDaClasse.error("Não foi possível salvar o objeto", e);
		}
	}

	/**
	 * Lista os objetos do tipo ControleGitDev
	 */
	public void listarInfos() {
		try {
			dao = new ControleGitDevDAO();
			dao.preencherCampoSelecionado();
			validarModulos();
			listaControle = dao.listar();
			listaControle.sort(ControleGitDev.getComparadorPorAlteracao());
			total = listaControle.size();
			Messages.addGlobalInfo("Lista de módulos do git atualizada!");
			logDaClasse.info("Lista Atualizada!");
		} catch (Exception e) {
			logDaClasse.error("Erro ao  Atualizar Lista.", e);
		}
	}

	/**
	 * Captura as informações de uma planilha xls e salva no banco de dados
	 */
	public void salvarPlanilha() {
		setControle(new ControleGitDev());
		dao = new ControleGitDevDAO();
		String sigla, sistema, caminho, pacoteChavePainelSonar, usuarioGit, url, branch;
		Date dateC = Date.from(LocalDateTime.now().atZone(ZoneId.of("Etc/GMT+3")).toInstant());

		// Carrega a planilha
		Workbook workbook = null;
		try {
			workbook = Workbook.getWorkbook(new File(CAMINHO));

			// Seleciona a aba do excel
			Sheet sheet = workbook.getSheet(0);

			// Numero de linhas com dados do xls
			int linhas = sheet.getRows();
			limparDB();
			for (int i = 1; i < linhas; i++) {
				Cell celula1 = sheet.getCell(0, i); // coluna 1 -Sigla
				Cell celula2 = sheet.getCell(1, i); // coluna 2 -Sistema
				Cell celula3 = sheet.getCell(2, i); // coluna 4 - pacote
				Cell celula4 = sheet.getCell(3, i); // coluna 3 - caminho
				Cell celula5 = sheet.getCell(4, i); // coluna 4 - usuarioGit
				Cell celula6 = sheet.getCell(5, i); // coluna 7 - url
				Cell celula7 = sheet.getCell(6, i); // coluna 8 - branch

				sigla = celula1.getContents().toString().trim().toUpperCase();
				sistema = celula2.getContents().toString().trim().toUpperCase();
				pacoteChavePainelSonar = celula3.getContents().toString().trim();
				caminho = celula4.getContents().toString().trim().toUpperCase();
				usuarioGit = celula5.getContents().toString().trim().toUpperCase();
				url = celula6.getContents().toString().trim();
				branch = celula7.getContents().toString().trim();

				// Encerra a leitura quando encontra linha vazia
				if (sigla.isEmpty()) {
					break;
				}

				if (!sigla.isEmpty()) {
					dateC = new Date();
					controle.setSigla(sigla);
					controle.setNomeSistema(sistema);
					controle.setCaminho(caminho);
					controle.setPacoteChaveNomePainelSonar(pacoteChavePainelSonar);
					controle.setNomeArquivo(CAMINHO);
					controle.setDataVerificacao(dateC);
					controle.setDataCommitAnt(null);
					controle.setDataCommit(null);
					controle.setUsuarioGit(usuarioGit);
					controle.setUrl(url);
					controle.setBranch(branch);
					controle.setNomeProjetoPadronizado(
							new ProjectName(sigla, pacoteChavePainelSonar, Repositorio.GIT, Ambiente.DESENVOLVIMENTO));
					salvar();
				}
			}
			validarModulos();
			Messages.addGlobalInfo("Planilha salva com sucesso!");
			gerarLogGit();
		} catch (Exception e) {
			logDaClasse.error("Erro ao salvar planilha", e);
			Messages.addGlobalError("Não foi possível salvar (salvarPlanilha())");
		}
	}

	/**
	 * Limpa a tabela do banco de dados.
	 */
	public void limparDB() {
		try {
			listarInfos();
			for (ControleGitDev controleGit : listaControle) {
				ControleGitDev entidade = dao.buscar(controleGit.getCodigo());
				dao.excluir(entidade);
			}
		} catch (Exception e) {
			logDaClasse.error("Não foi possivel limpar o Banco de Dados", e);
		}
	}

	/**
	 * Valida e formata um objeto do tipo Data.
	 * 
	 * @param dataInfo - Data a ser validada
	 * @param msg      - Mensagem alternativa.
	 * @return - Retorna um objeto do tipo Date.
	 */
	public static Date validadorData(String dataInfo, String msg) {
		DateFormat formatter = new SimpleDateFormat("dd/MM/yy");
		Date dataFinal = null;
		if (dataInfo.isEmpty()) {
			dataFinal = null;
		} else {
			try {
				dataFinal = (Date) formatter.parse(dataInfo);
			} catch (ParseException e) {
				dataFinal = null;
				logDaClasse.error("-Erro em data " + msg, e);
			}
		}
		return dataFinal;
	}

	/**
	 * Chama o Runnable do gitlog
	 */
	public static void gerarLogGit() {
		try {
			new Thread(gitLog).start();
			logDaClasse.info("Git log em execução!");
		} catch (Exception e) {
			logDaClasse.error("Erro ao executar Git Log!", e);
		}
	}

	/**
	 * Runnable para acionar o comando gitlog e capturar as informações.
	 */
	private static Runnable gitLog = new Runnable() {
		public void run() {

			ControleGitDevDAO controleGitDao = new ControleGitDevDAO();
			List<ControleGitDev> listaModulosGit = controleGitDao.listar().stream()
					.filter(modulo -> Files.exists(Paths.get(modulo.getCaminho()))).collect(Collectors.toList());

			String erro;

			for (ControleGitDev controleGitDev : listaModulosGit) {
				erro = "";
				ControleGitDev entidade = controleGitDao.buscar(controleGitDev.getCodigo());
				String pathSigla = "cd " + entidade.getCaminho();
				String comandoGit = "git log --stat -1 --date=format:%d/%m/%Y";
				String[] cmds = { pathSigla, comandoGit };
				StringBuilder log = new StringBuilder();
				log.append("\n \n");
				String author = "", dataCommit = "", descricaoLog;
				Date dataVerificacao = Date.from(LocalDateTime.now().atZone(ZoneId.of("Etc/GMT+3")).toInstant());
				Boolean fatalErro = new Boolean(false);
				controleGitDev.setDataVerificacao(dataVerificacao);

				try {
					ProcessBuilder builder = new ProcessBuilder("cmd", "/c", String.join("& ", cmds));
					builder.redirectErrorStream(true);
					Process p = builder.start();

					BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
					String line;
					int i = 0;
					while ((line = r.readLine()) != null && !fatalErro) {
						i++;
						if (line.contains("fatal")) {
							fatalErro = new Boolean(true);
							erro = line;
							controleGitDev.setErroGitPull(erro);
							controleGitDev.setDataCommit(null);
							controleGitDev.setDataCommitAnt(null);
						}

						if (i == 2 && line.contains("Author")) {
							author = line;
						}
						if (i == 3 && line.contains("Date")) {
							dataCommit = line;
						}

						if (i == 3 && line.contains("Author")) {
							author = line;
						}
						if (i == 4 && line.contains("Date")) {
							dataCommit = line;
						}

						log.append(i + ": " + line + "\n");

					}
					author = author.substring(7, author.length()).trim();
					dataCommit = dataCommit.substring(5, dataCommit.length()).trim();
					descricaoLog = log.toString();

					controleGitDev.setAuthor(author);
					if (controleGitDev.getDataCommit() != null) {
						controleGitDev.setDataCommitAnt(controleGitDev.getDataCommit());
					}
					controleGitDev.setDataCommit(ControleGitDevBean.validadorData(dataCommit, "Data Commit"));
					Date dataAtual = controleGitDev.getDataCommit();
					Date dataAnterior = controleGitDev.getDataCommitAnt();
					dataAnterior = formatadorData(dataAnterior);

					if (dataAtual.equals(dataAnterior)) {
						controleGitDev.setAlteracao(false);
					} else {
						controleGitDev.setAlteracao(true);
					}

					dataVerificacao = Date.from(LocalDateTime.now().atZone(ZoneId.of("Etc/GMT+3")).toInstant());
					controleGitDev.setDescricaoLog(descricaoLog);
				} catch (Exception e) {
					author = "----------";
					controleGitDev.setAuthor(author);
					descricaoLog = "null";
					controleGitDev.setDescricaoLog(descricaoLog);
				} finally {
					System.out.println(erro);
					log = new StringBuilder();
					if (!fatalErro) {
						controleGitDev.setErroGitPull("");
					}
					controleGitDao.editar(controleGitDev);

				}
			}

		}
	};

	/**
	 * Método Git Pull que chama uma nova Thread (gitPull)
	 */
	public void atualizarGit() {

		try {
			new Thread(gitPull).start();
			logDaClasse.info("Git pull em execução!");
		} catch (Exception e) {
			logDaClasse.error("Erro ao executar Git pull!", e);
		}
	}

	/**
	 * Metodo para formatar Data
	 * 
	 * @param data - recebe uma data
	 * @return - retorna um objeto do tipo Date
	 * @author andre.graca
	 */
	public static Date formatadorData(Date data) {
		Calendar c = Calendar.getInstance();
		c.setTime(data);
		String dataString = c.get(Calendar.DAY_OF_MONTH) + "/" + (c.get(Calendar.MONTH) + 1) + "/"
				+ c.get(Calendar.YEAR);
		return ControleGitDevBean.validadorData(dataString, "Data Anterior");
	}

	/**
	 * Runnable para acionar o gitpull e atualizar os pacotes das aplicações
	 */
	private final static Runnable gitPull = new Runnable() {
		public void run() {
			List<ControleGitDev> listaControle;
			ControleGitDevDAO dao = new ControleGitDevDAO();
			listaControle = dao.listar();

			List<ConfigGit> loginsGit = new ConfigGitDAO().listar();

			try {

				for (ConfigGit login : loginsGit) {
					alteraLoginGit(login.getLogin(), login.getAcessoSonar(), login.getUrl());
					executaComandoGitPull(listaControle.stream().filter(
							p -> p.getUsuarioGit().toLowerCase().toString().equals(login.getLogin().toLowerCase()))
							.collect(Collectors.toList()));
				}

				gerarLogGit();
			} catch (Exception e) {
				logDaClasse.error("Erro ao executar Git Pull", e);
			}
		}
	};

	/**
	 * 
	 * Executa o Runnable git clone
	 * 
	 */

	public void executaGitClone() {
		new Thread(gitClone).start();
	}

	/**
	 * Runnable para baixa os pacotes do git
	 */
	private final static Runnable gitClone = new Runnable() {
		public void run() {
			List<ControleGitDev> listaControle;
			ControleGitDevDAO dao = new ControleGitDevDAO();
			listaControle = dao.listar();

			List<ConfigGit> loginsGit = new ConfigGitDAO().listar();

			try {

				for (ConfigGit login : loginsGit) {
					alteraLoginGit(login.getLogin(), login.getAcessoSonar(), login.getUrl());
					executaComandoGitClone(listaControle.stream().filter(
							p -> p.getUsuarioGit().toLowerCase().toString().equals(login.getLogin().toLowerCase()))
							.collect(Collectors.toList()));
				}

				new Thread(gitLog).start();

				logDaClasse.info("Download dos pacotes git iniciado");
			} catch (Exception e) {
				logDaClasse.error("Erro  " + e.getMessage(), e);
			}
		}
	};

	/**
	 * Metodo para executar o comando git pull em cada um dos pacotes do git
	 * 
	 * @param listaControle - o metodo tem que recebr uma lista de objetos do tipo
	 *                      ControleGitDev pois esses objetos contem as informações
	 *                      necessárias para executar a atualização dos pacotes do
	 *                      git
	 * 
	 * @author andre.graca
	 */

	public static void executaComandoGitPull(List<ControleGitDev> listaControle) {

		ControleGitDevDAO dao = new ControleGitDevDAO();
		for (ControleGitDev obj : listaControle) {
			ControleGitDev entidade = dao.buscar(obj.getCodigo());
			String pathSigla = "cd " + entidade.getCaminho();
			String comandoUsuario = "git config --global user.name " + entidade.getUsuarioGit();
			String comandoGit = "git -c http.sslverify=no pull >>LogGit.txt";
			String[] cmds = { pathSigla, comandoUsuario, comandoGit };
			StringBuilder log = new StringBuilder();
			log.append("\n \n");
			logDaClasse.info(obj);
			Boolean fatalErro = new Boolean(false);
			try {
				ProcessBuilder builder = new ProcessBuilder("cmd", "/c", String.join("& ", cmds));
				builder.redirectErrorStream(true);
				Process p = builder.start();

				BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line, erro;

				int i = 0;
				while (true) {
					i++;
					line = r.readLine();
					if (line == null) {
						break;
					}
					if (line.contains("fatal")) {
						fatalErro = new Boolean(true);
						erro = line;
						obj.setErroGitPull(erro);
					}
					log.append(i + ": " + line + "\n");
					logDaClasse.debug(line);
				}
			} catch (Exception e) {
				logDaClasse.error("Erro ao executar GitPull", e);
			} finally {
				if (!fatalErro) {
					obj.setErroGitPull("");
				}
				dao.editar(obj);
			}
		}
	}

	/**
	 * Metodo para executar o clones do pacotes do git
	 * 
	 * @param listaControle - o metodo tem que recebr uma lista de objetos do tipo
	 *                      ControleGitDev pois esses objetos contem as informações
	 *                      necessárias para executar a atualização dos pacotes do
	 *                      git
	 * 
	 * @author andre.graca
	 */

	public static void executaComandoGitClone(List<ControleGitDev> listaControle) {

		ControleGitDevDAO dao = new ControleGitDevDAO();

		for (ControleGitDev obj : listaControle) {
			ControleGitDev entidade = dao.buscar(obj.getCodigo());
			String caminhoSigla = Paths.get(entidade.getCaminho()).getParent().toString();
			String criaPasta = "mkdir " + caminhoSigla;
			String pathSigla = "cd " + caminhoSigla;
			String comandoGit = "git clone -c http.sslverify=no " + entidade.getUrl() + " -b " + entidade.getBranch();
			String[] cmds = { criaPasta, pathSigla, comandoGit };
			StringBuilder log = new StringBuilder();
			Boolean erro = new Boolean(false);
			log.append("\n \n");
			try {
				ProcessBuilder builder = new ProcessBuilder("cmd", "/c", String.join("& ", cmds));
				builder.redirectErrorStream(true);
				Process p = builder.start();

				BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line;
				int i = 0;
				while (true) {
					i++;
					line = r.readLine();
					if (line == null) {
						break;
					}
					if (line.contains("fatal")) {
						erro = new Boolean(true);
						obj.setErroGitPull(line);
					}
					log.append(i + ": " + line + "\n");
					logDaClasse.debug(line);
				}
			} catch (Exception e) {
				logDaClasse.error("Erro ao executar GitClone", e);
			} finally {
				if (!erro) {
					obj.setErroGitPull("");
				}
				dao.editar(obj);
			}
		}
	}

	/**
	 * Metodos para escrever no arquivo C:/Users/usuario_local/_netrc. Este arquivo
	 * salva o login do GitLab na maquina, o que auxilia no git pull para contas
	 * diferentes.
	 * 
	 * @param login - String
	 * @param senha - String
	 * @author andre.graca
	 * @author helio.franca //Alterações.
	 */
	public static void alteraLoginGit(String login, String senha, String url) {

		try (PrintStream ps = new PrintStream("C:/Users/" + System.getProperty("user.name") + "/_netrc");) {
			ps.append("machine " + url + "\nlogin " + login + "\npassword " + senha);
			ps.close();
		} catch (NullPointerException e) {
			logDaClasse.error("Falha ao criar o arquivo _netrc dentro do usuario local", e);
		} catch (Exception e) {
			logDaClasse.error("Falha ao criar o arquivo _netrc dentro do usuario local", e);
		}

	}

	/**
	 * Executa Runnable que cria a estutura de execução do Sonar para todos os
	 * modulos
	 */
	public void validarModulos() {
		try {
			new Thread(validaModulos).start();
		} catch (Exception e) {
			logDaClasse.error("Erro ao validar modulos git", e);
		}
	}

	/**
	 * Runnable que cria a estrutura de execução para todos os modulos do Sonar
	 */
	private final static Runnable validaModulos = new Runnable() {

		@Override
		public void run() {
			ControleGitDevDAO controleDAO = new ControleGitDevDAO();
			for (ControleGitDev moduloParaValidacao : controleDAO.listar()) {
				ControleGitDevBean.controle = moduloParaValidacao;
				moduloParaValidacao.setCapturado(false);
				new ControleGitDevDAO().editar(moduloParaValidacao);
				criarPastas();
			}
		}
	};

	/**
	 * Metodo para criar as pastas target, o arquivo de propriedades do sonar
	 * 
	 */
	private static void criarPastas() {
		logDaClasse.info(controle.getPacoteChaveNomePainelSonar() + " " + controle.getCaminho());

		String caminhoModulo = controle.getCaminho().toString();
		ExecutarSonarPorModulosBean.escreveInformacoesPropertiesSonar(caminhoModulo + "\\" + "sonar-project.properties",
				controle.getChave(), controle.getNomeProjetoPadronizado(), "");

		// Copiando a pasta target para dentro das pastas dos modulos
		Path targetDentroDoPrograma = new MetodosUteis().caminhoTarget();
		Path caminhoDosModulos = Paths.get(caminhoModulo + "\\target");
		try {
			if (!Files.exists(caminhoDosModulos)) {
				Files.createDirectory(caminhoDosModulos);
				copiarArquivos(targetDentroDoPrograma, caminhoDosModulos);
			}
		} catch (Exception e) {
			logDaClasse.error("Erro ao copiar pasta target para " + caminhoModulo);
		}

	}

	/***
	 * 
	 * @param caminho - Path do arquivo ou pasta
	 * @throws IOException - Exceção lançada se ocorrer um erro ao deletar os
	 *                     arquivos
	 * 
	 */

	public static void deletarArquivos(Path caminho) throws IOException {

		if (Files.isDirectory(caminho)) {
			try ( // listamos todas as entradas do diretório
					DirectoryStream<Path> entradas = Files.newDirectoryStream(caminho);) {

				for (Path entrada : entradas) {
					// para cada entrada, achamos o arquivo equivalente dentro
					// de cada arvore
					Path novoArquivo = caminho.resolve(entrada.getFileName());

					// invoca o metodo de maneira recursiva
					deletarArquivos(novoArquivo);
				}
			} catch (IOException e) {
				logDaClasse.error("Erro ao deletar a pasta " + caminho.toAbsolutePath().toString(), e);
			}
		} else {
			// deleta o arquivo
			Files.delete(caminho);
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

		if (Files.isDirectory(origem)) {
			// se é um diretório, tentamos criar. se já existir, não tem
			// problema.
			Files.createDirectories(destino);
			try ( // listamos todas as entradas do diretório
					DirectoryStream<Path> entradas = Files.newDirectoryStream(origem);) {

				for (Path entrada : entradas) {
					// para cada entrada, achamos o arquivo equivalente dentro
					// de cada arvore
					Path novaOrigem = origem.resolve(entrada.getFileName());
					Path novoDestino = destino.resolve(entrada.getFileName());

					// invoca o metodo de maneira recursiva
					copiarArquivos(novaOrigem, novoDestino);
				}
			} catch (IOException e) {
				logDaClasse.error("Erro ao copiar a pasta target para " + destino.toAbsolutePath().toString(), e);
			}
		} else {
			// copiamos o arquivo
			Files.copy(origem, destino);
		}
	}

	/**
	 * Seleciona um objeto ControleGitDev, deixando o objeto marcado para execução
	 * do sonar ou captura em grupo de modulos
	 */
	public void selecionarModulo(ActionEvent evento) {

		try {
			setObj((ControleGitDev) evento.getComponent().getAttributes().get("meuSelect"));
			logDaClasse.info("ID: " + obj.getPacoteChaveNomePainelSonar());
			obj.setSelecionado(obj.getSelecionado().equals(ICONE_BLANK) ? ICONE_CHECK : ICONE_BLANK);
			dao = new ControleGitDevDAO();
			dao.editar(obj);
			listarInfos();
			PrimeFaces.current().ajax().update("formTb:fr:dataTableGitDev");

		} catch (Exception e) {
			logDaClasse.error("Erro ao Selecionar: ", e);
		}
	}

	/**
	 * 
	 * Seleciona o objeto que executa o scan ou captura
	 * 
	 */
	public void selecionarObj(ActionEvent event) {
		try {
			obj = (ControleGitDev) event.getComponent().getAttributes().get("meuSelect");
			logDaClasse.info("PACOTE :" + obj);
		} catch (Exception e) {
			logDaClasse.error("Erro ao selecionar objeto", e);
		}
	}

	/**
	 * Captura as informações de um modulo da aplicação
	 */
	public void capturar() {
		Long ID = obj.getCodigo();
		logDaClasse.info("ID:" + ID);
		Executors.newFixedThreadPool(1, new YourThreadFactory(ID)).submit(captura);
	}

	/**
	 * 
	 * Runnable responsavel por capturar os paineis
	 * 
	 */
	private final static Runnable captura = new Runnable() {

		public void run() {
			try {

				logDaClasse.info("\n-----------------------\nID identificado: " + Thread.currentThread().getName());
				ControleGitDevDAO daoTemp = new ControleGitDevDAO();
				ControleGitDev tempObj = daoTemp.buscar(Long.valueOf(Thread.currentThread().getName()));
				ColetaInformacoesSonarBean sonar = new ColetaInformacoesSonarBean();
				Analise_DiarioDAO analiseDAO = new Analise_DiarioDAO();
				Captura captura = sonar.getSonarApi(tempObj.getPacoteChaveNomePainelSonar());
				if (captura != null) {

					Analise_Dev_Diario analise = new Analise_Dev_Diario(captura);
					analise.setSigla(tempObj.getSigla());
					analise.setNomeProjeto(tempObj.getNomePainel());

					analise.setPadraoNomeSonar(tempObj.getNomeProjetoPadronizado());
					analise.setAmbiente(Ambiente.DESENVOLVIMENTO.getAmbiente());
					analise.setRepositorio(Repositorio.GIT.getRepositorio());

					tempObj.setDataSonar(analise.getDataSonar());
					tempObj.setVersao(analise.getVersao());
					tempObj.setCapturado(true);
					new ControleGitDevDAO().editar(tempObj);
					analiseDAO.salvarAnalise(analise);
					logDaClasse.info("Projeto: " + analise.getNomeProjeto() + " capturado com sucesso");
				}

			} catch (Exception e) {
				logDaClasse.error("Erro ao capturar painel", e);
			}
		}
	};

	/**
	 * 
	 * Metodo responsavel por executar o pacote no Sonar individualmente
	 * 
	 */
	public void executarSonar() {
		Executors.newFixedThreadPool(1, new YourThreadFactory(ControleGitDev.class.getName() + "-" + obj.getId()))
				.submit(ExecutarSonarPorModulosBean.sonarScannerAutomaticoDiaria);
	}

	/**
	 * Exibi o nome do modulo selecioando
	 *
	 */
	public void exibiSelecao() {
		logDaClasse.debug("\n\nSeleção: " + selecao + " obj : " + obj + "\n\n");
	}

	/**
	 * 
	 * Executa o sonar para modulos com atualização de maneira automatica
	 * 
	 */

	public static void main(String[] args) {
		new ControleGitDevBean().executarSonarModulosAtualizadosAutomatico();
	}

	public void executarSonarModulosAtualizadosAutomatico() {
		new Thread(runnableExecutaSonarModulosAtualizadosAt).start();
	}

	private final static Runnable runnableExecutaSonarModulosAtualizadosAt = new Runnable() {

		@Override
		public void run() {
			try {
				ControleGitDevDAO controleGitDAO = new ControleGitDevDAO();
				ArrayList<ControleGitDev> listaModulosAtualizados = (ArrayList<ControleGitDev>) controleGitDAO.listar()
						.stream().filter(c -> c.isAlteracao()).collect(Collectors.toList());
				if (listaModulosAtualizados.isEmpty()) {
					logDaClasse.info("Nenhum modulo Git foi atualizado");
				}
				for (ControleGitDev con : listaModulosAtualizados) {
					con.setCapturado(false);
					controleGitDAO.editar(con);
					logDaClasse.info("\nexecutarSonar :" + con.getPacoteChaveNomePainelSonar() + "\n Caminho: "
							+ con.getCaminho());
					Executors
							.newFixedThreadPool(3,
									new YourThreadFactory(ControleGitDev.class.getName() + "-" + con.getId()))
							.submit(ExecutarSonarPorModulosBean.sonarScannerAutomaticoDiaria);
				}
			} catch (Exception e) {
				e.printStackTrace();
				logDaClasse.error("Erro :" + e + " ao tentar executar o Sonar para modulos atualizados", e);
			}
		}
	};

	// Get e Set
	// ------------------------------------------------------------------------------------------------------------------------------------------------------

	public List<ControleGitDev> getListaControle() {
		return listaControle;
	}

	public void setListaControle(List<ControleGitDev> listaControle) {
		this.listaControle = listaControle;
	}

	public String getPathSigla() {
		return pathSigla;
	}

	public void setPathSigla(String pathSigla) {
		this.pathSigla = pathSigla;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public static String getCAMINHO() {
		return CAMINHO;
	}

	public static void setCAMINHO(String cAMINHO) {
		CAMINHO = cAMINHO;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
		setCAMINHO(path);
	}

	public ControleGitDev getObj() {
		return obj;
	}

	public boolean isSelecao() {
		return selecao;
	}

	public void setSelecao(boolean selecao) {
		this.selecao = selecao;
	}

	public static Runnable getCaptura() {
		return captura;
	}

	public static void setObj(ControleGitDev obj) {
		ControleGitDevBean.obj = obj;
	}

	public static void setControle(ControleGitDev controle) {
		ControleGitDevBean.controle = controle;
	}

	// ------------------------------------------------------------------------------------------------------------------------------------------------------

	public StreamedContent getExemploModulosGit() {
		return new MetodosUteis().getExcelComoStreamedContent("/resources/exemplos_excel/exemplo_moduloGit.xls",
				"exemplo_modulosGit.xls");
	}
}
