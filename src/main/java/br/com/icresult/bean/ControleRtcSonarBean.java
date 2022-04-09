package br.com.icresult.bean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
import br.com.icresult.dao.complementos.ControleRtcSonarDAO;
import br.com.icresult.dao.complementos.RelacaoProjetoSiglaGestorDAO;
import br.com.icresult.domain.complementos.Analise_Sonar;
import br.com.icresult.domain.complementos.ControleRtcSonar;
import br.com.icresult.domain.complementos.RelacaoProjetoSiglaGestor;
import br.com.icresult.model.SonarAtributos;
import br.com.icresult.util.MetodosUteis;
import br.com.icresult.util.YourThreadFactory;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.common.Logger;

/**
 * 
 * -Classe BEAN ControleRtcSonarBean.
 * 
 * @author helio.franca
 * @version v2.0.5
 * @since 25-07-2018
 *
 *
 */

@SuppressWarnings("serial")
@ManagedBean
@SessionScoped
public class ControleRtcSonarBean implements Serializable {

	private static ControleRtcSonar controle;
	private ControleRtcSonarDAO dao;
	private List<ControleRtcSonar> listaControle;
	private ControleRtcSonar obj;
	private String path;
	private int total;
	static String CAMINHO = "";
	private static Logger LOG = Logger.getLogger(ControleRtcSonarBean.class);
	private String valorEntregaTexto;

	/**
	 * @return - retorna uma lista de ControleRtcSonar que foram selecionadas
	 */
	public List<ControleRtcSonar> paineisSelecionados() {
		return new ControleRtcSonarDAO().listar().stream().filter(r -> r.getSelecionado()).collect(Collectors.toList());
	}

	/**
	 * @return - retorna uma lista de ControleRtcSonar que não foram selecionadas
	 */
	public List<ControleRtcSonar> paineisNaoSelecionados() {
		return new ControleRtcSonarDAO().listar().stream().filter(r -> !r.getSelecionado())
				.collect(Collectors.toList());
	}

	/**
	 * Seleciona todos os modulos
	 */
	public void selecionarTodosModulos() {
		ControleRtcSonarDAO controleDAO = new ControleRtcSonarDAO();
		for (ControleRtcSonar controle : paineisNaoSelecionados()) {
			controle.setSelecionado(true);
			controleDAO.editar(controle);
		}
		listarInfos();
	}

	/**
	 * limpa a seleção de todos os modulos
	 */
	public void limparSelecaoTodosModulos() {
		ControleRtcSonarDAO controleDAO = new ControleRtcSonarDAO();
		for (ControleRtcSonar controle : paineisSelecionados()) {
			controle.setSelecionado(false);
			controleDAO.editar(controle);
		}
		listarInfos();
	}

	/**
	 * 
	 * Metodo que valida a chave do Sonar para as siglas do RTC e chama o metodo
	 * para validar as pastas de execução do Sonar para as siglas
	 * 
	 */
	public void validarChave() {
		String painel = new String();
		try {
			dao = new ControleRtcSonarDAO();
			RelacaoProjetoSiglaGestorDAO relacaoDao = new RelacaoProjetoSiglaGestorDAO();
			List<RelacaoProjetoSiglaGestor> listaProjetos = relacaoDao.listar();
			for (ControleRtcSonar controle : dao.listar()) {
				painel = controle.getNomeSistema();
				List<RelacaoProjetoSiglaGestor> listaModulos = listaProjetos.stream()
						.filter(p -> p.getNome_Projeto().equals(controle.getNomeSistema()))
						.collect(Collectors.toList());
				if (!listaModulos.isEmpty()) {
					String chave = listaModulos.get(0).getChave();
					controle.setChave(chave);
					dao.editar(controle);
				}
			}
		} catch (Exception e) {
			LOG.debug("Erro nesta sigla: " + painel, e);
			LOG.error("Erro ao Validar Chaves", e);
		}
	}

	/**
	 * Executa o Runnable que cria a estrutura de execução do Sonar
	 * 
	 */
	public void validarModulos() {
		try {
			new MetodosUteis().copiaArquivosNecessarios();
			new Thread(validaModulos).start();
		} catch (Exception e) {
			LOG.error("Erro ao validar modulos", e);
		}
	}

