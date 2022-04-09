package br.com.icresult.bean;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import javax.faces.bean.ManagedBean;

import org.omnifaces.util.Messages;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 * -Classe de captura de eventos do FileUpload PrimeFaces
 * 
 * @author helio.franca
 * @version v2.3.4
 * @since 25-09-2018
 *
 */

@ManagedBean
public class FileUploadView {

	static final String PASTASISTEMA = "C:\\TempCargaRFC";
	static final String PASTASISTEMACAMINHO = "C:/TempCargaRFC/";

	/**
	 * Copia o arquivo selecionado em uma nova pasta e retorna o caminho do arquivo.
	 * 
	 * @param arquivo - UploadedFile
	 * @author helio.franca
	 * @since 25-09-2018
	 */
	// -----------------------------------------------------------------------------------
	public String retornoUploadFile(UploadedFile arquivo) {
		String caminho = "N/A";
		// Cria um arquivo UploadFile, para receber o arquivo do evento
		try (InputStream in = new BufferedInputStream(arquivo.getInputstream())) {

			// copiar para pasta do projeto
			File file = new File(PASTASISTEMACAMINHO + arquivo.getFileName());
			// O método file.getAbsolutePath() fornece o caminho do arquivo criado
			// Pode ser usado para ligar algum objeto do banco ao arquivo enviado
			caminho = file.getAbsolutePath();

			try (FileOutputStream fout = new FileOutputStream(file)) {
				while (in.available() != 0) {
					fout.write(in.read());
				}
			}

		} catch (Exception ex) {
			Messages.addGlobalError("Falha ao carregar arquivo:");
		}

		return caminho;
	}

	/**
	 * handleFileUpload p/ RFC
	 * 
	 * @param event - Evento
	 */
	// -----------------------------------------------------------------------------------
	public void handleFileUpload(FileUploadEvent event) {
		// Cria Pasta tempCarga caso não exista
		new File(PASTASISTEMA).mkdir();
		UploadedFile arq = event.getFile();
		RFCBean.CAMINHO = retornoUploadFile(arq);
		RFCBean bean = new RFCBean();
		bean.salvarPlanilha();
	}

	/**
	 * handleFileUpload p/ Controle de Siglas
	 * 
	 * @param event - Evento
	 */
	// ---------------------------------------------------------------------
	public void handleFileUploadControle(FileUploadEvent event) {
		new File(PASTASISTEMA).mkdir();
		UploadedFile arq = event.getFile();
		ControleSiglasBean.CAMINHO = retornoUploadFile(arq);
		ControleSiglasBean bean = new ControleSiglasBean();
		bean.salvarPlanilha();
	}

	/**
	 * handleFileUpload p/ controle git hk
	 * 
	 * @param event - Evento
	 */
	// -----------------------------------------------------------------------------
	public void handleFileUploadGitHK(FileUploadEvent event) {
		new File(PASTASISTEMA).mkdir();
		UploadedFile arq = event.getFile();
		ControleGitHKBean.CAMINHO = retornoUploadFile(arq);
		ControleGitHKBean bean = new ControleGitHKBean();
		bean.salvarPlanilha();
	}

	/**
	 * handleFileUpload p/ controle git da parte genérica
	 * 
	 * @param event - Evento
	 */
	// -----------------------------------------------------------------------------
	public void handleFileUploadGitGenerico(FileUploadEvent event) {
		new File(PASTASISTEMA).mkdir();
		UploadedFile arq = event.getFile();
		ControleGitSonarBean bean = new ControleGitSonarBean();
		bean.salvarPlanilha(retornoUploadFile(arq));
	}

	/**
	 * handleFileUpload p/ controle devOps da parte genérica
	 * 
	 * @param event - Evento
	 */
	// -----------------------------------------------------------------------------
	public void handleFileUploadDevOpsGenerico(FileUploadEvent event) {
		new File(PASTASISTEMA).mkdir();
		UploadedFile arq = event.getFile();
		DevOpsGenericoBean bean = new DevOpsGenericoBean();
		bean.salvarPlanilha(retornoUploadFile(arq));
	}
	
