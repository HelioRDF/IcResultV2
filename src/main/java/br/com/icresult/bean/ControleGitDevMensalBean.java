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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.omnifaces.util.Messages;
import org.primefaces.model.StreamedContent;

import br.com.icresult.dao.complementos.ConfigGitDAO;
import br.com.icresult.dao.complementos.ControleGitDevMensalDAO;
import br.com.icresult.dao.complementos.SiglasGitDAO;
import br.com.icresult.domain.complementos.ConfigGit;
import br.com.icresult.domain.complementos.ControleGitDevMensal;
import br.com.icresult.domain.complementos.SiglasGit;
import br.com.icresult.nomeprojeto.Ambiente;
import br.com.icresult.nomeprojeto.ProjectName;
import br.com.icresult.nomeprojeto.Repositorio;
import br.com.icresult.util.MetodosUteis;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.common.Logger;

/**
 * -Classe BEAN ControleGitDevMensalMensalBean.
 * 
 * @author
 *
 */

@SuppressWarnings("serial")
@ManagedBean
@SessionScoped
public class ControleGitDevMensalBean implements Serializable {
	static final Runtime run = Runtime.getRuntime();
	static Process pro;
	static BufferedReader read;
	private static ControleGitDevMensal controle;
	private ControleGitDevMensalDAO dao;
	private List<ControleGitDevMensal> listaControle;
	private String pathSigla;
	private String path;
	private int total;
	static String CAMINHO = "";
	private boolean selecao;
	private static Logger logDaClasse = Logger.getLogger(ControleGitDevMensalBean.class);

	/**
	 * // Salvar um objeto do tipo ControleGitDevMensal
	 */
	private void salvar() {
		try {
			dao.salvar(controle);
		} catch (Exception e) {
			logDaClasse.error("Não foi possível salvar o objeto", e);
		}
	}

	/**
	 * Lista os objetos do tipo ControleGitDevMensal
	 */
	public void listarInfos() {
		try {
			dao = new ControleGitDevMensalDAO();
			listaControle = dao.listar();
//			listaControle.sort(ControleGitDevMensal.getComparadorPorAlteracao());
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
		setControle(new ControleGitDevMensal());
		dao = new ControleGitDevMensalDAO();
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
					salvar();
				}
			}

			limparDBSiglasGit();
			Set<SiglasGit> listaSiglasDoGit = dao.listar().stream()
					.map(modulo -> new SiglasGit(modulo.getSigla(), modulo.getNomeSistema())).distinct()
					.collect(Collectors.toSet());
			Set<String> listaNomeSigla = new HashSet<String>();
			for (SiglasGit siglaGit : listaSiglasDoGit) {
				if (!listaNomeSigla.contains(siglaGit.getSigla())) {
					siglaGit.setChave(new ProjectName(siglaGit.getSigla(), siglaGit.getNome_Projeto(), Repositorio.GIT,
							Ambiente.DESENVOLVIMENTO).getPadraoNomeProjeto());
					List<ControleGitDevMensal> listaControle = dao.listar().stream()
							.filter(g -> g.getSigla().equals(siglaGit.getSigla())).collect(Collectors.toList());
					if (!listaControle.isEmpty()) {
						siglaGit.setSelecionado("ui-icon-blank");
						String caminhoSigla = listaControle.stream()
								.filter(e -> e.getNomeSistema().equals(siglaGit.getNome_Projeto()))
								.collect(Collectors.toList()).get(0).getCaminho();
						int ultimaBarra = caminhoSigla.lastIndexOf("\\");
						siglaGit.setCaminho(caminhoSigla.substring(0, ultimaBarra));
						siglaGit.setNomeProjetoPadronizado(new ProjectName(siglaGit.getSigla(),
								siglaGit.getNome_Projeto(), Repositorio.GIT, Ambiente.DESENVOLVIMENTO));
						System.out.println(siglaGit);
						salvarSiglasGit(siglaGit);
					}
					listaNomeSigla.add(siglaGit.getSigla());
				}
			}

