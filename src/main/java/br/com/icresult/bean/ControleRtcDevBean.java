package br.com.icresult.bean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.omnifaces.util.Faces;
import org.omnifaces.util.Messages;
import org.primefaces.PrimeFaces;
import org.primefaces.model.StreamedContent;

import br.com.icresult.dao.complementos.AnaliseDevDAO;
import br.com.icresult.dao.complementos.Analise_DiarioDAO;
import br.com.icresult.dao.complementos.ControleRtcDevDAO;
import br.com.icresult.dao.complementos.RelacaoProjetoSiglaGestorDAO;
import br.com.icresult.domain.complementos.Analise_Dev_Diario;
import br.com.icresult.domain.complementos.Analise_Dev_Mensal;
import br.com.icresult.domain.complementos.ControleRtcDev;
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
 * -Classe BEAN ControleRtcDevBean.
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
public class ControleRtcDevBean implements Serializable {

	private static ControleRtcDev controle;
	private ControleRtcDevDAO dao;
	private List<ControleRtcDev> listaControle;
	private ControleRtcDev obj;
	private boolean eCapturaMensal;
	private int total;
	static String CAMINHO = "";
	private static Logger LOG = Logger.getLogger(ControleRtcDevBean.class);

	/**
	 * Seleciona se um modulo será capturado na tabela da Inspeção Diária ou da
	 * Inspeção Mensal
	 * 
	 */
	public void editaTipoCaptura() {
		eCapturaMensal = !eCapturaMensal;
		String tipo = eCapturaMensal ? "mensal" : "diária";
		LOG.debug(tipo);
	}

	/**
	 * @return - retorna uma lista de ControleRtcDev que foram selecionadas
	 */
	public List<ControleRtcDev> paineisSelecionados() {
		return new ControleRtcDevDAO().listar().stream().filter(r -> r.getSelecionado().equals("ui-icon-check"))
				.collect(Collectors.toList());
	}

	/**
	 * @return - retorna uma lista de ControleRtcDev que não foram selecionadas
	 */
	public List<ControleRtcDev> paineisNaoSelecionados() {
		return new ControleRtcDevDAO().listar().stream().filter(r -> r.getSelecionado().equals("ui-icon-blank"))
				.collect(Collectors.toList());
	}

	/**
	 * Seleciona todos os modulos
	 */
	public void selecionarTodosModulos() {
		ControleRtcDevDAO controleDAO = new ControleRtcDevDAO();
		for (ControleRtcDev controle : paineisNaoSelecionados()) {
			controle.setSelecionado("ui-icon-check");
			controleDAO.editar(controle);
		}
		listarInfos();
	}

	/**
	 * limpa a seleção de todos os modulos
	 */
	public void limparSelecaoTodosModulos() {
		ControleRtcDevDAO controleDAO = new ControleRtcDevDAO();
		for (ControleRtcDev controle : paineisSelecionados()) {
			controle.setSelecionado("ui-icon-blank");
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
			dao = new ControleRtcDevDAO();
			RelacaoProjetoSiglaGestorDAO relacaoDao = new RelacaoProjetoSiglaGestorDAO();
			List<RelacaoProjetoSiglaGestor> listaProjetos = relacaoDao.listar();
			for (ControleRtcDev controle : dao.listar()) {
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
			ControleRtcDevDAO controleDAO = new ControleRtcDevDAO();
			for (ControleRtcDev controle : controleDAO.listar()) {
				ControleRtcDevBean.controle = controle;
				criarPastas();
			}
		}
	};

	/**
	 * Criar as pastas target, o arquivo de propriedades do sonar
	 */
	private static void criarPastas() {
		System.out.println(controle.getChave() + " " + controle.getCaminho());
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
			System.out.println(eCapturaMensal ? "Mensal" : "Diária");
			if (eCapturaMensal) {
				Executors.newFixedThreadPool(1, new YourThreadFactory(obj.getCodigo().toString()))
						.submit(capturaMensal);
			} else {
				Executors.newFixedThreadPool(1, new YourThreadFactory(obj.getCodigo().toString()))
						.submit(capturaDiaria);
			}

		} catch (Exception e) {
			LOG.error("Erro ao Capturar", e);
		}
	}