	/**
	 * handleFileUpload p/ controle de execuções do pipeline do jenkins
	 * 
	 * @param event - Evento
	 */
	// -----------------------------------------------------------------------------
	public void handleFileUploadExecucoesJenkins(FileUploadEvent event) {
		new File(PASTASISTEMA).mkdir();
		UploadedFile arq = event.getFile();
		DevOpsJenkinsBean bean = new DevOpsJenkinsBean();
		bean.salvarPlanilha(retornoUploadFile(arq));
	}

	/**
	 * handleFileUpload p/ controle rtc da parte genérica
	 * 
	 * @param event - Evento
	 */
	// -----------------------------------------------------------------------------
	public void handleFileUploadRtcGenerico(FileUploadEvent event) {
		new File(PASTASISTEMA).mkdir();
		UploadedFile arq = event.getFile();
		ControleRtcSonarBean bean = new ControleRtcSonarBean();
		bean.salvarPlanilha(retornoUploadFile(arq));
	}

	/**
	 * handleFileUpload p/ Controle Git Dev
	 * 
	 * @param event - Evento
	 */
	// -----------------------------------------------------------------------------
	public void handleFileUploadGitDev(FileUploadEvent event) {
		new File(PASTASISTEMA).mkdir();
		UploadedFile arq = event.getFile();
		ControleGitDevBean.CAMINHO = retornoUploadFile(arq);
		ControleGitDevBean bean = new ControleGitDevBean();
		bean.salvarPlanilha();
	}

	/**
	 * handleFileUpload p/ Controle RTC Dev
	 * 
	 * @param event - Evento
	 */
	// -----------------------------------------------------------------------------
	public void handleFileUploadRtcDev(FileUploadEvent event) {
		new File(PASTASISTEMA).mkdir();
		UploadedFile arq = event.getFile();
		ControleRtcDevBean.CAMINHO = retornoUploadFile(arq);
		ControleRtcDevBean bean = new ControleRtcDevBean();
		bean.salvarPlanilha();
	}

	/**
	 * handleFileUpload p/ Controle RTC HK
	 * 
	 * @param event - Evento
	 */
	// -----------------------------------------------------------------------------
	public void handleFileUploadRtcHK(FileUploadEvent event) {
		new File(PASTASISTEMA).mkdir();
		UploadedFile arq = event.getFile();
		ControleRtcHKBean.CAMINHO = retornoUploadFile(arq);
		ControleRtcHKBean bean = new ControleRtcHKBean();
		bean.salvarPlanilha();
	}

	/**
	 * handleFileUpload p/ Siglas Git Dev
	 * 
	 * @param event - Evento
	 */
	public void handleFileUploadSiglasGitDev(FileUploadEvent event) {
		new File(PASTASISTEMA).mkdir();
		UploadedFile arq = event.getFile();
		SiglasGitBean bean = new SiglasGitBean();
		bean.salvarPlanilha(retornoUploadFile(arq));

	}

	/**
	 * handleFileUploadGitMensal p/ ControleGitDevMensal
	 * 
	 * @param event - Evento
	 */
	public void handleFileUploadGitMensal(FileUploadEvent event) {
		new File(PASTASISTEMA).mkdir();
		UploadedFile arq = event.getFile();
		ControleGitDevMensalBean.CAMINHO = retornoUploadFile(arq);
		ControleGitDevMensalBean bean = new ControleGitDevMensalBean();
		bean.salvarPlanilha();
	}
	
	/**
	 * handleFileUploadListaCloudBees p/ ListaCloudBees
	 * 
	 * @param event - Evento
	 */
	public void handleFileUploadListaCloudBees(FileUploadEvent event) {
		new File(PASTASISTEMA).mkdir();
		UploadedFile arq = event.getFile();
		ListaCloudBeesBean.CAMINHO = retornoUploadFile(arq);
		ListaCloudBeesBean bean = new ListaCloudBeesBean();
		bean.salvarPlanilha();
	}
	
	/**
	 * handleFileUploadListaSonar p/ PainelDoSonarListaGeral
	 * 
	 * @param event - Evento
	 */
	public void handleFileUploadListaSonar(FileUploadEvent event) {
		new File(PASTASISTEMA).mkdir();
		UploadedFile arq = event.getFile();
		PainelDoSonarListaGeralBean.CAMINHO = retornoUploadFile(arq);
		PainelDoSonarListaGeralBean bean = new PainelDoSonarListaGeralBean();
		bean.salvarPlanilha();
	}

}