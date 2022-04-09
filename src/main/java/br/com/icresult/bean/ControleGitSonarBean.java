package br.com.icresult.bean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Serializable;
import java.nio.charset.Charset;
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
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;

import org.omnifaces.util.Messages;
import org.primefaces.model.StreamedContent;

import br.com.icresult.dao.complementos.Analise_SonarDAO;
import br.com.icresult.dao.complementos.ConfigGitDAO;
import br.com.icresult.dao.complementos.ControleGitSonarDAO;
import br.com.icresult.domain.complementos.Analise_Sonar;
import br.com.icresult.domain.complementos.ConfigGit;
import br.com.icresult.domain.complementos.ControleGitSonar;
import br.com.icresult.model.SonarAtributos;
import br.com.icresult.util.MetodosUteis;
import br.com.icresult.util.YourThreadFactory;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.common.Logger;

/**
 * -Classe BEAN ControleGitSonarBean.
 * 
 * @author andre.graca
 * @version v1.0
 * @since 13-06-2019
 *
 */

@ManagedBean
@SessionScoped
public class ControleGitSonarBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6568171723621268658L;
	private static ControleGitSonar controle;
	private ControleGitSonarDAO dao;
	private List<ControleGitSonar> listaControle;
	private String pathSigla;
	private String path;
	private int total;
	private static ControleGitSonar obj;
	private boolean selecao;
	private static Logger LOG = Logger.getLogger(ControleGitSonarBean.class);
	private String valorEntregaTexto;

	/**
	 * @return - retorna uma lista de ControleGitSonar que foram selecionadas na
	 *         aplicação
	 */
	public List<ControleGitSonar> paineisSelecionados() {
		return new ControleGitSonarDAO().listar().stream().filter(r -> r.getSelecionado()).collect(Collectors.toList());
	}

	/**
	 * @return - retorna uma lista de ControleGitSonar que não foram selecionadas na
	 *         aplicação
	 */
	public List<ControleGitSonar> paineisNaoSelecionados() {
		return new ControleGitSonarDAO().listar().stream().filter(r -> !r.getSelecionado())
				.collect(Collectors.toList());
	}

	/**
	 * Seleciona todos os elementos de ControleGitSonar
	 */
	public void selecionarTodosModulos() {
		ControleGitSonarDAO controleDAO = new ControleGitSonarDAO();
		for (ControleGitSonar controle : paineisNaoSelecionados()) {
			controle.setSelecionado(true);
			controleDAO.editar(controle);
		}
		listarInfos();
	}

	/**
	 * 
	 * Limpa a seleção de todo os elementos de ControleGitSonar
	 * 
	 */
	public void limparSelecaoTodosModulos() {
		ControleGitSonarDAO controleDAO = new ControleGitSonarDAO();
		for (ControleGitSonar controle : paineisSelecionados()) {
			controle.setSelecionado(false);
			controleDAO.editar(controle);
		}
		listarInfos();
	}

	/**
	 * Executa o Sonar para todos os módulos ControleGitSonar selecionados
	 * 
	 */
	public void executarSonarPorModulosSelecionados() {
		try {
			Executors.newFixedThreadPool(1, new YourThreadFactory(valorEntregaTexto))
					.submit(runnableExecutarSonarModulosSelecionados);
		} catch (Exception e) {
			LOG.error("Erro ao executar Sonar", e);
		}

	}

	Runnable runnableExecutarSonarModulosSelecionados = new Runnable() {

		@Override
		public void run() {
			try {
				String tipoEntrega = Thread.currentThread().getName();
				List<ControleGitSonar> listaModulosSelecionados = new ControleGitSonarDAO().listar().stream()
						.filter(r -> r.getSelecionado()).collect(Collectors.toList());
				for (ControleGitSonar controle : listaModulosSelecionados) {
					Executors
							.newFixedThreadPool(3,
									new YourThreadFactory(ControleGitSonar.class.getName() + "-" + controle.getId()
											+ "-" + tipoEntrega))
							.submit(ExecutarSonarPorModulosBean.sonarScannerAutomaticoInspecaoGenerica);
				}
			} catch (Exception e) {
				LOG.error("Erro ao executar Sonar", e);
			}
		}
	};

	/**
	 * Executa o Sonar para todos os modulos ControleGitSonar atualizados
	 */
	public void executarSonarModulosAtualizados() {
		Executors.newFixedThreadPool(1, new YourThreadFactory(valorEntregaTexto))
				.submit(runnableExecutaSonarModulosAtualizados);
	}

	Runnable runnableExecutaSonarModulosAtualizados = new Runnable() {

		@Override
		public void run() {
			try {
				String tipoEntrega = Thread.currentThread().getName();
				ControleGitSonarDAO controleGitSonarDAO = new ControleGitSonarDAO();
				LocalDate hoje = LocalDate.now();
				ArrayList<ControleGitSonar> listaModulosAtualizados = (ArrayList<ControleGitSonar>) controleGitSonarDAO
						.listar().stream()
						.filter(c -> c.getDataCommit() != null && Instant.ofEpochMilli(c.getDataCommit().getTime())
								.atZone(ZoneId.of("Etc/GMT+3")).toLocalDate().equals(hoje) && c.isAlteracao())
						.collect(Collectors.toList());
				if (listaModulosAtualizados.isEmpty()) {
					System.out.println("Nenhum modulo foi atualizado");
				}
				for (ControleGitSonar con : listaModulosAtualizados) {
					LOG.debug("\nexecutarSonar :" + con.getPacoteChaveNomePainelSonar() + "\n Caminho: "
							+ con.getCaminho());

					Executors
							.newFixedThreadPool(3,
									new YourThreadFactory(
											ControleGitSonar.class.getName() + "-" + con.getId() + "-" + tipoEntrega))
							.submit(ExecutarSonarPorModulosBean.sonarScannerAutomaticoInspecaoGenerica);
					Thread.sleep(3000);

				}
			} catch (Exception e) {
				e.printStackTrace();
				LOG.error("Erro :" + e + " ao tentar executar o Sonar para modulos atualziados");
			}
		}
	};

	/**
	 * Executa a captura das informações dos paineis Selecionados na Aplicação
	 * 
	 */
	public void executarCapturaPorModulosSelecionados() {
		ControleGitSonarDAO gitSonarDAO = new ControleGitSonarDAO();
		try {
			List<ControleGitSonar> listaModulosSelecionados = gitSonarDAO.listar().stream()
					.filter(r -> r.getSelecionado()).collect(Collectors.toList());
			for (ControleGitSonar controle : listaModulosSelecionados) {
				LOG.debug(controle.getPacoteChaveNomePainelSonar());
			}
			Executors.newFixedThreadPool(1, new YourThreadFactory(valorEntregaTexto))
					.submit(executaCapturaModulosSelecionados);
		} catch (Exception e) {
			LOG.error("Erro ao capturar modulos", e);
		}

	}

	Runnable executaCapturaModulosSelecionados = new Runnable() {

		@Override
		public void run() {
			try {

				String tipoEntrega = Thread.currentThread().getName();
				List<ControleGitSonar> listaModulosSelecionados = new ControleGitSonarDAO().listar().stream()
						.filter(r -> r.getSelecionado()).collect(Collectors.toList());
				for (ControleGitSonar controle : listaModulosSelecionados) {
					Executors
							.newFixedThreadPool(3,
									new YourThreadFactory(controle.getCodigo().toString() + "-" + tipoEntrega))
							.submit(captura);
					Thread.sleep(5000);
				}
			} catch (Exception e) {
				LOG.error("Erro ao capturar modulos selecionados", e);
			}
		}
	};

	/**
	 * Executa a captura dos modulos que foram atualizados
	 */
	public void executarCapturaPorModulosAtualizados() {
		try {
			Executors.newFixedThreadPool(1, new YourThreadFactory(valorEntregaTexto))
					.submit(runnableExecutarCapturaPorModulosAtualizados);
		} catch (Exception e) {
			LOG.error("Erro ao executar captura dos modulos git atualizados", e);
		}
	}

	/**
	 * Runnable para listar modulos atualizados e enviar os modulos para a Thread de
	 * captura
	 */

	private Runnable runnableExecutarCapturaPorModulosAtualizados = new Runnable() {
		public void run() {
			try {
				String tipoEntrega = Thread.currentThread().getName();
				ControleGitSonarDAO controleGitSonarDAO = new ControleGitSonarDAO();
				LocalDate hoje = LocalDate.now();
				ArrayList<ControleGitSonar> listaPacotesAtualizados = (ArrayList<ControleGitSonar>) controleGitSonarDAO
						.listar().stream().filter(c -> Instant.ofEpochMilli(c.getDataCommit().getTime())
								.atZone(ZoneId.of("Etc/GMT+3")).toLocalDate().equals(hoje) && c.isAlteracao())
						.collect(Collectors.toList());

				if (listaPacotesAtualizados.isEmpty()) {
					LOG.info("Nenhum modulo foi atualizado");
				} else {
					LOG.info(listaPacotesAtualizados.size() + " pacotes com atualizacao.");
				}
				for (ControleGitSonar con : listaPacotesAtualizados) {
					LOG.info(con.getPacoteChaveNomePainelSonar());
					Executors
							.newFixedThreadPool(3,
									new YourThreadFactory(con.getCodigo().toString() + "-" + tipoEntrega))
							.submit(captura);
					Thread.sleep(5000);
				}
			} catch (Exception e) {
				LOG.error("Erro ao executar a captura dos modoulos com atualização: ", e);
			}
		}
	};

	/**
	 * 
	 * Seleciona o objeto que executa o scan ou captura
	 * 
	 */
	public void selecionarObj(ActionEvent event) {
		try {
			obj = (ControleGitSonar) event.getComponent().getAttributes().get("meuSelect");
			LOG.info("PACOTE :" + obj);
		} catch (Exception e) {
			LOG.error("Erro ao selecionar objeto", e);
		}
	}

	/**
	 * // Salvar um objeto do tipo ControleGitDev
	 */
	private void salvar() {
		try {
			dao.salvar(controle);
		} catch (Exception e) {
			LOG.error("Não foi possível salvar o objeto", e);
		}
	}

	/**
	 * Lista os objetos do tipo ControleGitDev
	 */
	public void listarInfos() {
		try {
			dao = new ControleGitSonarDAO();
			dao.preencherCampoSelecionado();
			validarModulos();
			listaControle = dao.listar();
			total = listaControle.size();
			LOG.info("Lista Atualizada!");
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Erro ao  Atualizar Lista.", e);
		}
	}

	/**
	 * Captura as informações de uma planilha xls e salva no banco de dados
	 * 
	 * @param caminhoArquivo
	 */
	public void salvarPlanilha(String caminhoArquivo) {
		controle = new ControleGitSonar();
		dao = new ControleGitSonarDAO();
		String sigla, sistema, caminho, pacoteChavePainelSonar, usuarioGit, url, branch;
		Date dateC = Date.from(LocalDateTime.now().atZone(ZoneId.of("Etc/GMT+3")).toInstant());

		// Carrega a planilha
		Workbook workbook = null;
		try {
			workbook = Workbook.getWorkbook(new File(caminhoArquivo));

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
				Cell celula6 = sheet.getCell(5, i); // coluna 5 - url
				Cell celula7 = sheet.getCell(6, i); // coluna 6 - branch

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
					dateC = Date.from(LocalDateTime.now().atZone(ZoneId.of("Etc/GMT+3")).toInstant());
					controle.setSigla(sigla);
					controle.setNomeSistema(sistema);
					controle.setCaminho(caminho);
					controle.setPacoteChaveNomePainelSonar(pacoteChavePainelSonar);
					controle.setNomeArquivo(caminhoArquivo);
					controle.setDataVerificacao(dateC);
					controle.setUsuarioGit(usuarioGit);
					controle.setUrl(url);
					controle.setBranch(branch);
					controle.setDataCommitAnt(null);
					
					salvar();
				}
			}
			validarModulos();
			Messages.addGlobalInfo("Planilha salva com sucesso!");
		} catch (Exception e) {
			LOG.error("Erro ao salvar planilha", e);
			Messages.addGlobalError("Não foi possível salvar (salvarPlanilha())");
		}
	}

	/**
	 * Limpa a tabela do banco de dados.
	 */
	public void limparDB() {
		try {
			listarInfos();
			for (ControleGitSonar controleGit : listaControle) {
				dao.excluir(controleGit);
			}
		} catch (Exception e) {
			LOG.error("Não foi possivel limpar o Banco de Dados", e);
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
		Date dataFinal = Date.from(LocalDateTime.now().atZone(ZoneId.of("Etc/GMT+3")).toInstant());
		if (dataInfo.isEmpty()) {
			dataFinal = null;
		} else {
			try {
				dataFinal = (Date) formatter.parse(dataInfo);
			} catch (ParseException e) {
				dataFinal = null;
				LOG.error("-Erro em data " + msg, e);
			}
		}
		return dataFinal;
	}
	
	public static void main(String[] args) {
		gerarLogGit();
	}

	/**
	 * Chama o Runnable do gitlog
	 */
	public static void gerarLogGit() {
		try {
			new Thread(gitLog).start();
			LOG.info("Git log em execução!");
		} catch (Exception e) {
			LOG.error("Erro ao executar Git Log!", e);
		}
	}

	/**
	 * Runnable para acionar o comando gitlog e capturar as informações.
	 */
	private static Runnable gitLog = new Runnable() {
		public void run() {
			List<ControleGitSonar> listaControle;
			ControleGitSonarDAO dao = new ControleGitSonarDAO();
			listaControle = dao.listar().stream()
					.filter(modulo -> Files.exists(Paths.get(modulo.getCaminho()))).
					collect(Collectors.toList());

			for (ControleGitSonar controle : listaControle) {
				ControleGitSonar entidade = dao.buscar(controle.getCodigo());
				String pathSigla = "cd " + entidade.getCaminho();
				String comandoGit = "git log --stat -1 --date=format:%d/%m/%Y";
				String[] cmds = { pathSigla, comandoGit };
				StringBuilder log = new StringBuilder();
				log.append("\n \n");
				String author = "", dataCommit = "", descricaoLog;
				Date dataVerificacao = Date.from(LocalDateTime.now().atZone(ZoneId.of("Etc/GMT+3")).toInstant());
				controle.setDataVerificacao(dataVerificacao);

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

						if (line == null) {
							break;
						}
						log.append(i + ": " + line + "\n");

					}
					author = author.substring(7, author.length()).trim();
					dataCommit = dataCommit.substring(5, dataCommit.length()).trim();
					descricaoLog = log.toString();

					controle.setAuthor(author);
					controle.setDataCommitAnt(controle.getDataCommit());
					controle.setDataCommit(ControleGitSonarBean.validadorData(dataCommit, "Data Commit"));
					Date dataAtual = controle.getDataCommit();
					Date dataAnterior = controle.getDataCommitAnt();
					dataAnterior = formatadorData(dataAnterior);

					if (dataAtual.equals(dataAnterior)) {
						controle.setAlteracao(false);
					} else {
						controle.setAlteracao(true);
					}

					dataVerificacao = Date.from(LocalDateTime.now().atZone(ZoneId.of("Etc/GMT+3")).toInstant());
					controle.setDescricaoLog(descricaoLog);

				} catch (Exception e) {
					author = "----------";
					controle.setAuthor(author);
					descricaoLog = "null";
					controle.setDescricaoLog(descricaoLog);
				} finally {
					dao.editar(controle);

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
			LOG.info("Git pull em execução!");
		} catch (Exception e) {
			LOG.error("Erro ao executar Git pull!", e);
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
		return ControleGitSonarBean.validadorData(dataString, "Data Anterior");
	}

	/**
	 * Runnable para acionar o gitpull e atualizar os pacotes das aplicações
	 */
	private static Runnable gitPull = new Runnable() {
		public void run() {

			try {
				List<ControleGitSonar> listaControle;
				ControleGitSonarDAO dao = new ControleGitSonarDAO();

				listaControle = dao.listar();

				ConfigGitDAO daoConfigGit = new ConfigGitDAO();

				for (ConfigGit configGit : daoConfigGit.listar()) {
					LOG.debug(configGit);
					alteraLoginGit(configGit.getUrl(), configGit.getLogin(), configGit.getAcessoSonar());
					executaComandoGitPull(listaControle.stream()
							.filter(p -> p.getUsuarioGit().toString().equalsIgnoreCase(configGit.getLogin()))
							.collect(Collectors.toList()));

				}
				LOG.info("Atualização dos pacotes git finalizado");
				gerarLogGit();
			} catch (Exception e) {
				LOG.error("Erro  " + e.getMessage(), e);
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
	private static Runnable gitClone = new Runnable() {
		public void run() {

			try {
				List<ControleGitSonar> listaControle;
				ControleGitSonarDAO dao = new ControleGitSonarDAO();

				listaControle = dao.listar();

				ConfigGitDAO daoConfigGit = new ConfigGitDAO();

				for (ConfigGit configGit : daoConfigGit.listar()) {
					LOG.debug(configGit);
					alteraLoginGit(configGit.getUrl(), configGit.getLogin(), configGit.getAcessoSonar());
					executaComandoGitClone(listaControle.stream()
							.filter(p -> p.getUsuarioGit().toString().equalsIgnoreCase(configGit.getLogin()))
							.collect(Collectors.toList()));

				}
				LOG.info("Download dos pacotes git finalizado");
				gerarLogGit();
			} catch (Exception e) {
				LOG.error("Erro  " + e.getMessage(), e);
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

	public static void executaComandoGitPull(List<ControleGitSonar> listaControle) {

		ControleGitSonarDAO dao = new ControleGitSonarDAO();
		for (ControleGitSonar obj : listaControle) {
			ControleGitSonar entidade = dao.buscar(obj.getCodigo());
			String pathSigla = "cd " + entidade.getCaminho();
			String comandoUsuario = "git config --global user.name " + entidade.getUsuarioGit();
			String comandoGit = "git -c http.sslverify=no pull >>LogGit.txt";
			String[] cmds = { pathSigla, comandoUsuario, comandoGit };
			StringBuilder log = new StringBuilder();
			log.append("\n \n");
			System.out.println(obj);
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
					LOG.debug(line);
				}
			} catch (Exception e) {
				LOG.error("Erro ao executar GitPull", e);
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
	 *                      ControleGitSonar pois esses objetos contem as
	 *                      informações necessárias para executar a atualização dos
	 *                      pacotes do git
	 * 
	 * @author andre.graca
	 */

	public static void executaComandoGitClone(List<ControleGitSonar> listaControle) {

		ControleGitSonarDAO dao = new ControleGitSonarDAO();

		for (ControleGitSonar obj : listaControle) {
			ControleGitSonar entidade = dao.buscar(obj.getCodigo());
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
					LOG.debug(line);
				}
			} catch (Exception e) {
				LOG.error("Erro ao executar GitClone", e);
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
	public static void alteraLoginGit(String urlgit, String login, String senha) {

		try (PrintStream ps = new PrintStream("C:/Users/" + System.getProperty("user.name") + "/_netrc");) {
			ps.append("machine " + urlgit + "\nlogin " + login + "\npassword " + senha);
			ps.close();
		} catch (NullPointerException e) {
			LOG.error("Falha ao criar o arquivo _netrc dentro do usuario local", e);
		} catch (Exception e) {
			LOG.error("Falha ao criar o arquivo _netrc dentro do usuario local", e);
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
			LOG.error("Erro ao validar modulos git", e);
		}
	}

	/**
	 * Runnable que cria a estrutura de execução para todos os modulos do Sonar
	 */
	private static Runnable validaModulos = new Runnable() {

		@Override
		public void run() {
			ControleGitSonarDAO controleDAO = new ControleGitSonarDAO();
			for (ControleGitSonar controle : controleDAO.listar()) {
				ControleGitSonarBean.controle = controle;
				criarPastas();
			}
		}
	};

	/**
	 * Metodo para criar as pastas target, o arquivo de propriedades do sonar
	 * 
	 */
	private static void criarPastas() {
		LOG.info(controle.getPacoteChaveNomePainelSonar() + " " + controle.getCaminho());
		StringBuilder caminhoModulo = new StringBuilder(controle.getCaminho());
		StringBuilder caminhoPropertiesSonar = new StringBuilder(
				caminhoModulo.toString() + "\\" + "sonar-project.properties");
		Path caminhoDoSonarScannerPropertiesDentroDoModulo = Paths.get(caminhoPropertiesSonar.toString());
		if (!Files.exists(caminhoDoSonarScannerPropertiesDentroDoModulo)) {
			try {
				Files.createFile(caminhoDoSonarScannerPropertiesDentroDoModulo);
			} catch (Exception e) {
				LOG.error("Erro ao criar arquivo sonar.properties", e);
			}
		}
		try (FileInputStream in = new FileInputStream(caminhoDoSonarScannerPropertiesDentroDoModulo.toString());
				FileOutputStream out = new FileOutputStream(caminhoDoSonarScannerPropertiesDentroDoModulo.toString());) {
			// Criando o arquivo .properties necessario para a execução do Sonar
			Properties sonarProject = new Properties();
			sonarProject.load(new InputStreamReader(in, Charset.forName("UTF-8")));
			sonarProject.put("sonar.projectKey", controle.getPacoteChaveNomePainelSonar());
			sonarProject.put("sonar.projectName", controle.getPacoteChaveNomePainelSonar());
			sonarProject.put("sonar.projectVersion", "");
			sonarProject.put("sonar.sources", ".");
			sonarProject.put("sonar.java.binaries", "target/classes");
			sonarProject.put("sonar.exclusions", "**/*test.java,**/*teste.java,**/*.properties");
			sonarProject.put("sonar.cfamily.build-wrapper-output.bypass", "true");
			sonarProject.put("sonar.sourceEncoding", "ISO-8859-1");
			sonarProject.store(out, "Properties Sonar " + caminhoPropertiesSonar);

		} catch (Exception e) {
			LOG.error("Erro ao escrever no arquivo sonar.properties", e);
		}
		// Copiando a pasta target para dentro das pastas dos modulos
		Path targetDentroDoPrograma = new MetodosUteis().caminhoTarget();
		Path pastaTargetDentroDoModulo = Paths.get(caminhoModulo + "\\target");
		try {
			if (!Files.exists(pastaTargetDentroDoModulo)) {
				Files.createDirectory(pastaTargetDentroDoModulo);
				copiarArquivos(targetDentroDoPrograma, pastaTargetDentroDoModulo);
			}
		} catch (Exception e) {
			LOG.error("Erro ao criar pasta target", e);
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
				LOG.error("Erro ao deletar a pasta " + caminho.toAbsolutePath().toString(), e);
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
				LOG.error("Erro ao copiar a pasta target para " + destino.toAbsolutePath().toString(), e);
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
			obj = (ControleGitSonar) evento.getComponent().getAttributes().get("meuSelect");
			LOG.info("ID: " + obj.getPacoteChaveNomePainelSonar());
			obj.setSelecionado(!obj.getSelecionado());
			new ControleGitSonarDAO().editar(obj);

		} catch (Exception e) {
			LOG.error("Erro ao Selecionar: ", e);
		}
	}

	/**
	 * Captura as informações de um modulo da aplicação
	 */
	public void capturar() {
		Long ID = obj.getCodigo();
		LOG.info("ID:" + ID);
		LOG.info("Valor Teste:" + this.valorEntregaTexto);
		Executors.newFixedThreadPool(1, new YourThreadFactory(ID + "-" + valorEntregaTexto)).submit(captura);
	}

	/**
	 * 
	 * Runnable responsavel por capturar os paineis
	 * 
	 */
	private static Runnable captura = new Runnable() {

		public void run() {
			try {
				String IDComTipoEntrega = Thread.currentThread().getName();
				String[] split = IDComTipoEntrega.split("-");
				Long id = Long.parseLong(split[0]);
				String tipoEntrega = split[1];
				System.out.println("\n-----------------------\nID identificado: " + id);
				System.out.println("\n-----------------------\nEntrega identificada: " + tipoEntrega);
				ControleGitSonarDAO daoTemp = new ControleGitSonarDAO();
				ControleGitSonar tempObj = daoTemp.buscar(id);
				SonarAPIBean sonar = new SonarAPIBean();
				Analise_SonarDAO analiseDAO = new Analise_SonarDAO();
				SonarAtributos captura = sonar.getSonarApi(tempObj.getPacoteChaveNomePainelSonar());
				if (captura != null) {
					Analise_Sonar analise = new Analise_Sonar(captura);
					analise.setSigla(tempObj.getSigla());
					analise.setTipoEntrega(tipoEntrega);
					analiseDAO.salvar(analise);
					LOG.info("Projeto: " + analise.getModulo() + " capturado com sucesso");
				}

			} catch (Exception e) {
				LOG.error("Erro ao capturar painel", e);
			}
		}
	};

	/**
	 * 
	 * Metodo responsavel por executar o pacote no Sonar individualmente
	 * 
	 */
	public void executarSonar() {
		Executors
				.newFixedThreadPool(1,
						new YourThreadFactory(
								ControleGitSonar.class.getName() + "-" + obj.getId() + "-" + valorEntregaTexto))
				.submit(ExecutarSonarPorModulosBean.sonarScannerAutomaticoInspecaoGenerica);
	}

	/**
	 * Exibi o nome do modulo selecioando
	 *
	 */
	public void exibiSelecao() {
		LOG.debug("\n\nSeleção: " + selecao + " obj : " + obj + "\n\n");
	}

	// Get e Set
	// ------------------------------------------------------------------------------------------------------------------------------------------------------

	public List<ControleGitSonar> getListaControle() {
		return listaControle;
	}

	public void setListaControle(List<ControleGitSonar> listaControle) {
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

	public String getPath() {
		return path;
	}

	public ControleGitSonar getObj() {
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

	public String getValorEntregaTexto() {
		return valorEntregaTexto;
	}

	public void setValorEntregaTexto(String valorEntregaTexto) {
		this.valorEntregaTexto = valorEntregaTexto;
	}

	// ------------------------------------------------------------------------------------------------------------------------------------------------------

	public StreamedContent getExemploCarga() {
		return new MetodosUteis().getExcelComoStreamedContent("/resources/exemplos_excel/exemplo_carga_git.xls",
				"exemplo_carga_git.xls");
	}
}