	/**
	 * 
	 * Runnable responsavel por capturar os paineis para a entrega Mensal
	 * 
	 */
	private static Runnable capturaMensal = new Runnable() {

		public void run() {
			try {
				String key = Thread.currentThread().getName();
				System.out.println("\n-----------------------\nID identificado: " + key);
				ControleRtcDev aux = new ControleRtcDevDAO().buscar(new Long(key));
				String chave = aux.getChave();
				String nomeprojetoPadronizado = aux.getNomeProjetoPadronizado();
				AnaliseDevDAO analiseDAO = new AnaliseDevDAO();
				System.out.println("\n\n\n\n------------chave:--------\n" + chave);
				ColetaInformacoesSonarBean sonar = new ColetaInformacoesSonarBean();
				Captura captura = sonar.getSonarApi(chave);
				String repositorio = Repositorio.RTC.getRepositorio();
				String ambiente = Ambiente.DESENVOLVIMENTO.getAmbiente();
				if (captura != null) {
					Analise_Dev_Mensal analise = new Analise_Dev_Mensal(captura);
					analise.setSigla(aux.getSigla());
					analise.setNomeProjeto(aux.getNomePainel());
					analise.setAmbiente(ambiente);
					analise.setRepositorio(repositorio);
					analise.setPadraoNomeSonar(aux.getNomeProjetoPadronizado());
					analiseDAO.salvar(analise);
					System.out.println(analise);
				} else {
					LOG.info("Captura nula, pesquisando a ultima inspeção para esta chave");
					analiseDAO.buscaUltimaInspecaoComNomeProjeto(aux.getNomePainel(), nomeprojetoPadronizado, repositorio, ambiente);
				}

			} catch (Exception e) {
				LOG.error("Erro ao executar captura mensal", e);
			}
		}
	};

	/**
	 * 
	 * Runnable responsavel por capturar os paineis para a entrega Diária
	 * 
	 */

	private static Runnable capturaDiaria = new Runnable() {

		public void run() {
			try {
				String key = Thread.currentThread().getName();
				LOG.debug("ID identificado: " + key);
				ControleRtcDev aux = new ControleRtcDevDAO().buscar(new Long(key));
				String chave = aux.getChave();
				LOG.debug("Chave:" + chave);
				ColetaInformacoesSonarBean sonar = new ColetaInformacoesSonarBean();
				Captura captura = sonar.getSonarApi(chave);
				String repositorio = Repositorio.RTC.getRepositorio();
				String ambiente = Ambiente.DESENVOLVIMENTO.getAmbiente();
				if (captura != null && !chave.equalsIgnoreCase("null")) {
					Analise_Dev_Diario analise = new Analise_Dev_Diario(captura);
					analise.setSigla(aux.getSigla());
					analise.setNomeProjeto(aux.getNomePainel());
					analise.setAmbiente(ambiente);
					analise.setRepositorio(repositorio);
					analise.setPadraoNomeSonar(aux.getNomeProjetoPadronizado());
					new Analise_DiarioDAO().salvar(analise);
					LOG.info("Projeto :" + analise.getNomeProjeto() + " capturado!!");
				}

			} catch (Exception e) {
				LOG.error("Erro ao executar captura diária", e);
			}
		}
	};

	/***
	 * 
	 * Metodo que dispara a execução do Sonar para uma Sigla do RTC especifico
	 * 
	 */

	public void executarSonar() {
		if (eCapturaMensal) {
			System.out.println("\nMensal !\n");
			Executors
					.newFixedThreadPool(1,
							new YourThreadFactory(ControleRtcDev.class.getName() + "-" + obj.getCodigo()))
					.submit(ExecutarSonarPorModulosBean.sonarScannerAutomaticoMensal);
		} else {
			System.out.println("\nDiária !\n");
			Executors
					.newFixedThreadPool(1,
							new YourThreadFactory(ControleRtcDev.class.getName() + "-" + obj.getCodigo()))
					.submit(ExecutarSonarPorModulosBean.sonarScannerAutomaticoDiaria);
		}
	}

	/**
	 * Seleciona um objeto ControleRtcDev da tabela
	 */
	public void selecionarObj(ActionEvent evento) {

		try {
			obj = (ControleRtcDev) evento.getComponent().getAttributes().get("meuSelect");
			LOG.info("SIGLA:" + obj.getSigla());

		} catch (Exception e) {
			LOG.error("Erro ao selecionar", e);
		}
	}

