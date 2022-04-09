package br.com.icresult.bean;

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;

import org.omnifaces.util.Messages;

import br.com.icresult.dao.complementos.RFCDAO;
import br.com.icresult.domain.complementos.RFC;
import br.com.icresult.email.EnviarEmail;
import br.com.icresult.email.InspecaoList;
import br.com.icresult.tratamentos.TrataDados;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

/**
 * -Classe BEAN RFCBean.
 * 
 * @author helio.franca
 * @version v2.4.0
 * @since 27-09-2018
 *
 */

@SuppressWarnings("serial")
@ManagedBean
@SessionScoped
public class RFCBean implements Serializable {


	private RFC rfc;
	private RFCDAO daoRFC;
	private List<RFC> listaRFC;
	private int total;
	static String CAMINHO = "";
	private String siglaAtual;
	private boolean enviarEmailTemp;
	private Date filtrarDataEmail = Date.from(LocalDateTime.now().atZone(ZoneId.of("Etc/GMT+3")).toInstant());
	private String chaveEMail;
	private String origemEMail;
	private String destinoEMail;

	/**
	 * Dispara o envio de E-mail
	 */
	// -------------------------------------------------------------------------------------
	public void enviarEmail() {

		EnviarEmail email = new EnviarEmail();
		daoRFC = new RFCDAO();
		String resultado = "";
		List<RFC> rfcs;
		String nomeLiderAnt = "Novo";
		// filtrarDataEmail
		rfcs = daoRFC.listarRfcPorData(filtrarDataEmail);

		for (RFC obj : rfcs) {
			try {
				if (!nomeLiderAnt.equalsIgnoreCase(obj.getLider()) && !nomeLiderAnt.equalsIgnoreCase("novo")) {
					resultado += TrataDados.linhaColunaHtml();
					resultado += TrataDados.tituloColunaHtml();
				}

				InspecaoList list = new InspecaoList();
				resultado += list.alertaInspecao(obj);
				nomeLiderAnt = obj.getLider();

			} catch (Exception e) {
				Messages.addGlobalError("Erro enviarEmail()");
			}
		}
		email.emailHtmlInspecao(resultado, "Monitor de Inspeção/RFC", chaveEMail, origemEMail, destinoEMail);
	}

	/**
	 * Salvar objeto do tipo RFC
	 */
	// -------------------------------------------------------------------------------------
	public void salvar() {
		try {
			daoRFC.salvar(rfc);
			Messages.addGlobalInfo(siglaAtual + " - Salva | Inspeção:" + rfc.getCodInspecao());
		} catch (Exception e) {
			Messages.addGlobalError("Não foi possível salvar a Silga:" + siglaAtual);
		}
	}

	/**
	 * Cria uma nova instância de RFC e RFCDAO
	 */
	// -------------------------------------------------------------------------------------------
	public void novo() {
		rfc = new RFC();
		daoRFC = new RFCDAO();
	}

	/**
	 * Edita objeto selecionado
	 */
	// -------------------------------------------------------------------------------------------
	public void editar() {
		try {
			Messages.addGlobalInfo(" Editado com sucesso!!!");
		} catch (Exception e) {
			Messages.addGlobalError("Erro ao Editar ");
		}
	}

