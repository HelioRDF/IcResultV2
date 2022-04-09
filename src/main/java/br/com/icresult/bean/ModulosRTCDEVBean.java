package br.com.icresult.bean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.omnifaces.util.Messages;

import br.com.icresult.dao.complementos.Analise_HomologacaoDAO;
import br.com.icresult.dao.complementos.ControleRtcDevDAO;
import br.com.icresult.dao.complementos.ControleRtcHKDAO;
import br.com.icresult.dao.complementos.ControleSiglasDAO;
import br.com.icresult.dao.complementos.ModulosRTCDEVDAO;
import br.com.icresult.domain.complementos.Analise_Homologacao;
import br.com.icresult.domain.complementos.ControleRtcDev;
import br.com.icresult.domain.complementos.ControleRtcHK;
import br.com.icresult.domain.complementos.ControleSiglas;
import br.com.icresult.domain.complementos.ModulosRTCDEV;
import br.com.icresult.model.Captura;
import br.com.icresult.util.MetodosUteis;
import br.com.icresult.util.YourThreadFactory;

/**
 * -Classe BEAN RelacaoProjetoSiglaModulosRTCBean que cria a estrutura do sonar
 * para os modulos do RTC.
 * 
 * @author andre.graca
 * @version v1.1
 * @since 21-11-2018
 * 
 */

@SuppressWarnings("serial")
@ManagedBean(name = "relacaoProjetoSiglaModulosRTCDevBean")
@SessionScoped
public class ModulosRTCDEVBean implements Serializable {

	private static ModulosRTCDEVDAO rpDAO;
	private static List<ModulosRTCDEV> listaControle;
	private static int total;
	private ModulosRTCDEV obj;
	private ControleRtcDev controle;
	private static Logger LOG = Logger.getLogger(ModulosRTCDEVBean.class);

	/**
	 * Metodo que executa o Runnable resposavel por procurar todas as atualizações
	 * dos módulos do RTC
	 */
	public void gerarLogRTC() {
		new Thread(executaHistoryTodosModulosRTC).start();
	}

	/**
	 * Runnable que verifica atualizaçõa de todos os modulos do RTC
	 */
	private Runnable executaHistoryTodosModulosRTC = new Runnable() {

		@Override
		public void run() {
			List<ControleRtcDev> controleDAO = new ControleRtcDevDAO().listar();
			for (ControleRtcDev c : controleDAO) {
				executaHistory(c);
			}
		}
	};

	private static Integer mesComoNumeral(String mesComoString) {

		Map<String, Integer> meses = new HashMap<String, Integer>();

		meses.put("Jan", 1);
		meses.put("Fev", 2);
		meses.put("Mar", 3);
		meses.put("Abr", 4);
		meses.put("Mai", 5);
		meses.put("Jun", 6);
		meses.put("Jul", 7);
		meses.put("Ago", 8);
		meses.put("Set", 9);
		meses.put("Out", 10);
		meses.put("Nov", 11);
		meses.put("Dez", 12);

		return meses.get(mesComoString);
	}

	public void executaHistory(ControleRtcDev controle) {
		Executors.newFixedThreadPool(10, new YourThreadFactory(controle.getId())).submit(comandoHistory);
	}