	/***
	 * 
	 * Coloca um objeto do tipo ControleRtcDev com selecionado
	 * 
	 */

	public void selecionarModulo(ActionEvent evento) {
		try {
			obj = (ControleRtcDev) evento.getComponent().getAttributes().get("meuSelect");
			LOG.debug("ID: " + obj.getChave());
			if (obj.getSelecionado() == null) {
				obj.setSelecionado("ui-icon-check");
			} else {
				obj.setSelecionado(obj.getSelecionado().equals("ui-icon-check") ? "ui-icon-blank" : "ui-icon-check");
			}
			new ControleRtcDevDAO().editar(obj);

		} catch (Exception e) {
			LOG.error("Erro ao Selecionar: ", e);
		}
	}

	/**
	 * Salva objeto do tipo ControleRtcDev
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
	 * Lista os objetos do tipo ControleRtcDev
	 */
	// ------------------------------------------------------------------------------------------------------------------------------------------------------
	public void listarInfos() {
		try {
			dao = new ControleRtcDevDAO();
			listaControle = dao.listar();
			total = listaControle.size();
			listaControle.sort(ControleRtcDev.getComparadorPorDataCommit());
			if (Faces.getContext() != null) {
				Messages.addGlobalInfo("Lista do RTC DEV Atualizada!");
			}
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
	public void salvarPlanilha() {
		controle = new ControleRtcDev();
		dao = new ControleRtcDevDAO();
		String sigla, sistema, caminho;
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
				Cell celula3 = sheet.getCell(2, i); // coluna 3 - caminho

				sigla = celula1.getContents().toString().trim().toUpperCase();
				sistema = celula2.getContents().toString().trim().toUpperCase();

				caminho = celula3.getContents().toString().trim().toUpperCase();

				// Encerra a leitura quando encontra linha vazia
				if (sigla.isEmpty()) {
					break;
				}

				if (!sigla.isEmpty()) {
					Ambiente ambiente = Ambiente.DESENVOLVIMENTO;
					Repositorio repositorio = Repositorio.RTC;
					dateC = Date.from(LocalDateTime.now().atZone(ZoneId.of("Etc/GMT+3")).toInstant());
					controle.setSigla(sigla);
					controle.setNomeSistema(sistema);
					controle.setCaminho(caminho);
					controle.setSelecionado("ui-icon-blank");
					controle.setNomeArquivo(CAMINHO);
					controle.setDataVerificacao(dateC);
					controle.setDataCommitAnt(null);
					controle.setAmbiente(ambiente.getAmbiente());
					controle.setRepositorio(repositorio.getRepositorio());
					controle.setNomeProjetoPadronizado(
							new ProjectName(sigla, sistema, repositorio, ambiente).getPadraoNomeProjeto());
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
	 * Limpa as informações da tabela ControleRtcDev no banco de dados
	 */
	// -------------------------------------------------------------------------------------
	public void limparDB() {
		try {
			listarInfos();
			for (ControleRtcDev ControleRtcDev : listaControle) {
				ControleRtcDev entidade = dao.buscar(ControleRtcDev.getCodigo());
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
			List<ControleRtcDev> listaControle;
			ControleRtcDevDAO dao = new ControleRtcDevDAO();
			listaControle = dao.listar();

			for (ControleRtcDev obj : listaControle) {
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
	 * @param obj - Objeto ControleRtcDev
	 */
	// -------------------------------------------------------------------------------------
	public static void lerLogRtc(ControleRtcDev obj) {
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
							LocalDate dataLocalizada = dataCommit.toInstant().atZone(ZoneId.systemDefault())
									.toLocalDate();
							LocalDate dataArmazenada = obj.getDataCommit().toInstant().atZone(ZoneId.systemDefault())
									.toLocalDate();
							if (dataArmazenada.equals(dataLocalizada)) {
								obj.setAlteracao(false);
							} else {
								obj.setAlteracao(true);
								new ModulosRTCDEVBean().executaHistory(obj);
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
			new ControleRtcDevDAO().editar(obj);
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
	 * Metodo para capturar todas as siglas
	 * 
	 */

	public void executarCapturaTodasSiglasAtualizadas() {
		try {
			new Thread(runnableExecutaCapturaTodasSiglasAtualizadas).start();
		} catch (Exception e) {
			LOG.error("Erro ao tentar captura todas as sigla atualizadas", e);
		}
	}

	Runnable runnableExecutaCapturaTodasSiglasAtualizadas = new Runnable() {

		@Override
		public void run() {
			ControleRtcDevDAO controleRtcDAO = new ControleRtcDevDAO();
			List<ControleRtcDev> listaSiglaAtualizadas = controleRtcDAO.listar().stream().filter(c -> c.isAlteracao())
					.collect(Collectors.toList());
			for (ControleRtcDev con : listaSiglaAtualizadas) {
				LOG.debug(con.getChave());
				if (eCapturaMensal) {
					Executors.newFixedThreadPool(10, new YourThreadFactory(con.getCodigo().toString()))
							.submit(capturaMensal);
				} else {
					Executors.newFixedThreadPool(10, new YourThreadFactory(con.getCodigo().toString()))
							.submit(capturaDiaria);

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
			@SuppressWarnings("unchecked")
			List<ControleRtcDev> listaModulosSelecionados = ((List<ControleRtcDev>) evento.getComponent()
					.getAttributes().get("tabela")).stream().filter(r -> r.getSelecionado().equals("ui-icon-check"))
							.collect(Collectors.toList());
			for (ControleRtcDev relacao : listaModulosSelecionados) {
				LOG.debug(relacao.getChave());
				new ControleRtcDevDAO().editar(relacao);
			}
			new Thread(runnableExecutarCapturaPorSiglas).start();
		} catch (Exception e) {
			LOG.info("Erro ao executar captura", e);
		}

	}

	Runnable runnableExecutarCapturaPorSiglas = new Runnable() {

		@Override
		public void run() {
			try {
				List<ControleRtcDev> listaModulosSelecionados = new ControleRtcDevDAO().listar().stream()
						.filter(r -> r.getSelecionado().equals("ui-icon-check")).collect(Collectors.toList());
				for (ControleRtcDev relacao : listaModulosSelecionados) {
					System.out.println(relacao.getChave());
					if (eCapturaMensal) {
						Executors.newFixedThreadPool(10, new YourThreadFactory(relacao.getCodigo().toString()))
								.submit(capturaMensal);
					} else {
						Executors.newFixedThreadPool(10, new YourThreadFactory(relacao.getCodigo().toString()))
								.submit(capturaDiaria);
					}

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
			new Thread(runnableExecutaTodasSiglasAtualizadas).start();
		} catch (Exception e) {
			LOG.error("Erro ao executar o Sonar para as siglas atualizadas", e);
		}

	}

	Runnable runnableExecutaTodasSiglasAtualizadas = new Runnable() {
		public void run() {
			try {
				List<ControleRtcDev> listaModulosSelecionados = new ControleRtcDevDAO().listar().stream()
						.filter(c -> c.isAlteracao()).collect(Collectors.toList());

				Runnable tipoExecucao = eCapturaMensal ? ExecutarSonarPorModulosBean.sonarScannerAutomaticoMensal
						: ExecutarSonarPorModulosBean.sonarScannerAutomaticoDiaria;

				String nomeDaClasseComTraco = ControleRtcDev.class.getName() + "-";

				for (ControleRtcDev controle : listaModulosSelecionados) {

					Executors.newFixedThreadPool(5, new YourThreadFactory(nomeDaClasseComTraco + controle.getId()))
							.submit(tipoExecucao);

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
			new Thread(runnableExecutarSonarPorSiglasSelecionadas).start();
		} catch (Exception e) {
			LOG.error("Erro ao executar sonar", e);
		}
	}

	Runnable runnableExecutarSonarPorSiglasSelecionadas = new Runnable() {

		@Override
		public void run() {
			List<ControleRtcDev> listaModulosSelecionados = new ControleRtcDevDAO().listar().stream()
					.filter(r -> r.getSelecionado().equals("ui-icon-check")).collect(Collectors.toList());
			for (ControleRtcDev controle : listaModulosSelecionados) {
				if (eCapturaMensal) {
					System.out.println("\nMensal !\n");
					Executors
							.newFixedThreadPool(1,
									new YourThreadFactory(ControleRtcDev.class.getName() + "-" + controle.getCodigo()))
							.submit(ExecutarSonarPorModulosBean.sonarScannerAutomaticoMensal);
				} else {
					System.out.println("\nDiária !\n");
					Executors
							.newFixedThreadPool(1,
									new YourThreadFactory(ControleRtcDev.class.getName() + "-" + controle.getCodigo()))
							.submit(ExecutarSonarPorModulosBean.sonarScannerAutomaticoDiaria);
				}
			}
		}
	};

	public void buscaUltimaAltualizacaoRTC() {
		new ControleRtcDevDAO().buscaDataAtualizacaoPorSigla();
	}

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
				List<ControleRtcDev> listaModulosSelecionados = new ControleRtcDevDAO().listar().stream()
						.filter(c -> c.isAlteracao()).collect(Collectors.toList());
				List<ControleRtcDev> listaModulosNaoAlterados = new ControleRtcDevDAO().listar().stream()
						.filter(c -> c.isAlteracao()).collect(Collectors.toList());
				if (listaModulosSelecionados.isEmpty()) {
					LOG.info("Nenhuma Sigla do RTC Atualizada");
				}
				for (ControleRtcDev controle : listaModulosSelecionados) {
					controle.setCapturado(false);
					new ControleRtcDevDAO().editar(controle);
					Executors
							.newFixedThreadPool(5,
									new YourThreadFactory(ControleRtcDev.class.getName() + "-" + controle.getId()))
							.submit(ExecutarSonarPorModulosBean.sonarScannerAutomaticoMensal);
				}
				for (ControleRtcDev controle : listaModulosNaoAlterados) {
					System.out.println("Capturando " + controle.getNomeSistema());
					Executors.newFixedThreadPool(5, new YourThreadFactory(controle.getId()))
							.submit(ControleRtcDevBean.capturaMensal);
				}
			} catch (Exception e) {
				LOG.error("Erro ao executar o RTC Log", e);
			}
		}
	};

	public void executarTodasSiglasAtualizadasAutomatico() {

		try {
			new Thread(runnableExecutaTodasSiglasAtualizadasAt).start();
		} catch (Exception e) {
			LOG.error("Erro ao executar o Sonar para as siglas atualizadas", e);
		}

	}

	Runnable runnableExecutaTodasSiglasAtualizadasAt = new Runnable() {
		public void run() {
			try {

				ControleRtcDevDAO controleRTCDevDAO = new ControleRtcDevDAO();
				LocalDate hoje = LocalDate.now();
				ArrayList<ControleRtcDev> listaModulosSelecionados = (ArrayList<ControleRtcDev>) controleRTCDevDAO
						.listar().stream().filter(c -> c.getDataCommit() != null)
						.filter(c -> Instant.ofEpochMilli(c.getDataCommit().getTime()).atZone(ZoneId.systemDefault())
								.toLocalDate().equals(hoje))
						.collect(Collectors.toList());

				if (listaModulosSelecionados.isEmpty()) {
					LOG.info("Nenhuma Sigla do RTC Atualizada");
				}
				for (ControleRtcDev controle : listaModulosSelecionados) {
					Executors
							.newFixedThreadPool(5,
									new YourThreadFactory(ControleRtcDev.class.getName() + "-" + controle.getId()))
							.submit(ExecutarSonarPorModulosBean.sonarScannerAutomaticoDiaria);
				}
			} catch (Exception e) {
				LOG.error("Erro ao executar o RTC Log", e);
			}
		}
	};

	// Get e Set
	// ------------------------------------------------------------------------------------------------------------------------------------------------------

	public ControleRtcDev getobj() {
		return obj;
	}

	public boolean isTipoCaptura() {
		return eCapturaMensal;
	}

	public void setTipoCaptura(boolean tipoCaptura) {
		this.eCapturaMensal = tipoCaptura;
	}

	public ControleRtcDev getControle() {
		return controle;
	}

	public List<ControleRtcDev> getListaControle() {
		return listaControle;
	}

	public void setListaControle(List<ControleRtcDev> listaControle) {
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
		return capturaDiaria;
	}

	public static Runnable getCapturaMensal() {
		return capturaMensal;
	}

	// ------------------------------------------------------------------------------------------------------------------------------------------------------

	public StreamedContent getExemploSiglasRTC() {
		return new MetodosUteis().getExcelComoStreamedContent("/resources/exemplos_excel/exemplo_siglasRTC.xls",
				"exemplo_siglasRTC.xls");
	}
}