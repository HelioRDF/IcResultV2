package br.com.icresult.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.omnifaces.util.Faces;
import org.omnifaces.util.Messages;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.com.icresult.bean.ControleRtcDevBean;
import br.com.icresult.domain.complementos.Config;
import jxl.common.Logger;

public class MetodosUteis {

	private static Logger LOG = Logger.getLogger(ControleRtcDevBean.class);

	/**
	 * 
	 * Copia LocalizaEDestroi, pasta target e pasta para o disco C:
	 * 
	 */
	public void copiaArquivosNecessarios() {
		try {
			File pastaLocalizaEDestroi = new File("C:/localiza/");
			Boolean notExistsLocaliza = Files.notExists(Paths.get(pastaLocalizaEDestroi.getPath()));
			if (notExistsLocaliza) {
				pastaLocalizaEDestroi.mkdir();
				copiarArquivos(Paths.get(Faces.getRealPath("/lib/localiza")), Paths.get("C:/localiza/"));
			}
			File pastaTarget = caminhoTarget().toFile();
			if (!pastaTarget.exists()) {
				pastaTarget.mkdir();
				copiarArquivos(Paths.get(Faces.getRealPath("/lib/target")), caminhoTarget());
			}
			File pastaSonarScannerCli = new File("C:/sonar_scan/");
			if (!pastaSonarScannerCli.exists()) {
				pastaSonarScannerCli.mkdir();
				copiarArquivos(Paths.get(Faces.getRealPath("/lib/sonar_scan")), pastaSonarScannerCli.toPath());
			}
			File pastaPropertiesRTC = new File(caminhoPropertiesRTC().toUri());
			File diretorioPropertiesRTC = new File(pastaPropertiesRTC.getParent());
			boolean diretorioPropertiesRtcExiste = !diretorioPropertiesRTC.exists();
			if (diretorioPropertiesRtcExiste) {
				diretorioPropertiesRTC.mkdir();
				copiarArquivos(Paths.get(Faces.getRealPath("/lib/pass.properties")), pastaPropertiesRTC.toPath());
			}

		} catch (IOException e) {
			LOG.error("Erro ao copiar arquivos de inicialização");
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

	/**
	 * @return - Path do caminho onde se localiza a pasta target a ser copiada para
	 *         dentro dos projeto que serão executados no Sonar
	 */
	public Path caminhoTarget() {
		return Paths.get("C:/target/");
	}

	/**
	 * @return retorna um excel de acordo com o caminho recebido;
	 */
	public StreamedContent getExcelComoStreamedContent(String caminhoDoArquivoDentroAplicacao, String nomeArquivo) {
		StreamedContent arquivo = null;
		try {
			String caminhoArquivo = Faces.getRealPath(caminhoDoArquivoDentroAplicacao);
			arquivo = new DefaultStreamedContent(new FileInputStream(new File(caminhoArquivo)),
					"application/vnd.ms-excel", nomeArquivo);
		} catch (Exception e) {
			Messages.addGlobalError("Erro ao exportar arquivo");
			e.printStackTrace();
		}
		return arquivo;
	}

	public Path caminhoLocalizaEDestroi() {
		return Paths.get("C:/localiza/");
	}

	public Path caminhoPropertiesRTC() {
		return Paths.get("C:/properties_rtc/pass.properties");
	}

	/**
	 * Utilizado para inserir as configurações do Sonar no arquivo
	 * "C:/sonar_scan/conf/sonar-scanner.properties"
	 * 
	 * @param configuracaoSonar - Configurações que serão inseridas, no objeto
	 *                          Config é necessário possui a url, senha e login
	 */
	public void inserirInformacoesPropertiesSONARCli(Config configuracaoSonar) {
		String caminhoProperties = "C:/sonar_scan/conf/sonar-scanner.properties";
		try (FileInputStream in = new FileInputStream(caminhoProperties);
				FileOutputStream out = new FileOutputStream(caminhoProperties);) {
			Properties sonarProp = new Properties();
			sonarProp.load(new InputStreamReader(in, Charset.forName("UTF-8")));
			sonarProp.put("sonar.host.url", configuracaoSonar.getUrl());
			sonarProp.put("sonar.login", configuracaoSonar.getLogin());
			sonarProp.put("sonar.password", configuracaoSonar.getAcessoSonar());
			sonarProp.put("sonar.ws.timeout", "300");
			sonarProp.put("sonar.cfamily.build-wrapper-output.bypass", "true");
			sonarProp.put("sonar.ce.workerCount", "5");
			sonarProp.put("sonar.ce.javaOpts", "-Xmx4G");
			sonarProp.put("sonar.web.javaOpts", "Xmx4G");

			sonarProp.store(out, "#Configurações para utilizar o scan do sonar\r\n");

		} catch (IOException e) {
			LOG.error(e.getMessage());
		}
	}

	public String verificaLinkSONAR(String urlAPI) {
		// return urlAPI.contains("https") ? urlAPI : urlAPI.replace("http", "https");
		return urlAPI;
	}
}