	/**
	 * Runnable que cria a estrutura de execução do Sonar
	 */
	private static Runnable validaModulos = new Runnable() {

		@Override
		public void run() {
			ControleRtcSonarDAO controleDAO = new ControleRtcSonarDAO();
			for (ControleRtcSonar controle : controleDAO.listar()) {
				ControleRtcSonarBean.controle = controle;
				criarPastas();
			}
		}
	};

	/**
	 * Criar as pastas target, o arquivo de propriedades do sonar
	 */
	private static void criarPastas() {
		System.out.println(controle.getChave() + " " + controle.getCaminho());
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
		try (FileOutputStream out = new FileOutputStream(diretorio.toString());
				FileInputStream in = new FileInputStream(diretorio.toString());) {

			// Criando o arquivo .properties necessario para a execução do Sonar
			Properties sonarProject = new Properties();

			sonarProject.load(new InputStreamReader(in, Charset.forName("UTF-8")));
			sonarProject.put("sonar.projectKey", controle.getChave());
			sonarProject.put("sonar.projectName", controle.getNomeSistema());
			sonarProject.put("sonar.projectVersion", "");
			sonarProject.put("sonar.sources", ".");
			sonarProject.put("sonar.java.binaries", "target/classes");
			sonarProject.put("sonar.exclusions", "**/*test.java,**/*teste.java,**/*.properties");
			sonarProject.put("sonar.cfamily.build-wrapper-output.bypass", "true");
			sonarProject.put("sonar.sourceEncoding", "ISO-8859-1");
			sonarProject.store(out, "Properties Sonar " + caminhoPropertiesSonar);

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
			LOG.error("Erro ao criar a pasta target", e);
		}

		// Copiando os arquivos do LocalizaEDestroi_Cli para dentro da pasta do modulo
		Path arquivosLocalizaEDestroi = new MetodosUteis().caminhoLocalizaEDestroi();

		try {
			copiarArquivos(arquivosLocalizaEDestroi, Paths.get(caminhoModulo.toString()));
		} catch (Exception e) {
			LOG.error("Erro ao copiar arquivos do localizaEDestroi_CLI", e);
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
					// para cada entrada, achamos o arquivo equivalente dentro
					// de cada arvore
					Path novaOrigem = origem.resolve(entrada.getFileName());
					Path novoDestino = destino.resolve(entrada.getFileName());

					// invoca o metodo de maneira recursiva
					copiarArquivos(novaOrigem, novoDestino);
				}

			} catch (Exception e) {
				LOG.error("Erro ao copiar pasta target para " + destino, e);
			}

		} else {
			// copiamos o arquivo
			Files.copy(origem, destino);
		}
	}
	// --------------------------------------//

	/**
	 * 
	 * Metodo que dispara a captura do Sonar para uma Sigla do RTC especifico
	 * 
	 */
	public void capturar() {
		try {
			Executors.newFixedThreadPool(1, new YourThreadFactory(obj.getCodigo().toString() + "-" + valorEntregaTexto))
					.submit(captura);
		} catch (Exception e) {
			LOG.error("Erro ao Capturar", e);
		}
	}

	/**
	 * 
	 * Runnable responsavel por capturar os paineis para a entrega Mensal
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
				ControleRtcSonarDAO daoTemp = new ControleRtcSonarDAO();
				ControleRtcSonar tempObj = daoTemp.buscar(id);
				SonarAPIBean sonar = new SonarAPIBean();
				Analise_SonarDAO analiseDAO = new Analise_SonarDAO();
				SonarAtributos captura = sonar.getSonarApi(tempObj.getChave());
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

	/***
	 * 
	 * Metodo que dispara a execução do Sonar para uma Sigla do RTC especifico
	 * 
	 */

	public void executarSonar() {
		Executors
				.newFixedThreadPool(1,
						new YourThreadFactory(
								ControleRtcSonar.class.getName() + "-" + obj.getCodigo() + "-" + valorEntregaTexto))
				.submit(ExecutarSonarPorModulosBean.sonarScannerAutomaticoInspecaoGenerica);
	}

	/**
	 * Seleciona um objeto ControleRtcSonar da tabela
	 */
	public void selecionarObj(ActionEvent evento) {

		try {
			obj = (ControleRtcSonar) evento.getComponent().getAttributes().get("meuSelect");
			LOG.info("SIGLA:" + obj.getSigla());

		} catch (Exception e) {
			LOG.error("Erro ao selecionar", e);
		}
	}

	/***
	 * 
	 * Coloca um objeto do tipo ControleRtcSonar com selecionado
	 * 
	 */

	public void selecionarModulo(ActionEvent evento) {
		try {
			obj = (ControleRtcSonar) evento.getComponent().getAttributes().get("meuSelect");
			LOG.debug("ID: " + obj.getChave());
			obj.setSelecionado(obj.getSelecionado() ? false : true);
			new ControleRtcSonarDAO().editar(obj);
		} catch (Exception e) {
			LOG.error("Erro ao Selecionar: ", e);
		}
	}

	/**
	 * Salva objeto do tipo ControleRtcSonar
	 */
	// -------------------------------------------------------------------------------------
	private void salvar() {
		try {
			dao.salvar(controle);
		} catch (Exception e) {
			LOG.error("Não foi possível salvar", e);
		}
	}

	/**
	 * Lista os objetos do tipo ControleRtcSonar
	 */
	// ------------------------------------------------------------------------------------------------------------------------------------------------------
	public void listarInfos() {
		try {
			dao = new ControleRtcSonarDAO();
			listaControle = dao.listar();
			total = listaControle.size();
			LOG.info("Lista Atualizada!");

		} catch (Exception e) {
			LOG.error("Erro ao atualizar lista!", e);
		}
	}

	/**
	 * Captura as informações de uma planilha xls e salva no banco de dados
	 * 
	 */
	// -------------------------------------------------------------------------------------------
	public void salvarPlanilha(String caminhoArquivo) {
		controle = new ControleRtcSonar();
		dao = new ControleRtcSonarDAO();
		String sigla, sistema, caminho;
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
				Cell celula3 = sheet.getCell(2, i); // coluna 3 - caminho

				sigla = celula1.getContents().toString().trim().toUpperCase();
				sistema = celula2.getContents().toString().trim().toUpperCase();

				caminho = celula3.getContents().toString().trim().toUpperCase();

				// Encerra a leitura quando encontra linha vazia
				if (sigla.isEmpty()) {
					break;
				}

				if (!sigla.isEmpty()) {
					dateC = Date.from(LocalDateTime.now().atZone(ZoneId.of("Etc/GMT+3")).toInstant());
					controle.setSigla(sigla);
					controle.setNomeSistema(sistema);
					controle.setCaminho(caminho);
					controle.setSelecionado(true);
					controle.setNomeArquivo(CAMINHO);
					controle.setDataVerificacao(dateC);
					controle.setDataCommitAnt(null);

					salvar();
				}
			}
			validarChave();
			Messages.addGlobalInfo("Planilha salva com sucesso!");
		} catch (Exception e) {
			Messages.addGlobalError("Não foi possível salvar Planilha ");
		}
	}

	/**
	 * Limpa as informações da tabela ControleRtcSonar no banco de dados
	 */
	// -------------------------------------------------------------------------------------
	public void limparDB() {
		try {
			listarInfos();
			for (ControleRtcSonar ControleRtcSonar : listaControle) {
				ControleRtcSonar entidade = dao.buscar(ControleRtcSonar.getCodigo());
				dao.excluir(entidade);
			}
		} catch (Exception e) {
			LOG.error("Não foi possível limparDB", e);
		}
	}

	/**
	 * Chama o Runnable do Log RTC
	 */
	// ------------------------------------------------------------------------------------------------------------------------------------------------------
	public void gerarLogRTC() {
		try {
			new Thread(rtcLog).start();
			LOG.info("RTC Log em Execução");
		} catch (Exception e) {
			LOG.error("Erro ao executar RTC Log!", e);
		}
	}

	/**
	 * Runnable para acionar o comando RTClog e capturar as informações.
	 */
	// -------------------------------------------------------------------------------------
	private static Runnable rtcLog = new Runnable() {
		public void run() {
			List<ControleRtcSonar> listaControle;
			ControleRtcSonarDAO dao = new ControleRtcSonarDAO();
			listaControle = dao.listar();

			for (ControleRtcSonar obj : listaControle) {
				try {
					lerLogRtc(obj);
				} catch (Exception e) {
					LOG.error("Erro rtcLog", e);
				}
			}
		}
	};

	/**
	 * Faz a leitura de um arquivo txt e trata as informações
	 * 
	 * @param obj - Objeto ControleRtcSonar
	 */
	// -------------------------------------------------------------------------------------
	public static void lerLogRtc(ControleRtcSonar obj) {
		String sigla = obj.getSigla();
		String path = obj.getCaminho();
		StringBuilder log = new StringBuilder();
		obj.setDataVerificacao(Date.from(LocalDateTime.now().atZone(ZoneId.of("Etc/GMT+3")).toInstant()));
		path = path + "//Log_" + sigla + ".txt";
		File file = new File(path);
		String siglaTemp, commitTemp, dataTemp = "01/01/1900";

		try (FileReader fileReader = new FileReader(file);
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(new FileInputStream(file.getAbsolutePath()), "UTF-8"));) {

			String info = null;
			int linha = 0;
			while ((info = reader.readLine()) != null) {
				linha++;
				log.append("\n");
				log.append(info);

				if (linha == 1) {
					siglaTemp = info;
					String array[] = new String[2];
					array = siglaTemp.split(":");
					siglaTemp = array[1].trim();

				}
				if (linha == 2) {
					commitTemp = info;
					String array[] = new String[2];
					array = commitTemp.split(":");
					commitTemp = array[1].trim();
					if (commitTemp.equalsIgnoreCase("True")) {
						obj.setAlteracao(true);
					} else {
						obj.setAlteracao(false);
					}

				}
				if (linha == 3) {
					dataTemp = info;
					String array[] = new String[2];
					array = dataTemp.split(":");
					dataTemp = array[1].trim();
					Date dataCommit = validadorData(dataTemp, "");
					if (dataCommit == null) {
						LOG.info("Data Nula");
					} else {
						LOG.info("Achou Data :" + obj.getSigla());
						if (obj.getDataCommit() == null) {
							obj.setAlteracao(false);
						} else {
							LocalDate dataLocalizada = dataCommit.toInstant().atZone(ZoneId.of("Etc/GMT+3"))
									.toLocalDate();
							LocalDate dataArmazenada = obj.getDataCommit().toInstant().atZone(ZoneId.of("Etc/GMT+3"))
									.toLocalDate();
							if (dataArmazenada.equals(dataLocalizada)) {
								obj.setAlteracao(false);
							} else {
								obj.setAlteracao(true);
								obj.setDataCommitAnt(java.sql.Date.valueOf(dataArmazenada));
							}
						}
						obj.setDataCommit(dataCommit);
					}

				}

			}

			info = reader.readLine();
			obj.setDescricaoLog(log.toString());

		} catch (Exception e) {
			LOG.error("Erro ao executar git log", e);
		} finally {
			new ControleRtcSonarDAO().editar(obj);
		}
	}

	/**
	 * Valida e converte uma objeto do tipo data
	 * 
	 * @param dataInfo - data para validara
	 * @param msg      - mensagem opcional
	 * @return - retorna um objeto do tipo data
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
				LOG.error("Erro em data " + msg, e);
			}
		}
		return dataFinal;
	}

	/**
	 * 
	 * Metodo para capturar todas as siglas atualizadas
	 * 
	 */

	public void executarCapturaTodasSiglasAtualizadas() {
		try {
			Executors.newFixedThreadPool(1, new YourThreadFactory(valorEntregaTexto))
					.submit(runnableExecutaCapturaTodasSiglasAtualizadas);
		} catch (Exception e) {
			LOG.error("Erro ao tentar captura todas as sigla atualizadas", e);
		}
	}

	Runnable runnableExecutaCapturaTodasSiglasAtualizadas = new Runnable() {

		@Override
		public void run() {
			String valorEntregaTexto = Thread.currentThread().getName();
			ControleRtcSonarDAO controleRtcDAO = new ControleRtcSonarDAO();
			List<ControleRtcSonar> listaSiglaAtualizadas = controleRtcDAO.listar().stream().filter(c -> c.isAlteracao())
					.collect(Collectors.toList());
			for (ControleRtcSonar con : listaSiglaAtualizadas) {
				try {
					LOG.info(con.getChave());
					Executors
							.newFixedThreadPool(10,
									new YourThreadFactory(con.getCodigo().toString() + "-" + valorEntregaTexto))
							.submit(captura);
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};

	/**
	 * 
	 * Metodo para capturar as siglas que foram selecionadas
	 * 
	 */
	public void executarCapturaPorSiglas(ActionEvent evento) {
		try {
			Executors.newFixedThreadPool(1, new YourThreadFactory(valorEntregaTexto))
					.submit(runnableExecutarCapturaPorSiglas);
		} catch (Exception e) {
			LOG.info("Erro ao executar captura", e);
		}

	}

	Runnable runnableExecutarCapturaPorSiglas = new Runnable() {

		@Override
		public void run() {
			try {
				String valorEntregaTexto = Thread.currentThread().getName();
				List<ControleRtcSonar> listaModulosSelecionados = new ControleRtcSonarDAO().listar().stream()
						.filter(r -> r.getSelecionado()).collect(Collectors.toList());
				for (ControleRtcSonar relacao : listaModulosSelecionados) {
					System.out.println(relacao.getChave());
					Executors
							.newFixedThreadPool(10,
									new YourThreadFactory(relacao.getCodigo().toString() + "-" + valorEntregaTexto))
							.submit(captura);
					Thread.sleep(5000);
				}
			} catch (Exception e) {
				LOG.error("Erro ao capturar", e);
			}
		}
	};

	/**
	 * 
	 * Executar o Sonar para todas as siglas atualizadas
	 * 
	 */

	public void executarTodasSiglasAtualizadas() {

		try {
			Executors.newFixedThreadPool(1, new YourThreadFactory(valorEntregaTexto))
					.submit(runnableExecutaTodasSiglasAtualizadas);
		} catch (Exception e) {
			LOG.error("Erro ao executar o Sonar para as siglas atualizadas", e);
		}

	}

	Runnable runnableExecutaTodasSiglasAtualizadas = new Runnable() {
		public void run() {
			try {
				String valorEntregaTexto = Thread.currentThread().getName();
				List<ControleRtcSonar> listaModulosSelecionados = new ControleRtcSonarDAO().listar().stream()
						.filter(c -> c.isAlteracao()).collect(Collectors.toList());

				String nomeDaClasseComTraco = ControleRtcSonar.class.getName() + "-";

				for (ControleRtcSonar controle : listaModulosSelecionados) {

					Executors
							.newFixedThreadPool(5,
									new YourThreadFactory(
											nomeDaClasseComTraco + controle.getId() + "-" + valorEntregaTexto))
							.submit(ExecutarSonarPorModulosBean.sonarScannerAutomaticoInspecaoGenerica);
					Thread.sleep(5000);

				}
			} catch (Exception e) {
				LOG.error("Erro ao executar o sonar", e);
			}
		}
	};

	/**
	 * 
	 * Executar o Sonar para as Siglas que foram selecionadas
	 * 
	 */
	public void executarSonarPorSiglasSelecionadas(ActionEvent evento) {

		try {
			Executors.newFixedThreadPool(1, new YourThreadFactory(valorEntregaTexto))
					.submit(runnableExecutarSonarPorSiglasSelecionadas);
		} catch (Exception e) {
			LOG.error("Erro ao executar sonar", e);
		}
	}

	Runnable runnableExecutarSonarPorSiglasSelecionadas = new Runnable() {

		@Override
		public void run() {
			String valorEntregaTexto = Thread.currentThread().getName();
			List<ControleRtcSonar> listaModulosSelecionados = new ControleRtcSonarDAO().listar().stream()
					.filter(r -> r.getSelecionado()).collect(Collectors.toList());
			for (ControleRtcSonar controle : listaModulosSelecionados) {

				Executors
						.newFixedThreadPool(1,
								new YourThreadFactory(ControleRtcSonar.class.getName() + "-" + controle.getCodigo()
										+ "-" + valorEntregaTexto))
						.submit(ExecutarSonarPorModulosBean.sonarScannerAutomaticoInspecaoGenerica);

			}
		}
	};

	// Get e Set
	// ------------------------------------------------------------------------------------------------------------------------------------------------------

	public ControleRtcSonar getobj() {
		return obj;
	}

	public ControleRtcSonar getControle() {
		return controle;
	}

	public List<ControleRtcSonar> getListaControle() {
		return listaControle;
	}

	public void setListaControle(List<ControleRtcSonar> listaControle) {
		this.listaControle = listaControle;
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

	public static void setCAMINHO(String caminho) {
		CAMINHO = caminho;
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

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	// ------------------------------------------------------------------------------------------------------------------------------------------------------

	public StreamedContent getExemploCarga() {
		return new MetodosUteis().getExcelComoStreamedContent("/resources/exemplos_excel/exemplo_carga_rtc.xls",
				"exemplo_carga_rtc.xls");
	}
}