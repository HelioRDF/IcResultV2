
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;

import org.omnifaces.util.Messages;

import br.com.icresult.dao.complementos.Analise_HomologacaoDAO;
import br.com.icresult.dao.complementos.ConfigGitDAO;
import br.com.icresult.dao.complementos.ControleGitHKDAO;
import br.com.icresult.dao.complementos.ControleSiglasDAO;
import br.com.icresult.dao.complementos.RelacaoProjetoSiglaGestorDAO;
import br.com.icresult.domain.complementos.Analise_Homologacao;
import br.com.icresult.domain.complementos.ConfigGit;
import br.com.icresult.domain.complementos.ControleGitHK;
import br.com.icresult.domain.complementos.RelacaoProjetoSiglaGestor;
import br.com.icresult.email.EnviarEmail;
import br.com.icresult.email.GitList;
import br.com.icresult.model.Captura;
import br.com.icresult.nomeprojeto.Ambiente;
import br.com.icresult.nomeprojeto.ProjectName;
import br.com.icresult.nomeprojeto.Repositorio;
import br.com.icresult.util.CodeInspecion;
import br.com.icresult.util.ComunicadorAPIRSI;
import br.com.icresult.util.MetodosUteis;
import br.com.icresult.util.YourThreadFactory;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.common.Logger;

/**
 * -Classe BEAN ControleGitHKBean.
 * 
 * @author helio.franca
 * @version v2.1.3
 * @since 08-08-2018
 *
 */

@SuppressWarnings("serial")
@ManagedBean
@SessionScoped
public class ControleGitHKBean implements Serializable {
	static final Runtime run = Runtime.getRuntime();
	static Process pro;
	static BufferedReader read;
	private static ControleGitHK controle;
	private ControleGitHKDAO dao;
	private List<ControleGitHK> listaControle;
	private String pathSigla;
	String path;
	private int total;
	static String CAMINHO = "";
	private String chaveEMail;
	private String origemEMail;
	private String destinoEMail;
	private ControleGitHK obj;
	private static Logger LOG = Logger.getLogger(ControleGitHKBean.class);

	/**
	 * Dispara o envio de E-mail
	 */
	// -------------------------------------------------------------------------------------
	public void enviarEmail() {
		Messages.addGlobalWarn("Teste");
		EnviarEmail email = new EnviarEmail();
		ControleGitHKDAO gitDao = new ControleGitHKDAO();
		String resultado = "";
		List<ControleGitHK> git;
		git = gitDao.listarOrdenandoAlteracao();

		for (ControleGitHK obj : git) {
			GitList list = new GitList();
			resultado += list.alertaGit(obj.getSigla(), obj.getNomeSistema(), obj.getPacote(), obj.getDataCommit(),
					obj.getDataCommitAnt(), obj.isAlteracao());
			LOG.info(resultado);
		}
		email.emailHtmlGIT(resultado, "Monitor GIT", chaveEMail, origemEMail, destinoEMail);

	}

	/**
	 * Salva objeto do tipo ControleGitHK
	 */
	// -------------------------------------------------------------------------------------
	private void salvar() {
		try {
			dao.salvar(controle);
		} catch (Exception e) {
			LOG.error("Não foi possivel salvar o objeto", e);
		}
	}

	/**
	 * Lista os objetos do tipo ControleGitHK
	 */
	// ------------------------------------------------------------------------------------------------------------------------------------------------------
	public void listarInfos() {
		try {
			dao = new ControleGitHKDAO();
			listaControle = dao.listar();
			total = listaControle.size();
			LOG.info("Lista Atualizada!");
		} catch (Exception e) {
			LOG.error("Erro ao  Atualizar Lista.", e);
		}
	}