	private Runnable comandoHistory = new Runnable() {

		@Override
		public void run() {

			Long idSigla = new Long(Thread.currentThread().getName());

			ControleRtcDev controle = new ControleRtcDevDAO().buscar(idSigla);

			LOG.info("Buscando atualizações nos módulos da sigla " + controle.getSigla());

			List<ModulosRTCDEV> modulosDentroDoControle = new ModulosRTCDEVDAO().listar().stream()
					.filter(r -> r.getSigla().equals(controle.getSigla())).collect(Collectors.toList());
			for (ModulosRTCDEV relacao : modulosDentroDoControle) {

				String pathSigla = "cd " + relacao.getCaminho();
				String comandoAccept = "scm accept --overwrite-uncommitted";
				String comandoHistory = "scm history -j";
				String[] cmds = { pathSigla, comandoAccept, comandoHistory };
				BufferedReader in = null;
				StringBuilder log = new StringBuilder();
				try {
					ProcessBuilder builder = new ProcessBuilder("cmd", "/c", String.join("& ", cmds));
					builder.redirectErrorStream(true);
					Process p = builder.start();

					in = new BufferedReader(new InputStreamReader(p.getInputStream()));

					LOG.info("Analisando historico do módulo " + relacao.getCaminho());

					String line = new String();

					while ((line = in.readLine()) != null) {
						log.append(line);
					}
					String jsonReferenteHistory = log.toString().substring(log.toString().indexOf("{"));
					System.out.println(jsonReferenteHistory);
					JSONObject json = new JSONObject(jsonReferenteHistory);
					JSONArray changes = json.getJSONArray("changes");
					JSONObject ultimaAtualizacao = new JSONObject(changes.get(0).toString());
					String data = ultimaAtualizacao.get("modified").toString();

					Integer horaUltimaAtualizacao = Integer.parseInt(data.substring(12, 14));

					LocalDateTime horario = LocalDateTime.of(Integer.parseInt(data.substring(7, 11)),
							mesComoNumeral(data.substring(3, 6)), Integer.parseInt(data.substring(0, 2)),
							data.substring(18, 20).equals("PM")
									? (horaUltimaAtualizacao + 12) == 24 ? 0 : (horaUltimaAtualizacao + 12)
									: horaUltimaAtualizacao,
							Integer.parseInt(data.substring(15, 17)));

					Date commitAtual = relacao.getDataCommit();

					Boolean atualizacao = new Boolean(false);

					if (commitAtual == null) {
						relacao.setDataCommit(Timestamp.valueOf(horario));
					} else {
						if (!commitAtual.equals(Timestamp.valueOf(horario))) {
							atualizacao = true;
							relacao.setDataCommit(Timestamp.valueOf(horario));
							relacao.setDataCommitAnt(commitAtual);
						}
					}

					String comitador = ultimaAtualizacao.getString("author");
					relacao.setAutor(comitador);

					relacao.setAlterada(atualizacao);

					new ModulosRTCDEVDAO().editar(relacao);

				} catch (Exception e) {
					LOG.error("Erro ao buscar atualização do módulo " + relacao.getNome_Projeto(), e);
				} finally {
					if (in != null) {
						try {
							in.close();
						} catch (IOException e) {
							LOG.error("Erro ao busca atualização do módulo " + relacao.getNome_Projeto(), e);
						}
					}
				}
			}
		}
	};

	public List<ModulosRTCDEV> paineisSelecionados() {
		return new ModulosRTCDEVDAO().listar().stream().filter(r -> r.getSelecionado().equals("ui-icon-check"))
				.collect(Collectors.toList());
	}

	public List<ModulosRTCDEV> paineisNaoSelecionados() {
		return new ModulosRTCDEVDAO().listar().stream().filter(r -> r.getSelecionado().equals("ui-icon-blank"))
				.collect(Collectors.toList());
	}

	public void selecionarTodosModulos() {
		ModulosRTCDEVDAO relacaoProjetoDAO = new ModulosRTCDEVDAO();
		for (ModulosRTCDEV controle : paineisNaoSelecionados()) {
			controle.setSelecionado("ui-icon-check");
			relacaoProjetoDAO.editar(controle);
		}
		listarInfos();
	}

	public void limparSelecaoTodosModulos() {
		ModulosRTCDEVDAO relacaoProjetoDAO = new ModulosRTCDEVDAO();
		for (ModulosRTCDEV controle : paineisSelecionados()) {
			System.out.println(controle.getId());
			controle.setSelecionado("ui-icon-blank");
			relacaoProjetoDAO.editar(controle);
		}
		listarInfos();
	}

	public void habilitarCaptura() {
		ModulosRTCDEVDAO relacaoProjetoDAO = new ModulosRTCDEVDAO();
		for (ModulosRTCDEV controle : relacaoProjetoDAO.listar()) {
			controle.setCapturado(false);
			relacaoProjetoDAO.editar(controle);
		}
	}