	/**
	 * Captura as informações de uma planilha xls e salva no banco de dados
	 * 
	 */
	// -------------------------------------------------------------------------------------------
	public void salvarPlanilha() {
		rfc = new RFC();
		daoRFC = new RFCDAO();
		String codRfc, codProj, sigla, observacao, status, codVazio, codInspecao, lider, emailLider, gestorEntrega,
				dataPro;
		String dataCadastro = "";
		String dataInspecao = "";
		Date dateC = Date.from(LocalDateTime.now().atZone(ZoneId.of("Etc/GMT+3")).toInstant());
		Date dateI = Date.from(LocalDateTime.now().atZone(ZoneId.of("Etc/GMT+3")).toInstant());
		String salvarSigla;
		int setCodInspecaoInt = 0;

		try {
			// Carrega a planilha
			Workbook workbook = null;
			workbook = Workbook.getWorkbook(new File(CAMINHO));

			// Seleciona a aba do excel
			Sheet sheet = workbook.getSheet(0);
			// Numero de linhas com dados do xls
			int linhas = sheet.getRows();
			for (int i = 1; i < linhas; i++) {
				Cell celulaRFC = sheet.getCell(1, i); // coluna 1 -COD_RFC
				Cell celulaCodProj = sheet.getCell(2, i); // coluna 2 - COD_PROJ
				Cell celulaSigla = sheet.getCell(3, i); // coluna 3 - SIGLA
				Cell celulaLider = sheet.getCell(4, i); // coluna 4 - Lider QA
				Cell celulaGestorEntrega = sheet.getCell(5, i); // coluna 5 - Novo gestorEntrega
				Cell celulaCodInsp = sheet.getCell(6, i); // coluna 6 - Cod Inspeção
				Cell celulaDataC = sheet.getCell(7, i); // coluna 7 - DATA_CADASTRO
				Cell celulaDataI = sheet.getCell(8, i); // coluna 8 - DATA_INSPECAO
				Cell celulaObs = sheet.getCell(9, i); // coluna 9 - OBSERVACAO
				Cell celulaStatus = sheet.getCell(10, i); // coluna 10 - STATUS
				Cell celulaCodVazio = sheet.getCell(11, i); // coluna 11 - Cod_Vazio
				Cell celula11 = sheet.getCell(12, i); // coluna 12 - Salvar no Banco
				Cell celula13 = sheet.getCell(13, i); // coluna 13 - Email Lider
				Cell celula14 = sheet.getCell(14, i); // coluna 14 - Data_Pro

				codRfc = celulaRFC.getContents().toString().trim().toUpperCase(); // Coluna 1:COD_RFC
				codProj = celulaCodProj.getContents().toString().trim().toUpperCase(); // Coluna 2:COD_PROJ
				sigla = celulaSigla.getContents().toString().trim().toUpperCase(); // Coluna 3:SIGLA
				dataCadastro = celulaDataC.getContents().toString().trim().toUpperCase(); // Coluna 4:DATA_CADASTRO DB
				dataInspecao = celulaDataI.getContents().toString().trim().toUpperCase(); // Coluna 5:DATA_INSPECAO
				observacao = celulaObs.getContents().toString().trim().toUpperCase(); // Coluna 6:OBSERVACAO
				status = celulaStatus.getContents().toString().trim().toUpperCase(); // Coluna 7:STATUS
				codVazio = celulaCodVazio.getContents().toString().trim().toUpperCase(); // Coluna 8:Cod_Vazio
				codInspecao = celulaCodInsp.getContents().toString().trim().toUpperCase(); // Coluna 9:Cod_Inspeção
																							// Anterior
				salvarSigla = celula11.getContents().toString().trim().toUpperCase();// Coluna 11:Salvar no Banco

				lider = celulaLider.getContents().toString().trim().toUpperCase();// Lider
				gestorEntrega = celulaGestorEntrega.getContents().toString().trim().toUpperCase();// Lider
				emailLider = celula13.getContents().toString().trim().toUpperCase();// Email Lider
				dataPro = celula14.getContents().toString().trim().toUpperCase();// Data_Pro

				// Encerra a leitura quando encontra linha vazia
				if (sigla.isEmpty()) {
					break;
				}
				try {
					setCodInspecaoInt = Integer.parseInt(codInspecao);
				} catch (Exception e) {
					setCodInspecaoInt = 0;
				}

				if (!salvarSigla.isEmpty()) {
					dateC = validadorData(dataCadastro, "Data Cadastro");
					dateI = validadorData(dataInspecao, "Data Inspeção");
					rfc.setInspecionar(validarInspecao(codVazio, status));
					rfc.setCodRfc(codRfc);
					rfc.setCodProj(codProj);
					rfc.setSigla(sigla);
					rfc.setDataCadastro(dateC);
					rfc.setDataInspecao(dateI);
					rfc.setObservacao(observacao);
					rfc.setStatus(status);
					rfc.setCodVazio(codVazio);
					rfc.setCodInspecao(setCodInspecaoInt);
					rfc.setLider(lider);
					rfc.setGestorEntrega(gestorEntrega);
					rfc.setEmailLider(emailLider);
					rfc.setDataPro(dataPro);
					siglaAtual = sigla;
					salvar();
				}
			}
		} catch (NullPointerException e) {
			Messages.addGlobalError("Erro");

		} catch (Exception e) {
			Messages.addGlobalError("Erro");
		}
	}