	/**
	 * Captura as informações de uma planilha xls e salva no banco de dados
	 * 
	 */
	// -------------------------------------------------------------------------------------------
	public void salvarPlanilha() {
		controle = new ControleGitHK();
		dao = new ControleGitHKDAO();
		String sigla, sistema, caminho, pacote, usuarioGit, urlGit, branch;
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
				Cell celula6 = sheet.getCell(5, i); // coluna 5 - url do git
				Cell celula7 = sheet.getCell(6, i); // coluna 6 - branch

				sigla = celula1.getContents().toString().trim().toUpperCase();
				sistema = celula2.getContents().toString().trim().toUpperCase();
				pacote = celula3.getContents().toString().trim().toUpperCase();
				caminho = celula4.getContents().toString().trim().toUpperCase();
				usuarioGit = celula5.getContents().toString().trim().toUpperCase();
				urlGit = celula6.getContents().toString().trim();
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
					controle.setPacote(pacote);
					controle.setNomeArquivo(CAMINHO);
					controle.setDataVerificacao(dateC);
					controle.setUsuarioGit(usuarioGit);
					controle.setNomeProjetoPadronizado(
							new ProjectName(sigla, pacote, Repositorio.GIT, Ambiente.HOMOLOGACAO)
									.getPadraoNomeProjeto());
					controle.setUrl(urlGit);
					controle.setBranch(branch);

					salvar();
				}
			}
			Messages.addGlobalInfo("Planilha salva com sucesso!");
		} catch (Exception e) {
			Messages.addGlobalError("Erro ao salvar a planilha");
			LOG.error("Não foi possível salvar planilha", e);
		}
	}

	/**
	 * Limpa as informações da tabela ControleGitHK no banco de dados
	 */
	// -------------------------------------------------------------------------------------
	public void limparDB() {
		try {
			listarInfos();
			for (ControleGitHK ControleGitHK : listaControle) {
				ControleGitHK entidade = dao.buscar(ControleGitHK.getCodigo());
				dao.excluir(entidade);
			}
		} catch (Exception e) {
			LOG.error("Não foi possível limparDB", e);
		}
	}

	/**
	 * Valida e converte uma objeto do tipo data
	 * 
	 * @param dataInfo - data para validara
	 * @param msg      - mensagem opcional
	 * @return - retorna um objeto do tipo data
	 */
	// ------------------------------------------------------------------------------------------------------------------------------------------------------
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
				LOG.error("Erro em data" + msg, e);
			}
		}
		return dataFinal;
	}

	/**
	 * Chama o Runnable do gitClone
	 */
	// ------------------------------------------------------------------------------------------------------------------------------------------------------
	public static void baixarPacotes() {
		try {
			new Thread(gitClone).start();
			LOG.info("Git clone em execução!");
		} catch (Exception e) {
			LOG.error("Erro ao executar git clone!", e);
		}
	}

	/**
	 * Runnable para baixa os pacotes do git
	 */
	private final static Runnable gitClone = new Runnable() {
		public void run() {
			List<ControleGitHK> listaControle;
			ControleGitHKDAO dao = new ControleGitHKDAO();
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

				LOG.info("Download dos pacotes git iniciado");
			} catch (Exception e) {
				LOG.error("Erro  " + e.getMessage(), e);
			}
		}
	};

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

	public static void executaComandoGitClone(List<ControleGitHK> listaControle) {

		ControleGitHKDAO dao = new ControleGitHKDAO();

		for (ControleGitHK obj : listaControle) {
			ControleGitHK entidade = dao.buscar(obj.getCodigo());
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
	 * Chama o Runnable do gitlog
	 */
	// ------------------------------------------------------------------------------------------------------------------------------------------------------
	public static void gerarLogGit() {
		try {
			new Thread(gitLog).start();
			LOG.info("Git Log em execução");
		} catch (Exception e) {
			LOG.error("Erro ao executar Git Log!", e);
		}
	}

	/**
	 * Runnable para acionar o comando gitlog e capturar as informações.
	 */
	// -------------------------------------------------------------------------------------
	private static Runnable gitLog = new Runnable() {
		public void run() {
			List<ControleGitHK> listaControle;
			ControleGitHKDAO dao = new ControleGitHKDAO();
			listaControle = dao.listar();
			for (ControleGitHK ControleGitHK : listaControle) {
				ControleGitHK entidade = dao.buscar(ControleGitHK.getCodigo());
				String pathSigla = "cd " + entidade.getCaminho();
				String comandoGit = "git log --stat -1 --date=format:%d/%m/%Y";
				String[] cmds = { pathSigla, comandoGit };
				StringBuilder log = new StringBuilder();
				log.append("\n \n");
				String author = "", dataCommit = "", descricaoLog;
				Date dataVerificacao = Date.from(LocalDateTime.now().atZone(ZoneId.of("Etc/GMT+3")).toInstant());

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

					ControleGitHK.setAuthor(author);
					ControleGitHK.setDataCommitAnt(ControleGitHK.getDataCommit());
					ControleGitHK.setDataCommit(ControleGitHKBean.validadorData(dataCommit, "Data Commit"));
					Date dataAtual = ControleGitHK.getDataCommit();
					Date dataAnterior = ControleGitHK.getDataCommitAnt();
					if (dataAnterior != null) {
						dataAnterior = formatadorData(dataAnterior);

						if (dataAtual.equals(dataAnterior)) {
							ControleGitHK.setAlteracao(false);
						} else {
							ControleGitHK.setAlteracao(true);
						}
					}

					ControleGitHK.setDataVerificacao(dataVerificacao);
					ControleGitHK.setDescricaoLog(descricaoLog);

				} catch (Exception e) {
					LOG.error("Erro ao executar Git Log", e);
					author = "----------";
					ControleGitHK.setAuthor(author);
					descricaoLog = "null";
					ControleGitHK.setDescricaoLog(descricaoLog);
				} finally {
					dao.editar(ControleGitHK);
				}
			}

		}
	};

	/**
	 * Método Git Pull que chama uma nova Thread (gitPull)
	 */
	// ------------------------------------------------------------------------------------------------------------------------------------------------------
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
		return ControleGitHKBean.validadorData(dataString, "Data Anterior");
	}

	/**
	 * Runnable para acionar o gitpull e atualizar os pacotes das aplicações
	 */
	private final static Runnable gitPull = new Runnable() {
		public void run() {
			List<ControleGitHK> listaControle;
			ControleGitHKDAO dao = new ControleGitHKDAO();
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
				System.err.println("Erro ao executar Git Pull : " + e);
			}
		}
	};

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
			System.err.println("Falha ao criar o arquivo _netrc dentro do usuario local : " + e);
		} catch (Exception e) {
			System.err.println("Falha ao criar o arquivo _netrc dentro do usuario local : " + e);
		}

	}

	/**
	 * Metodo para executar o comando git pull em cada um dos pacotes do git
	 * 
	 * @param listaControle - o metodo tem que recebr uma lista de objetos do tipo
	 *                      ControleGitHK pois esses objetos contem as informações
	 *                      necessárias para executar a atualização dos pacotes do
	 *                      git
	 * 
	 * @author andre.graca
	 */

	public static void executaComandoGitPull(List<ControleGitHK> listaControle) {

		ControleGitHKDAO dao = new ControleGitHKDAO();

		for (ControleGitHK obj : listaControle) {
			ControleGitHK entidade = dao.buscar(obj.getCodigo());
			String pathSigla = "cd " + entidade.getCaminho();
			String comandoGit = "git -c http.sslverify=no pull >>LogGit.txt";
			String[] cmds = { pathSigla, comandoGit };
			StringBuilder log = new StringBuilder();
			log.append("\n \n");
			System.out.println(obj);
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
					log.append(i + ": " + line + "\n");
					LOG.debug(line);
				}
				// Messages.addGlobalInfo("Executado com sucesso!");
			} catch (Exception e) {
				// LOG.error("Caminho não encontrado ...
				// :\n"
				// +
				// ControleGitHKDev.getNomeSistema());
			} finally {
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
	public static void alteraLoginGit(String login, String senha) {
		String caminho = "C:/Users/" + System.getProperty("user.name") + "/_netrc";
		try (PrintStream ps = new PrintStream(caminho)) {
			ps.append("machine gitlab.produbanbr.corp\nlogin " + login + "\npassword " + senha);
			ps.close();
		} catch (NullPointerException e) {
			LOG.error("Falha ao criar o arquivo _netrc dentro do usuario local", e);
		} catch (Exception e) {
			LOG.error("Falha ao criar o arquivo _netrc dentro do usuario local", e);
		}
	}

	/**
	 * 
	 * Seleciona o objeto que executa o scan ou captura
	 * 
	 */

	public void selecionarObj(ActionEvent event) {
		try {
			obj = (ControleGitHK) event.getComponent().getAttributes().get("meuSelect");
			LOG.info("PACOTE :" + obj);
		} catch (Exception e) {
			LOG.error("Erro ao selecionar objeto", e);
		}
	}

	/***
	 * Captura as informações de um modulo
	 * 
	 */
	public void capturar() {
		Long ID = obj.getCodigo();
		LOG.info("ID:" + ID);
		Executors.newFixedThreadPool(1, new YourThreadFactory(ID)).submit(captura);
	}

	/**
	 * 
	 * Metodo responsavel por executar o pacote no Sonar individualmente
	 * 
	 */

	public void executarSonar() {
		String nomeSistema = obj.getNomeSistema();

		Optional<RelacaoProjetoSiglaGestor> painelPesquisado = new RelacaoProjetoSiglaGestorDAO().listar().stream()
				.filter(r -> r.getNome_Projeto().equals(nomeSistema)).findFirst();

		if (painelPesquisado.isPresent()) {

			String chave = painelPesquisado.get().getChave();
			System.out.println("Chave: " + chave);
			ControleGitHKDAO dao = new ControleGitHKDAO();
			dao.validarChave();
			Optional<ControleGitHK> controle = dao.listar().stream().filter(c -> c.getNomeSistema().equals(nomeSistema))
					.findFirst();
			if (controle.isPresent()) {
				String caminhoTarget = controle.get().getCaminho() + "\\target";
				String caminhoProperties = controle.get().getCaminho() + "\\sonar-project.properties";
				ControleGitHKBean.controle = controle.get();
				if (!Files.exists(Paths.get(caminhoTarget)) || !Files.exists(Paths.get(caminhoProperties))) {
					criarPastas();
				}
				Executors
						.newFixedThreadPool(3,
								new YourThreadFactory(ControleGitHK.class.getName() + "-" + controle.get().getId()))
						.submit(ExecutarSonarPorModulosBean.sonarScannerAutomaticoDiaria);
			}

		}
	}

	/**
	 * 
	 * Runnable responsavel por capturar os paineis
	 * 
	 */

	private static Runnable captura = new Runnable() {

		public void run() {
			try {

				LOG.info("ID identificado: " + Thread.currentThread().getName());
				ControleGitHKDAO daoTemp = new ControleGitHKDAO();
				ControleGitHK tempObj = daoTemp.buscar(Long.valueOf(Thread.currentThread().getName()));
				ColetaInformacoesSonarBean sonar = new ColetaInformacoesSonarBean();
				Analise_HomologacaoDAO analiseDAO = new Analise_HomologacaoDAO();
				Optional<RelacaoProjetoSiglaGestor> painelPesquisado = new RelacaoProjetoSiglaGestorDAO().listar()
						.stream().filter(r -> r.getNome_Projeto().equals(tempObj.getNomeSistema())).findFirst();
				if (painelPesquisado.isPresent()) {
					String chave = painelPesquisado.get().getChave();
					String repositorio = tempObj.getRepositorio();
					String ambiente = tempObj.getAmbiente();
					Captura captura = sonar.getSonarApi(chave);
					if (captura != null) {
						Analise_Homologacao analise = new Analise_Homologacao(captura);
						analise.setPainelGestor(new ControleSiglasDAO().buscaGestorPorSigla(tempObj.getSigla()));
						analise.setSigla(tempObj.getSigla());
						analise.setNomeProjeto(tempObj.getNomeSistema());
						analise.setAmbiente(ambiente);
						analise.setRepositorio(repositorio);
						analise.setPadraoNomeSonar(tempObj.getNomeProjetoPadronizado());
						analise.setDataCommitAnterior(tempObj.getDataCommitAnt() == null ? ""
								: tempObj.getDataCommit().toString().substring(0, 10));
						analise.setDataCommit(tempObj.getDataCommit() == null ? ""
								: tempObj.getDataCommit().toString().substring(0, 10));
						analise.setDevOps("NÃO");
						LOG.info(analise.toString());
						analiseDAO.salvar(analise);

						Analise_Homologacao_Bean analise_Homologacao_Bean = new Analise_Homologacao_Bean();
						analise_Homologacao_Bean.calcNotaInfosAnt();

						analise.setNotaProjeto(analise_Homologacao_Bean.geraNotaDoProjeto(analise));

						CodeInspecion.Builder codeInspectionBuilder = new CodeInspecion.Builder()
								.withComponentCode(chave).withAplicationScore(Double.valueOf(analise.getNotaProjeto()))
								.withBeforeCommitDate(tempObj.getDataCommitAnt())
								.withCurrentCommitDate(tempObj.getDataCommit())
								.withBugQuantityHigh(analise.getIssuesMuitoAlta())
								.withBugQuantityMediumHigh(analise.getIssuesAlta())
								.withBugQuantityMedium(analise.getIssuesMedia())
								.withBugQuantityLow(analise.getIssuesBaixa())
								.withCodeSmellQuantity(analise.getCodeSmall())
								.withExecutionDate(analise.getDataCaptura()).withLoc(analise.getLinhaCodigo())
								.withModified(tempObj.isAlteracao())
								.withSecurityQuantity(analise.getVulnerabilidades());
						if (analise.getCobertura() != null) {
							codeInspectionBuilder
									.withUnitTestCoverage(Double.valueOf(analise.getCobertura().equals("null") ? "0.0"
											: analise.getCobertura().replace('%', ' ')));
						} else {
							codeInspectionBuilder.withUnitTestCoverage(Double.valueOf("0.0"));
						}

						CodeInspecion codeInspecion = codeInspectionBuilder.build();

						LOG.info(codeInspecion);

						String comunicador = new ComunicadorAPIRSI().chamadaCodeInspection(codeInspecion, false);

						if (comunicador.isEmpty()) {
							System.err.println("-----------------------------");
							System.err.println("-----------------------------");
							System.err.println("----Comunicador Falhou!------");
							System.err.println("-----------------------------");
							System.err.println("-----------------------------");
						} else {
							System.out.println("-----------------------------");
							System.out.println("-----------------------------");
							System.out.println(comunicador.toString());
							System.out.println("-----------------------------");
							System.out.println("-----------------------------");

						}

					}

				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	/**
	 * Executa o Runnable que cria a estrutura de execução do Sonar para o
	 * ControleGitHK
	 */
	public void validarModulos() {
		try {
			new Thread(validaModulos).start();
		} catch (Exception e) {
			LOG.error("Erro ao validar modulos", e);
		}
	}

	/**
	 * Cria a estrutura de execução do Sonar para o ControleGitHK
	 */
	private static Runnable validaModulos = new Runnable() {

		@Override
		public void run() {
			ControleGitHKDAO controleDAO = new ControleGitHKDAO();
			for (ControleGitHK controle : controleDAO.listar()) {
				ControleGitHKBean.controle = controle;
				criarPastas();
			}
		}
	};

	/**
	 * Metodo para criar as pastas target, o arquivo de propriedades do sonar
	 * 
	 */
	private static void criarPastas() {
		System.out.println(controle.getPacote() + " " + controle.getCaminho());
		StringBuilder caminhoModulo = new StringBuilder(controle.getCaminho());
		StringBuilder caminhoPropertiesSonar = new StringBuilder(
				caminhoModulo.toString() + "\\" + "sonar-project.properties");
		Path diretorio = Paths.get(caminhoPropertiesSonar.toString());
		if (!Files.exists(diretorio)) {
			try {
				Files.createFile(diretorio);

			} catch (Exception e) {
				LOG.error("Erro ao criar sonar.properties", e);
			}
		}
		try (FileInputStream in = new FileInputStream(diretorio.toString());
				FileOutputStream out = new FileOutputStream(diretorio.toString());) {
			// Criando o arquivo .properties necessario para a execução do Sonar
			Properties sonarProject = new Properties();
			Optional<RelacaoProjetoSiglaGestor> painelPesquisado = new RelacaoProjetoSiglaGestorDAO().listar().stream()
					.filter(r -> r.getNome_Projeto().equals(controle.getNomeSistema())).findFirst();
			if (painelPesquisado.isPresent()) {
				String chave = painelPesquisado.get().getChave();
				sonarProject.load(new InputStreamReader(in, Charset.forName("UTF-8")));
				sonarProject.put("sonar.projectKey", chave);
				sonarProject.put("sonar.projectName", controle.getNomeSistema());
				sonarProject.put("sonar.projectVersion", "");
				sonarProject.put("sonar.sources", ".");
				sonarProject.put("sonar.java.binaries", "target/classes");
				sonarProject.put("sonar.exclusions", "**/*test.java,**/*teste.java,**/*.properties");
				sonarProject.put("sonar.cfamily.build-wrapper-output.bypass", "true");
//				sonarProject.put("sonar.sourceEncoding", "ISO-8859-1");
				sonarProject.store(out, "Properties Sonar " + caminhoPropertiesSonar);
			}

		} catch (Exception e) {
			LOG.error("Erro ao escrever no sonar.properties", e);
		}
		// Copiando a pasta target para dentro das pastas dos modulos
		Path targetDentroDoPrograma = new MetodosUteis().caminhoTarget();
		Path caminhoDosModulos = Paths.get(caminhoModulo + "\\target");
		try {
			if (!Files.exists(caminhoDosModulos)) {
				Files.createDirectory(caminhoDosModulos);
				copiarArquivos(targetDentroDoPrograma, caminhoDosModulos);
			}
		} catch (Exception e) {
			LOG.error("Erro ao criar pasta target", e);
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
			// se é um diretório, tentamos criar. se já existir, não tem problema.
			Files.createDirectories(destino);
			try ( // listamos todas as entradas do diretório
					DirectoryStream<Path> entradas = Files.newDirectoryStream(origem);) {

				for (Path entrada : entradas) {
					// para cada entrada, achamos o arquivo equivalente dentro de cada arvore
					Path novaOrigem = origem.resolve(entrada.getFileName());
					Path novoDestino = destino.resolve(entrada.getFileName());

					// invoca o metodo de maneira recursiva
					copiarArquivos(novaOrigem, novoDestino);
				}
			} catch (IOException e) {
				System.out.println("Erro ao copiar a pasta target para " + destino.toAbsolutePath().toString());
			}
		} else {
			// copiamos o arquivo
			Files.copy(origem, destino);
		}
	}

	/**
	 * Atualiza módulos do git e executa o SONAR para módulos com atualização.
	 */

	public void atualizaGitExecutaSonar() {
		try {
			atualizarGit();
			boolean atualizacaoTodosModulosGitConcluida = false;
			Date dataComecouAExecutarGitPull = Date
					.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
			while (!atualizacaoTodosModulosGitConcluida) {
				// Aguarda a conclusão da execução do Git Pull e Git Log dos módulos de
				// Homologação
				atualizacaoTodosModulosGitConcluida = atualizacaoTodosModulosGitConcluida(dataComecouAExecutarGitPull);
			}
			executarSonarModulosAtualizadosAutomatico();
		} catch (Exception e) {
			LOG.error("Erro ao aguardar atualização do Git", e);
		}
	}

	/**
	 * 
	 * Executa o sonar para modulos com atualização de maneira automatica
	 * 
	 */
	public void executarSonarModulosAtualizadosAutomatico() {
		new Thread(runnableExecutaSonarModulosAtualizadosAt).start();
	}

	Runnable runnableExecutaSonarModulosAtualizadosAt = new Runnable() {

		@Override
		public void run() {
			try {
				ControleGitHKDAO controleGitDAO = new ControleGitHKDAO();
				ArrayList<ControleGitHK> listaModulosAtualizados = (ArrayList<ControleGitHK>) controleGitDAO.listar()
						.stream().filter(c -> c.isAlteracao()).collect(Collectors.toList());
				if (listaModulosAtualizados.isEmpty()) {
					LOG.info(LocalDateTime.now() + ": Nenhum modulo Git HK foi atualizado");
				}
				for (ControleGitHK con : listaModulosAtualizados) {
					LOG.info("executarSonar :" + con.getChave() + " Caminho: " + con.getCaminho());
					Executors
							.newFixedThreadPool(3,
									new YourThreadFactory(ControleGitHK.class.getName() + "-" + con.getId()))
							.submit(ExecutarSonarPorModulosBean.sonarScannerAutomaticoDiaria);
				}
			} catch (Exception e) {
				LOG.error("Erro :" + e + " ao tentar executar o Sonar para modulos atualziados", e);
			}
		}
	};

	private boolean atualizacaoTodosModulosGitConcluida(Date dataComecouAExecutarGitPull) {
		ControleGitHKDAO dao = new ControleGitHKDAO();
		List<ControleGitHK> listaGit = dao.listar();
		Date umMinutoAntesDaDataComecouAExecutarGitPull = Date
				.from(LocalDateTime.ofInstant(dataComecouAExecutarGitPull.toInstant(), ZoneId.systemDefault())
						.minusMinutes(1).atZone(ZoneId.systemDefault()).toInstant());

		listaGit = listaGit.stream().filter(modulo -> Files.exists(Paths.get(modulo.getCaminho())))
				.collect(Collectors.toList());
		Date dataUltimoModulo = Date.from(listaGit.get(listaGit.size() - 1).getDataVerificacao().toInstant());

		LocalDateTime horarioComecoExecucao = LocalDateTime
				.ofInstant(umMinutoAntesDaDataComecouAExecutarGitPull.toInstant(), ZoneId.systemDefault());
		LocalDateTime horarioAtualizacaoUltimoModulo = LocalDateTime.ofInstant(dataUltimoModulo.toInstant(),
				ZoneId.systemDefault());
		return dataUltimoModulo == null ? false : horarioComecoExecucao.isBefore(horarioAtualizacaoUltimoModulo);
	}

	// Get e Set
	// ------------------------------------------------------------------------------------------------------------------------------------------------------

	public ControleGitHK getObj() {
		return obj;
	}

	public ControleGitHK getControle() {
		return controle;
	}

	public List<ControleGitHK> getListaControle() {
		return listaControle;
	}

	public void setListaControle(List<ControleGitHK> listaControle) {
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
		CAMINHO = path;
	}

	public String getChaveEMail() {
		return chaveEMail;
	}

	public void setChaveEMail(String chaveEMail) {
		this.chaveEMail = chaveEMail;
	}

	public String getOrigemEMail() {
		return origemEMail;
	}

	public void setOrigemEMail(String origemEMail) {
		this.origemEMail = origemEMail;
	}

	public String getDestinoEMail() {
		return destinoEMail;
	}

	public void setDestinoEMail(String destinoEMail) {
		this.destinoEMail = destinoEMail;
	}

	public static Runnable getCaptura() {
		return captura;
	}

}