	public void executarCapturaSiglasAtualizadas() {
		ControleRtcHKDAO controleRtcDAO = new ControleRtcHKDAO();
		ModulosRTCDEVDAO relacaoProjetoDAO = new ModulosRTCDEVDAO();
		ArrayList<ControleRtcHK> listaSiglaAtualizadas = (ArrayList<ControleRtcHK>) controleRtcDAO.listar().stream()
				.filter(c -> c.isAlteracao()).collect(Collectors.toList());
		if (listaSiglaAtualizadas.isEmpty()) {
			System.out.println("Nenhuma Sigla foi atualizada");
		}
		for (ControleRtcHK con : listaSiglaAtualizadas) {

			List<ModulosRTCDEV> modulosSiglasAtualizadas = relacaoProjetoDAO.listar().stream()
					.filter(r -> r.getSigla().equals(con.getSigla())).collect(Collectors.toList());
			for (ModulosRTCDEV relacao : modulosSiglasAtualizadas) {
				Executors.newFixedThreadPool(10, new YourThreadFactory(relacao.getChave())).submit(captura);
			}
		}

	}

	public void executarCapturaPorModulos(ActionEvent evento) {

		@SuppressWarnings("unchecked")
		List<ModulosRTCDEV> listaModulosSelecionados = ((List<ModulosRTCDEV>) evento.getComponent().getAttributes()
				.get("tabela")).stream().filter(r -> r.getSelecionado().equals("ui-icon-check"))
						.collect(Collectors.toList());
		for (ModulosRTCDEV relacao : listaModulosSelecionados) {
			LocalTime horarioAtual = LocalTime.now();
			System.out.println(horarioAtual.toString());
			Executors.newFixedThreadPool(3, new YourThreadFactory(relacao.getChave())).submit(captura);
		}
	}

	public void executarSonarPorModulosSelecionados(ActionEvent evento) {

		try {
			@SuppressWarnings("unchecked")
			List<ModulosRTCDEV> listaModulosSelecionados = ((List<ModulosRTCDEV>) evento.getComponent().getAttributes()
					.get("tabela")).stream().filter(r -> r.getSelecionado().equals("ui-icon-check"))
							.collect(Collectors.toList());
			Map<String, String> siglaCaminho = new HashMap<>();
			for (ControleRtcHK cont : new ControleRtcHKDAO().listar()) {
				siglaCaminho.put(cont.getSigla(), cont.getCaminho());
			}
			for (ModulosRTCDEV relacao : listaModulosSelecionados) {
				relacao.setCapturado(false);
				new ModulosRTCDEVDAO().editar(relacao);
				StringBuilder caminhoModulo = new StringBuilder();
				caminhoModulo.append(siglaCaminho.get(relacao.getSigla()));
				caminhoModulo.append("/");
				caminhoModulo.append(relacao.getChave());
				Executors
						.newFixedThreadPool(5,
								new YourThreadFactory(ModulosRTCDEV.class.getName() + "-" + relacao.getChave()))
						.submit(ExecutarSonarPorModulosBean.sonarScanner);

			}
		} catch (Exception e) {
			e.getStackTrace();
		}

	}

	public void executarSonarPorSigla() {

		ControleRtcHKDAO controleRtcDAO = new ControleRtcHKDAO();
		ModulosRTCDEVDAO relacaoProjetoDAO = new ModulosRTCDEVDAO();
		ArrayList<ControleRtcHK> listaSiglaAtualizadas = (ArrayList<ControleRtcHK>) controleRtcDAO.listar().stream()
				.filter(c -> c.isAlteracao()).collect(Collectors.toList());
		if (listaSiglaAtualizadas.isEmpty()) {
			System.out.println("Nenhuma Sigla foi atualizada");
		}
		for (ControleRtcHK con : listaSiglaAtualizadas) {
			Set<ModulosRTCDEV> modulosSiglasAtualizadas = relacaoProjetoDAO.listar().stream()
					.filter(r -> r.getSigla().equals(con.getSigla())).collect(Collectors.toSet());
			for (ModulosRTCDEV relacao : modulosSiglasAtualizadas) {
				relacao.setCapturado(false);
				new ModulosRTCDEVDAO().editar(relacao);
				Executors
						.newFixedThreadPool(5,
								new YourThreadFactory(ModulosRTCDEV.class.getName() + "-" + relacao.getId()))
						.submit(ExecutarSonarPorModulosBean.sonarScanner);
			}
		}

	}