			Messages.addGlobalInfo("Planilha salva com sucesso!");
			gerarLogGit();
		} catch (Exception e) {
			logDaClasse.error("Erro ao salvar planilha", e);
			Messages.addGlobalError("Não foi possível salvar (salvarPlanilha())");
		}
	}

	/**
	 * Cria uma sigla do git de acordo com os móduloa carregados na planilha
	 * 
	 * @param siglaGit - Sigla do git a ser salva.
	 */
	private void salvarSiglasGit(SiglasGit siglaGit) {
		SiglasGitDAO dao = new SiglasGitDAO();
		dao.salvar(siglaGit);
	}

	/**
	 * Limpa a tabela de siglas do git.
	 * 
	 */
	private void limparDBSiglasGit() {
		SiglasGitDAO dao = new SiglasGitDAO();
		for (SiglasGit s : dao.listar()) {
			dao.excluir(s);
		}
	}

	/**
	 * Limpa a tabela de módulos do git.
	 */
	public void limparDB() {
		try {
			listarInfos();
			for (ControleGitDevMensal controleGit : listaControle) {
				ControleGitDevMensal entidade = dao.buscar(controleGit.getCodigo());
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

			ControleGitDevMensalDAO controleGitDao = new ControleGitDevMensalDAO();
			List<ControleGitDevMensal> listaModulosGit = controleGitDao.listar().stream()
					.filter(modulo -> Files.exists(Paths.get(modulo.getCaminho()))).collect(Collectors.toList());

			String erro;

			for (ControleGitDevMensal ControleGitDevMensal : listaModulosGit) {
				erro = "";
				ControleGitDevMensal entidade = controleGitDao.buscar(ControleGitDevMensal.getCodigo());
				String pathSigla = "cd " + entidade.getCaminho();
				String comandoGit = "git log --stat -1 --date=format:%d/%m/%Y";
				String[] cmds = { pathSigla, comandoGit };
				StringBuilder log = new StringBuilder();
				log.append("\n \n");
				String author = "", dataCommit = "", descricaoLog;
				Date dataVerificacao = Date.from(LocalDateTime.now().atZone(ZoneId.of("Etc/GMT+3")).toInstant());
				Boolean fatalErro = new Boolean(false);
				ControleGitDevMensal.setDataVerificacao(dataVerificacao);

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
							ControleGitDevMensal.setErroGitPull(erro);
							ControleGitDevMensal.setDataCommit(null);
							ControleGitDevMensal.setDataCommitAnt(null);
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

					ControleGitDevMensal.setAuthor(author);
					if (ControleGitDevMensal.getDataCommit() != null) {
						ControleGitDevMensal.setDataCommitAnt(ControleGitDevMensal.getDataCommit());
					}
					ControleGitDevMensal
							.setDataCommit(ControleGitDevMensalBean.validadorData(dataCommit, "Data Commit"));
					Date dataAtual = ControleGitDevMensal.getDataCommit();
					Date dataAnterior = ControleGitDevMensal.getDataCommitAnt();
					dataAnterior = formatadorData(dataAnterior);

					if (dataAtual.equals(dataAnterior)) {
						ControleGitDevMensal.setAlteracao(false);
					} else {
						ControleGitDevMensal.setAlteracao(true);
					}

					dataVerificacao = Date.from(LocalDateTime.now().atZone(ZoneId.of("Etc/GMT+3")).toInstant());
					ControleGitDevMensal.setDescricaoLog(descricaoLog);
				} catch (Exception e) {
					author = "----------";
					ControleGitDevMensal.setAuthor(author);
					descricaoLog = "null";
					ControleGitDevMensal.setDescricaoLog(descricaoLog);
				} finally {
					System.out.println(erro);
					log = new StringBuilder();
					if (!fatalErro) {
						ControleGitDevMensal.setErroGitPull("");
					}
					controleGitDao.editar(ControleGitDevMensal);

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
		return ControleGitDevMensalBean.validadorData(dataString, "Data Anterior");
	}

	/**
	 * Runnable para acionar o gitpull e atualizar os pacotes das aplicações
	 */
	private final static Runnable gitPull = new Runnable() {
		public void run() {
			List<ControleGitDevMensal> listaControle;
			ControleGitDevMensalDAO dao = new ControleGitDevMensalDAO();
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
			List<ControleGitDevMensal> listaControle;
			ControleGitDevMensalDAO dao = new ControleGitDevMensalDAO();
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
	 *                      ControleGitDevMensal pois esses objetos contem as
	 *                      informações necessárias para executar a atualização dos
	 *                      pacotes do git
	 * 
	 * @author andre.graca
	 */

	public static void executaComandoGitPull(List<ControleGitDevMensal> listaControle) {

		ControleGitDevMensalDAO dao = new ControleGitDevMensalDAO();
		for (ControleGitDevMensal obj : listaControle) {
			ControleGitDevMensal entidade = dao.buscar(obj.getCodigo());
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
	 *                      ControleGitDevMensal pois esses objetos contem as
	 *                      informações necessárias para executar a atualização dos
	 *                      pacotes do git
	 * 
	 * @author andre.graca
	 */

	public static void executaComandoGitClone(List<ControleGitDevMensal> listaControle) {

		ControleGitDevMensalDAO dao = new ControleGitDevMensalDAO();

		for (ControleGitDevMensal obj : listaControle) {
			ControleGitDevMensal entidade = dao.buscar(obj.getCodigo());
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

	// Get e Set
	// ------------------------------------------------------------------------------------------------------------------------------------------------------

	public List<ControleGitDevMensal> getListaControle() {
		return listaControle;
	}

	public void setListaControle(List<ControleGitDevMensal> listaControle) {
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

	public boolean isSelecao() {
		return selecao;
	}

	public void setSelecao(boolean selecao) {
		this.selecao = selecao;
	}

	public static void setControle(ControleGitDevMensal controle) {
		ControleGitDevMensalBean.controle = controle;
	}

	// ------------------------------------------------------------------------------------------------------------------------------------------------------

	public StreamedContent getExemploModulosGit() {
		return new MetodosUteis().getExcelComoStreamedContent("/resources/exemplos_excel/exemplo_moduloGit.xls",
				"exemplo_modulosGit.xls");
	}
}
