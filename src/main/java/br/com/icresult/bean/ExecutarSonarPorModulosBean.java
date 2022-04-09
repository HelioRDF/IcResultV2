package br.com.icresult.bean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.icresult.util.MetodosUteis;
import br.com.icresult.util.YourThreadFactory;

/**
 * -Classe BEAN ExecutarSonarPorModulosBean que executa o sonar por Modulo do
 * RTC.
 * 
 * @author andre.graca
 * @version v1.0
 * @since 16-11-2018
 * 
 */
public class ExecutarSonarPorModulosBean {

	private static Logger LOG = LoggerFactory.getLogger(ExecutarSonarPorModulosBean.class);

	/**
	 * 
	 * Runnable responsável pela execução do sonar scanner a partir do caminho de um
	 * modulo do Sonar
	 * 
	 */
	public static final Runnable sonarScanner = new Runnable() {

		public void run() {

			try {

				String nomeThread = Thread.currentThread().getName();
				String[] parametros = nomeThread.split("[-]");

				System.out.println("\n-----------------------\nID identificado: " + nomeThread);
				Class<?> classeObjetoEnviadoExecucaoSonar = Class.forName(parametros[0]);
				String[] pacotes = classeObjetoEnviadoExecucaoSonar.getName().split("[.]");
				String nomeClasseSemPacote = pacotes[pacotes.length - 1];
				Long id = Long.parseLong(parametros[1]);

				Constructor<?> contrutorObjetoEnviadoExecucaoSonar = classeObjetoEnviadoExecucaoSonar.getConstructor();
				Object domain = contrutorObjetoEnviadoExecucaoSonar.newInstance();

				Class<?> classeDao = Class.forName("br.com.icresult.dao.complementos." + nomeClasseSemPacote + "DAO");
				Constructor<?> contrutorDAO = classeDao.getConstructor();
				Object instanciaDao = contrutorDAO.newInstance();

				Method metodoBuscarDao = classeDao.getMethod("buscar", Long.class);

				domain = metodoBuscarDao.invoke(instanciaDao, id);

				String caminho = (String) classeObjetoEnviadoExecucaoSonar.getMethod("getCaminho").invoke(domain);
				String moduloAux = (String) classeObjetoEnviadoExecucaoSonar.getMethod("getChave").invoke(domain);
				String auxNomePainel = (String) classeObjetoEnviadoExecucaoSonar.getMethod("getNomeProjetoPadronizado")
						.invoke(domain);

				if (!caminho.isEmpty()) {
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
					String dataExecucao = LocalDateTime.now().format(formatter).toString();
					executaSonarComLOG(caminho, moduloAux, auxNomePainel, dataExecucao);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	};

	/**
	 * 
	 * Runnable responsável pela execução do sonar scanner de maneira automatica
	 * para a inspeção mensal
	 * 
	 */
	public static final Runnable sonarScannerAutomaticoMensal = new Runnable() {
		public void run() {
			try {

				String nomeThread = Thread.currentThread().getName();
				String[] parametros = nomeThread.split("[-]");

				System.out.println("\n-----------------------\nID identificado: " + nomeThread);
				Class<?> classeObjetoEnviadoExecucaoSonar = Class.forName(parametros[0]);
				String[] pacotes = classeObjetoEnviadoExecucaoSonar.getName().split("[.]");
				String nomeClasseSemPacote = pacotes[pacotes.length - 1];
				Long id = Long.parseLong(parametros[1]);

				Constructor<?> contrutorObjetoEnviadoExecucaoSonar = classeObjetoEnviadoExecucaoSonar.getConstructor();
				Object domain = contrutorObjetoEnviadoExecucaoSonar.newInstance();

				Class<?> classeDao = Class.forName("br.com.icresult.dao.complementos." + nomeClasseSemPacote + "DAO");
				Constructor<?> contrutorDAO = classeDao.getConstructor();
				Object instanciaDao = contrutorDAO.newInstance();

				Method metodoBuscarDao = classeDao.getMethod("buscar", Long.class);

				domain = metodoBuscarDao.invoke(instanciaDao, id);

				String caminho = (String) classeObjetoEnviadoExecucaoSonar.getMethod("getCaminho").invoke(domain);
				String moduloAux = (String) classeObjetoEnviadoExecucaoSonar.getMethod("getChave").invoke(domain);
				String auxNomePainel = (String) classeObjetoEnviadoExecucaoSonar.getMethod("getNomeProjetoPadronizado")
						.invoke(domain);

				Long IdObjeto = (Long) classeObjetoEnviadoExecucaoSonar.getMethod("getId").invoke(domain);

				if (!caminho.isEmpty()) {

					try {
						DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
						String dataExecucao = LocalDateTime.now().format(formatter).toString();
						aguardaExecucaoSonar(caminho, moduloAux, auxNomePainel, dataExecucao);
						verificaSeExecucaoFoiConcluidaMensal(nomeClasseSemPacote, caminho, moduloAux, IdObjeto,
								dataExecucao);

					} catch (Exception e) {
						System.out.println("Erro ao executar o Sonar " + caminho);
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	/**
	 * 
	 * Runnable responsável pela execução do sonar scanner de maneira automatica
	 * para a inspeção diária
	 * 
	 */
	public static final Runnable sonarScannerAutomaticoDiaria = new Runnable() {
		public void run() {
			try {

				String nomeThread = Thread.currentThread().getName();
				String[] parametros = nomeThread.split("[-]");

				System.out.println("\n-----------------------\nID identificado: " + nomeThread);
				Class<?> classeObjetoEnviadoExecucaoSonar = Class.forName(parametros[0]);
				String[] pacotes = classeObjetoEnviadoExecucaoSonar.getName().split("[.]");
				String nomeClasseSemPacote = pacotes[pacotes.length - 1];
				Long id = Long.parseLong(parametros[1]);

				Constructor<?> contrutorObjetoEnviadoExecucaoSonar = classeObjetoEnviadoExecucaoSonar.getConstructor();
				Object domain = contrutorObjetoEnviadoExecucaoSonar.newInstance();

				Class<?> classeDao = Class.forName("br.com.icresult.dao.complementos." + nomeClasseSemPacote + "DAO");
				Constructor<?> contrutorDAO = classeDao.getConstructor();
				Object instanciaDao = contrutorDAO.newInstance();

				Method metodoBuscarDao = classeDao.getMethod("buscar", Long.class);

				domain = metodoBuscarDao.invoke(instanciaDao, id);

				String caminho = (String) classeObjetoEnviadoExecucaoSonar.getMethod("getCaminho").invoke(domain);
				String moduloAux = (String) classeObjetoEnviadoExecucaoSonar.getMethod("getChave").invoke(domain);
				String auxNomePainel = (String) classeObjetoEnviadoExecucaoSonar.getMethod("getNomeProjetoPadronizado")
						.invoke(domain);

				Long IdObjeto = (Long) classeObjetoEnviadoExecucaoSonar.getMethod("getId").invoke(domain);

				if (!caminho.isEmpty()) {

					try {
						DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
						String dataExecucao = LocalDateTime.now().format(formatter).toString();
						aguardaExecucaoSonar(caminho, moduloAux, auxNomePainel, dataExecucao);
						verificaSeExecucaoFoiConcluida(nomeClasseSemPacote, caminho, moduloAux, IdObjeto, dataExecucao);

					} catch (Exception e) {
						System.out.println("Erro ao executar o Sonar " + caminho);
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	/**
	 * 
	 * Runnable responsável pela execução do sonar scanner de maneira automatica
	 * para as inspeções genéricas
	 * 
	 */
	public static final Runnable sonarScannerAutomaticoInspecaoGenerica = new Runnable() {
		public void run() {
			try {

				String nomeThread = Thread.currentThread().getName();
				String[] parametros = nomeThread.split("[-]");

				System.out.println("\n-----------------------\nID identificado: " + nomeThread);
				Class<?> classeObjetoEnviadoExecucaoSonar = Class.forName(parametros[0]);
				String[] pacotes = classeObjetoEnviadoExecucaoSonar.getName().split("[.]");
				String nomeClasseSemPacote = pacotes[pacotes.length - 1];
				Long id = Long.parseLong(parametros[1]);
				String tipoEntrega = parametros[2];

				Constructor<?> contrutorObjetoEnviadoExecucaoSonar = classeObjetoEnviadoExecucaoSonar.getConstructor();
				Object domain = contrutorObjetoEnviadoExecucaoSonar.newInstance();

				Class<?> classeDao = Class.forName("br.com.icresult.dao.complementos." + nomeClasseSemPacote + "DAO");
				Constructor<?> contrutorDAO = classeDao.getConstructor();
				Object instanciaDao = contrutorDAO.newInstance();

				Method metodoBuscarDao = classeDao.getMethod("buscar", Long.class);

				domain = metodoBuscarDao.invoke(instanciaDao, id);

				String caminho = (String) classeObjetoEnviadoExecucaoSonar.getMethod("getCaminho").invoke(domain);
				String moduloAux = (String) classeObjetoEnviadoExecucaoSonar.getMethod("getChave").invoke(domain);
				String auxNomePainel = (String) classeObjetoEnviadoExecucaoSonar.getMethod("getNomePainel")
						.invoke(domain);

				Long IdObjeto = (Long) classeObjetoEnviadoExecucaoSonar.getMethod("getId").invoke(domain);

				if (!caminho.isEmpty()) {

					try {
						DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
						String dataExecucao = LocalDateTime.now().format(formatter).toString();
						aguardaExecucaoSonar(caminho, moduloAux, auxNomePainel, dataExecucao);
						verificaSeExecucaoFoiConcluidaInspecaoGenerica(nomeClasseSemPacote, caminho, moduloAux,
								IdObjeto, dataExecucao, tipoEntrega);

					} catch (Exception e) {
						System.out.println("Erro ao executar o Sonar " + caminho);
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
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

	private static void copiarArquivos(Path origem, Path destino) throws IOException {

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
				System.out.println("Erro ao copiar pasta " + origem + " para " + destino);
			}

		} else {
			// copiamos o arquivo
			Files.copy(origem, destino);
		}
	}

	private static Process executaSonar(String caminho, String moduloAux, String auxNomePainel, String dataExecucao)
			throws IOException {
		String pathSigla = "cd " + caminho;
		String exeLocalizaEDestroi = "localizaEDestroi_CLI.exe";
		String comandoSonar = "C:/sonar_scan/bin/sonar-scanner.bat -X";/* > LOG/LOG" + LocalDate.now() + ".txt"; */
		String[] cmds = { pathSigla, exeLocalizaEDestroi, comandoSonar };
		String caminhoProperties = criaArquivosUtilidadeParaExecucaoSonar(caminho);
		if (Files.exists(Paths.get(caminhoProperties))) {
			escreveInformacoesPropertiesSonar(caminhoProperties, moduloAux, auxNomePainel, dataExecucao);
			ProcessBuilder builder = new ProcessBuilder("cmd", "/c", String.join("& ", cmds));
			builder.redirectErrorStream(true);
			System.out.println("\n\nExecutando sonar em : " + caminho + "\n\n");
			return builder.start();
		}
		return null;
	}

	private static Process executaSonarComLOG(String caminho, String moduloAux, String auxNomePainel,
			String dataExecucao) throws IOException {
		String pathSigla = "cd " + caminho;
		String exeLocalizaEDestroi = "localizaEDestroi_CLI.exe";
		String comandoSonar = "C:/sonar_scan/bin/sonar-scanner.bat -X > LOG/LOG" + LocalDate.now() + ".txt";
		String[] cmds = { pathSigla, exeLocalizaEDestroi, comandoSonar };
		String caminhoProperties = criaArquivosUtilidadeParaExecucaoSonar(caminho);
		if (Files.exists(Paths.get(caminhoProperties))) {
			escreveInformacoesPropertiesSonar(caminhoProperties, moduloAux, auxNomePainel, dataExecucao);
			ProcessBuilder builder = new ProcessBuilder("cmd", "/c", String.join("& ", cmds));
			builder.redirectErrorStream(true);
			System.out.println("\n\nExecutando sonar em : " + caminho + "\n\n");
			return builder.start();
		}
		return null;
	}

	private static String criaArquivosUtilidadeParaExecucaoSonar(String caminho) throws IOException {
		if (caminho != null && Files.exists(Paths.get(caminho))) {
			String caminhoPastaLog = caminho + "//LOG";
			String caminhoProperties = caminho + "/sonar-project.properties";

			if (!Files.exists(Paths.get(caminhoPastaLog))) {
				Files.createDirectory(Paths.get(caminhoPastaLog));
			}

			if (!Files.exists(Paths.get(caminhoProperties))) {
				Files.createFile(Paths.get(caminhoProperties));
			}

			Path caminhoLocalizaDestroi = new MetodosUteis().caminhoLocalizaEDestroi();
			copiarArquivos(caminhoLocalizaDestroi, Paths.get(caminho));

			Path caminhoTarget = new MetodosUteis().caminhoTarget();
			new File(caminho + "/target/").mkdir();
			copiarArquivos(caminhoTarget, Paths.get(caminho + "/target/"));

			return caminhoProperties;
		} else {
			throw new RuntimeException("Não foi possivel completar a operação!\nCaminho não existe.");

		}
	}

	public static String escreveInformacoesPropertiesSonar(String caminhoPropertiesSonar, String chavePainelSonar,
			String nomePainelSonar, String dataExecucao) {
		Path diretorio = Paths.get(caminhoPropertiesSonar.toString());
		if (!Files.exists(diretorio)) {
			try {
				Files.createFile(diretorio);

			} catch (Exception e) {
				LOG.error("Erro ao criar propreties : " + e.getLocalizedMessage());
			}
		}
		Properties sonarProp = new Properties();
		try (FileInputStream in = new FileInputStream(caminhoPropertiesSonar);
				FileOutputStream out = new FileOutputStream(caminhoPropertiesSonar);) {
			sonarProp.load(new InputStreamReader(in, Charset.forName("UTF-8")));
			sonarProp.put("sonar.projectVersion", dataExecucao);
			sonarProp.put("sonar.sources", ".");
			sonarProp.put("sonar.java.binaries", "target/classes");
			sonarProp.put("sonar.exclusions", "**/*enum,**/*Test.java,**/*test,**/*.properties");
			sonarProp.put("sonar.cfamily.build-wrapper-output.bypass", "true");
			sonarProp.put("sonar.sourceEncoding", "ISO-8859-1");
			sonarProp.put("sonar.projectKey", chavePainelSonar);
			sonarProp.put("sonar.projectName", nomePainelSonar);

			sonarProp.store(out, "Scanner referente ao dia " + dataExecucao);
			out.close();
			in.close();

		} catch (IOException e) {
			LOG.error(e.getMessage());
		}
		return dataExecucao;
	}

	public static void escreveArquivoLOG(String caminho, String texto) {
		Path path = Paths.get(caminho);
		if (!Files.exists(path)) {
			try {
				Files.createFile(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try (OutputStream os = new FileOutputStream(path.toFile(), true); PrintStream ps = new PrintStream(os);) {
			ps.append(texto + "\r\n");
		} catch (IOException e) {
			System.out.println("Erro ao escrever no arquivo: ");
			e.printStackTrace();
		}
	}

	private static void aguardaExecucaoSonar(String caminho, String moduloAux, String auxNomePainel,
			String dataExecucao) throws IOException {

		BufferedReader r = null;
		try {

			Process executaSonar = executaSonar(caminho, moduloAux, auxNomePainel, dataExecucao);
			String caminhoLog = caminho + "/LOG/LOG" + LocalDate.now() + ".txt";
			r = new BufferedReader(new InputStreamReader(executaSonar.getInputStream()));
			Files.deleteIfExists(Paths.get(caminhoLog));
			String linha = r.readLine();
			boolean terminou = false;
			while (!terminou) {
				if (linha != null) {
					System.out.println(caminho + " : " + linha);
					escreveArquivoLOG(caminhoLog, linha);
					if (linha.contains("Final Memory")) {
						terminou = true;
					}
				}
				linha = r.readLine();
			}
			// Thread.sleep adicionado, pois o novo link do SONAR demora a carregar a
			// inspeção no dashboard
			Thread.sleep(60000);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (r != null) {
				r.close();
			}
		}
	}

	private static void verificaSeExecucaoFoiConcluida(String nomeClasseSemPacote, String caminho, String moduloAux,
			Long IdObjeto, String dataExecucao) throws IOException {
		String caminhoLogScannerGeral = "C:/TempCargaRFC/LogScanner/LOG_" + LocalDate.now() + ".txt";
		PrintWriter w = new PrintWriter(new FileWriter(caminhoLogScannerGeral, true));
		ColetaInformacoesSonarBean captura = new ColetaInformacoesSonarBean();
		String ultimaVersaoSonar = captura.getVersao(moduloAux);
		try {
			if (dataExecucao.toString().equals(ultimaVersaoSonar)) {
				Class<?> classeBean = Class.forName("br.com.icresult.bean." + nomeClasseSemPacote + "Bean");
				Object instanciaBean = classeBean.getConstructor().newInstance();
				Runnable runnableCaptura = (Runnable) classeBean.getMethod("getCaptura").invoke(instanciaBean);
				Executors.newFixedThreadPool(1, new YourThreadFactory(IdObjeto)).submit(runnableCaptura);
				w.println("Sonar-Scanner para " + caminho + " concluido com sucesso!");
			} else {
				w.println("Sonar-Scanner para " + caminho + " falhou as " + LocalTime.now() + "!"
						+ ": modulo não subiu pra o Sonar");
			}
		} catch (Exception e) {
			e.printStackTrace();
			w.println("Sonar-Scanner para " + caminho + " falhou as " + LocalTime.now() + "!");
		} finally {
			if (w != null) {
				w.close();
			}
		}
	}

	private static void verificaSeExecucaoFoiConcluidaInspecaoGenerica(String nomeClasseSemPacote, String caminho,
			String moduloAux, Long IdObjeto, String dataExecucao, String tipoEntrega) throws IOException {
		String caminhoLogScannerGeral = "C:/TempCargaRFC/LogScanner/LOG_" + LocalDate.now() + ".txt";
		PrintWriter w = new PrintWriter(new FileWriter(caminhoLogScannerGeral, true));

		try {
			Thread.sleep(60000);
			ColetaInformacoesSonarBean captura = new ColetaInformacoesSonarBean();
			String ultimaVersaoSonar = captura.getVersao(moduloAux);
			System.out.println("Ultima versão: " + ultimaVersaoSonar + "DataExecução: " + dataExecucao.toString());
			if (dataExecucao.toString().equals(ultimaVersaoSonar)) {
				Class<?> classeBean = Class.forName("br.com.icresult.bean." + nomeClasseSemPacote + "Bean");
				Object instanciaBean = classeBean.getConstructor().newInstance();
				Runnable runnableCaptura = (Runnable) classeBean.getMethod("getCaptura").invoke(instanciaBean);
				Executors.newFixedThreadPool(1, new YourThreadFactory(IdObjeto + "-" + tipoEntrega))
						.submit(runnableCaptura);
				w.println("Sonar-Scanner para " + caminho + " concluido com sucesso!");
			} else {
				w.println("Sonar-Scanner para " + caminho + " falhou as " + LocalTime.now() + "!"
						+ ": modulo não subiu pra o Sonar");
			}
		} catch (Exception e) {
			e.printStackTrace();
			w.println("Sonar-Scanner para " + caminho + " falhou as " + LocalTime.now() + "!");
		} finally {
			if (w != null) {
				w.close();
			}
		}
	}

	private static void verificaSeExecucaoFoiConcluidaMensal(String nomeClasseSemPacote, String caminho,
			String moduloAux, Long IdObjeto, String dataExecucao) throws IOException {
		String caminhoLogScannerGeral = "C:/TempCargaRFC/LogScanner/LOG_" + LocalDate.now() + ".txt";
		PrintWriter w = new PrintWriter(new FileWriter(caminhoLogScannerGeral, true));
		ColetaInformacoesSonarBean captura = new ColetaInformacoesSonarBean();
		String ultimaVersaoSonar = captura.getVersao(moduloAux);
		try {
			if (dataExecucao.toString().equals(ultimaVersaoSonar)) {
				Class<?> classeBean = Class.forName("br.com.icresult.bean." + nomeClasseSemPacote + "Bean");
				Object instanciaBean = classeBean.getConstructor().newInstance();
				Runnable runnableCaptura = (Runnable) classeBean.getMethod("getCapturaMensal").invoke(instanciaBean);
				Executors.newFixedThreadPool(1, new YourThreadFactory(IdObjeto)).submit(runnableCaptura);
				w.println("Sonar-Scanner para " + caminho + " concluido com sucesso!");
			} else {
				w.println("Sonar-Scanner para " + caminho + " falhou as " + LocalTime.now() + "!");
			}
		} catch (Exception e) {
			w.println("Sonar-Scanner para " + caminho + " falhou as " + LocalTime.now() + "!");
		} finally {
			if (w != null) {
				w.close();
			}
		}
	}

}