	public void capturar() {
		String chave = obj.getChave();
		System.out.println("\n\n\n\n------------chave:--------\n" + chave);
		Executors.newFixedThreadPool(1, new YourThreadFactory(chave)).submit(captura);
	}

	private static Runnable captura = new Runnable() {

		@Override
		public synchronized void run() {

			System.out.println("\n-----------------------\nID identificado: " + Thread.currentThread().getName());
			ModulosRTCDEVDAO daoTemp = new ModulosRTCDEVDAO();
			ModulosRTCDEV tempObj = daoTemp.buscarPorChave(Thread.currentThread().getName().trim());

			try {

				ColetaInformacoesSonarBean sonar = new ColetaInformacoesSonarBean();
				Analise_HomologacaoDAO analiseDAO = new Analise_HomologacaoDAO();
				Captura captura = sonar.getSonarApi(tempObj.getChave());
				if (captura != null) {
					Analise_Homologacao analise = new Analise_Homologacao(captura);
					analise.setSigla(tempObj.getSigla());
					analise.setDataCommit(tempObj.getDataCommit().toInstant().atZone(ZoneId.of("Etc/GMT+3"))
							.toLocalDate().toString());
					String painelGestor = new ControleSiglasDAO().buscaGestorPorSigla(tempObj.getSigla());
					analise.setPainelGestor(painelGestor);
					analise.setNomeProjeto(tempObj.getNome_Projeto());
					analiseDAO.salvar(analise);
					tempObj.setDataSonar(analise.getDataSonar());
					tempObj.setVersao(analise.getVersao());
					tempObj.setCapturado(true);
					new ModulosRTCDEVDAO().editar(tempObj);
					new Analise_Homologacao_Bean().calcNotaInfosAnt();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	};

	public void validarModulos() {
		criaEstruturaExecucaoRTC();

	}

	public static void criaEstruturaExecucaoRTC() {
		try {
			new Thread(criaEstruturaRTC).start();
		} catch (Exception e) {
			e.getStackTrace();
		}
	}

	/**
	 * Metodo para criar as pastas target, o arquivo de propriedades do sonar e a
	 * pasta LOG
	 */

	private static Runnable criaEstruturaRTC = new Runnable() {

		@Override
		public void run() {

			ControleRtcHKDAO controleDAO = new ControleRtcHKDAO();
			limparDB();
			for (ControleRtcHK controle : controleDAO.listar()) {

				String auxCaminho = controle.getCaminho();
				File diretorioRTC = new File(auxCaminho);
				Path caminhoSigla = Paths.get(auxCaminho.toString());
				if (Files.exists(caminhoSigla)) {
					String[] listaArquivosDentroDiretorio = diretorioRTC.list();

					for (String modulo : listaArquivosDentroDiretorio) {
						File caminhoArquivo = new File(auxCaminho.toString() + "//" + modulo);
						if (caminhoArquivo.isDirectory()) {
							if (!(modulo.charAt(0) == '.') && !modulo.equals("LOG") && !modulo.equals("target")
									&& !modulo.equals("LogDeletados")) {
								StringBuilder diretorioModulo = new StringBuilder(diretorioRTC + "\\" + modulo + "\\");
								StringBuilder chaveModulo = new StringBuilder(modulo);

								StringBuilder caminhoModulo = new StringBuilder(diretorioModulo);
								StringBuilder caminhoPropertiesSonar = new StringBuilder(
										caminhoModulo.toString() + "sonar-project.properties");
								Path diretorio = Paths.get(caminhoPropertiesSonar.toString());
								if (!Files.exists(diretorio)) {
									try {
										Files.createFile(diretorio);

									} catch (Exception e) {
										LOG.error("Erro ao criar o properties", e);
									}
								}
								// Criando o arquivo .properties necessario para a execução do Sonar

								Properties sonarProject = new Properties();
								try (FileOutputStream out = new FileOutputStream(diretorio.toString());
										FileInputStream in = new FileInputStream(diretorio.toString());) {
									sonarProject.load(new InputStreamReader(in, Charset.forName("UTF-8")));
									sonarProject.put("sonar.projectKey", chaveModulo);
									sonarProject.put("sonar.projectName", chaveModulo);
									sonarProject.put("sonar.projectVersion", "");
									sonarProject.put("sonar.sources", ".");
									sonarProject.put("sonar.java.binaries", "target/classes");
									sonarProject.put("sonar.exclusions",
											"**/*test.java,**/*teste.java,**/*.properties");
									sonarProject.put("sonar.cfamily.build-wrapper-output.bypass", "true");
									sonarProject.put("sonar.sourceEncoding", "ISO-8859-1");
									String caminhoProperties = caminhoPropertiesSonar.toString();
									sonarProject.store(out, "Properties Sonar " + caminhoProperties);

								} catch (Exception e) {
									LOG.error("Erro ao escrever o properties do Sonar", e);
								}
								// Copiando a pasta target para dentro das pastas dos modulos
								Path targetDentroDoPrograma = new MetodosUteis().caminhoTarget();
								Path caminhoDosModulos = Paths.get(caminhoModulo + "\\target");
								try {
									if (!Files.exists(caminhoDosModulos)) {
										Files.createDirectory(caminhoDosModulos);
										copiarArquivos(targetDentroDoPrograma, caminhoDosModulos);
									}
									// Salvando o modulo no banco de dados
									List<ControleSiglas> controleSiglas = new ControleSiglasDAO().listar();
									ModulosRTCDEV moduloRTCDB = new ModulosRTCDEV();
									moduloRTCDB.setChave(chaveModulo.toString() + "-DEV");
									moduloRTCDB.setNome_Projeto(chaveModulo.toString());
									moduloRTCDB.setCaminho(caminhoModulo.toString());
									moduloRTCDB.setSelecionado("ui-icon-blank");
									String[] lista = caminhoModulo.toString().split("\\\\");
									String sigla = lista[lista.length - 2];
									controleSiglas = controleSiglas.stream().filter(p -> p.getSigla().equals(sigla))
											.collect(Collectors.toList());
									moduloRTCDB.setSigla(sigla);
									moduloRTCDB.setCapturado(false);
									salvarModuloBD(moduloRTCDB);
								} catch (Exception e) {
									LOG.error("Erro ao salvar o módulo", e);
								}
							}
						}
					}
				}

			}

		}
	};

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
				System.out.println("Erro ao copiar pasta target para : " + destino);
			}

		} else {
			// copiamos o arquivo
			Files.copy(origem, destino);
		}
	}