	/**
	 * Seleciona uma RFC da table
	 * 
	 * @param evento - Evento
	 */
	// -------------------------------------------------------------------------------------------
	public void selecionarRFC(ActionEvent evento) {
		try {
			rfc = (RFC) evento.getComponent().getAttributes().get("meuSelect");
		} catch (Exception e) {
			Messages.addGlobalError("Erro ao Editar: ");
		}
	}

	/**
	 * Lista os objetos do tipo RFC
	 */
	// ------------------------------------------------------------------------------------------------------------------------------------------------------
	public void listarInfos() {
		try {
			daoRFC = new RFCDAO();
			listaRFC = daoRFC.listar();
			total = listaRFC.size();
			Messages.addGlobalInfo("Lista Atualizada!");

		} catch (Exception e) {
			Messages.addGlobalError("Erro ao  Atualizar Lista.");
		}
	}

	/**
	 * Valida e formata um objeto do tipo Data
	 * 
	 * @param dataInfo - Date
	 * @param msg      - String
	 * @return - Date
	 */
	// ------------------------------------------------------------------------------------------------------------------------------------------------------
	public Date validadorData(String dataInfo, String msg) {
		DateFormat formatter = new SimpleDateFormat("dd/MM/yy");
		Date dataFinal = Date.from(LocalDateTime.now().atZone(ZoneId.of("Etc/GMT+3")).toInstant());
		if (dataInfo.isEmpty()) {
			dataFinal = null;
		} else {
			try {
				dataFinal = (Date) formatter.parse(dataInfo);
			} catch (ParseException e) {
				dataFinal = null;
			}
		}
		return dataFinal;
	}

	/**
	 * Valida se RFC deve ser inspecionada
	 * 
	 * @param vazio  - String
	 * @param status - String
	 * @return - String
	 */
	// ------------------------------------------------------------------------------------------------------------------------------------------------------
	public String validarInspecao(String vazio, String status) {
		String resultado = "NÃO";
		if (vazio.equalsIgnoreCase("false") && status.equalsIgnoreCase("ativa")) {
			resultado = "SIM";
		}
		System.out.println("--------------Inspecionar? ------- " + resultado);
		return resultado;
	}

	// Get e Set
	// ------------------------------------------------------------------------------------------------------------------------------------------------------
	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public String getCaminho() {
		return CAMINHO;
	}

	public void setCaminho(String caminho) {
		RFCBean.CAMINHO = caminho;
	}

	public List<RFC> getListaRFC() {
		return listaRFC;
	}

	public void setListaRFC(List<RFC> listaRFC) {
		this.listaRFC = listaRFC;
	}

	public RFC getrFC() {
		return rfc;
	}

	public void setrFC(RFC rFC) {
		this.rfc = rFC;
	}

	public Date getFiltrarDataEmail() {
		return filtrarDataEmail;
	}

	public void setFiltrarDataEmail(Date filtrarDataEmail) {
		this.filtrarDataEmail = filtrarDataEmail;
	}

	public boolean isEnviarEmailTemp() {
		return enviarEmailTemp;
	}

	public void setEnviarEmailTemp(boolean enviarEmailTemp) {
		this.enviarEmailTemp = enviarEmailTemp;
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

}