	/***
	 * 
	 * @param moduloRTC objeto do tipo RelacaoProjetoSiglaModulosRTC a ser salvo no
	 *                  banco de dados Metódo responsável por salvar o moduloRTC no
	 *                  banco de dados
	 * 
	 */
	public static void salvarModuloBD(ModulosRTCDEV moduloRTC) {
		rpDAO = new ModulosRTCDEVDAO();
		rpDAO.salvar(moduloRTC);
	}

	/**
	 * Limpa as informações da tabela relacionada a classe
	 * RelacaoProjetoSiglaModulosRTC no banco de dados
	 */
	// -------------------------------------------------------------------------------------
	public static void limparDB() {
		ModulosRTCDEVDAO dao = new ModulosRTCDEVDAO();
		List<ModulosRTCDEV> listaModulos = dao.listar();
		try {
			for (ModulosRTCDEV entidade : listaModulos) {
				dao.excluir(entidade);
			}
		} catch (Exception e) {
			Messages.addGlobalError("Não foi possível limparDB");
		}
	}

	/**
	 * 
	 * Lista os objetos do tipo RelacaoProjetoSiglaModulos
	 * 
	 */
	// ------------------------------------------------------------------------------------------------------------------------------------------------------
	public void listarInfos() {
		try {
			rpDAO = new ModulosRTCDEVDAO();
			rpDAO.preencherCampoSelecionado();
			listaControle = rpDAO.listar();
			total = listaControle.size();
			LOG.info("Lista Atualizada!");

		} catch (Exception e) {
			LOG.error("Erro ao  Atualizar Lista.", e);
		}
	}

	/**
	 * Seleciona um objeto RelacaoProjetoSiglaModulosRTC da tabela
	 */
	// -------------------------------------------------------------------------------------
	public void selecionarObj(ActionEvent evento) {

		try {
			obj = (ModulosRTCDEV) evento.getComponent().getAttributes().get("meuSelect");
			System.out.println("\n\n\n\n------------ID:--------\n" + obj.getChave());
		} catch (Exception e) {
			Messages.addGlobalError("Erro ao Selecionar: ");
		}
	}

	/**
	 * Seleciona um objeto RelacaoProjetoSiglaModulosRTC, deixando o objeto marcado
	 * para execução do sonar ou captura em grupo de modulos
	 */
	// -------------------------------------------------------------------------------------
	public void selecionarModulo(ActionEvent evento) {

		try {
			obj = (ModulosRTCDEV) evento.getComponent().getAttributes().get("meuSelect");
			System.out.println("\n\n\n\n------------ID:--------\n" + obj.getId());
			obj.setSelecionado(obj.getSelecionado().equals("ui-icon-check") ? "ui-icon-blank" : "ui-icon-check");
			new ModulosRTCDEVDAO().editar(obj);

		} catch (Exception e) {
			Messages.addGlobalError("Erro ao Selecionar: ");
		}
	}

	/***
	 * 
	 * Metodo que dispara a execução do Sonar para um Modulo especifico
	 * 
	 */

	public void executarSonar() {
		Executors.newFixedThreadPool(1, new YourThreadFactory(ModulosRTCDEV.class.getName() + "-" + obj.getId()))
				.submit(ExecutarSonarPorModulosBean.sonarScanner);
	}

	public void executarTodasSiglasAtualizadasAutomatico() {
		try {
			new Thread(runnableExecutaTodasSiglasAtualizadasAt).start();
		} catch (Exception e) {
			System.out.println("Erro ao executar o Sonar para as siglas atualizadas");
			e.printStackTrace();
		}

	}

	Runnable runnableExecutaTodasSiglasAtualizadasAt = new Runnable() {
		public void run() {
			try {
				List<ModulosRTCDEV> listaModulosSelecionados = new ModulosRTCDEVDAO().listar().stream()
						.filter(c -> c.getAlterada()).collect(Collectors.toList());
				if (listaModulosSelecionados.isEmpty()) {
					System.out.println("Nenhum Modulo do RTC HK foi atualizado");
				}
				for (ModulosRTCDEV controle : listaModulosSelecionados) {
					Executors
							.newFixedThreadPool(5,
									new YourThreadFactory(ModulosRTCDEV.class.getName() + "-" + controle.getId()))
							.submit(ExecutarSonarPorModulosBean.sonarScannerAutomaticoDiaria);
				}
			} catch (Exception e) {
				e.getStackTrace();
			}
		}
	};

	// Get e Set
	// ------------------------------------------------------------------------------------------------------------------------------------------------------

	public List<ModulosRTCDEV> getListaControle() {
		return listaControle;
	}

	public int getTotal() {
		return total;
	}

	public ModulosRTCDEV getObj() {
		return obj;
	}

	public ControleRtcDev getControle() {
		return controle;
	}

	public static Runnable getCaptura() {
		return captura;
	}

}
